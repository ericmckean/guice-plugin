/**
 * Copyright (C) 2007 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.inject.tools.suite.snippets;

import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.google.inject.Module;
import com.google.inject.tools.suite.snippets.bindings.BindingRepresentation;
import com.google.inject.tools.suite.snippets.bindings.InjectorRepresentation;
import com.google.inject.tools.suite.snippets.bindings.KeyRepresentation;
import com.google.inject.tools.suite.snippets.problems.CodeProblem;
import com.google.inject.tools.suite.snippets.problems.InvalidModuleProblem;

/**
 * This code snippet runs a module context. It creates an injector based on the
 * context information and finds all its bindings and any problems with creating
 * the injector.
 * 
 * @author Darren Creutz (dcreutz@gmail.com)
 */
public class ModuleContextSnippet extends CodeSnippet {
  /**
   * The result of a module context snippet, containing all the information
   * about the context.
   */
  public static class ModuleContextResult extends CodeSnippetResult {
    private static final long serialVersionUID = -1832891974235767711L;
    
    private final String name;
    private final InjectorRepresentation injector;
    private final Set<String> modules;

    public ModuleContextResult(String name,
        Set<ModuleRepresentation> moduleReps,
        Set<CodeProblem> problems) {
      super(problems);
      this.name = name;
      this.modules = new HashSet<String>();
      Set<Module> moduleInstances = new HashSet<Module>();
      if (moduleReps != null) {
        for (ModuleRepresentation module : moduleReps) {
          this.modules.add(module.getName());
          try {
            moduleInstances.add(module.getInstance());
          } catch (Throwable throwable) {
            problems.add(new InvalidModuleProblem(module, throwable));
          }
        }
      }
      injector = new InjectorRepresentation(moduleInstances);
      problems.addAll(injector.problems());
    }

    public String getName() {
      return name;
    }

    public Set<String> getModules() {
      return modules;
    }

    public InjectorRepresentation getInjector() {
      return injector;
    }
    
    @Override
    public Set<? extends CodeProblem> getAllProblems() {
      Set<CodeProblem> problems = new HashSet<CodeProblem>(this.problems);
      problems.addAll(injector.problems());
      for (KeyRepresentation key : injector.bindings().keySet()) {
        problems.addAll(key.problems());
      }
      for (BindingRepresentation binding : injector.bindings().values()) {
        problems.addAll(binding.problems());
      }
      return problems;
    }
  }

  /**
   * Represents a module in this context internally.
   */
  public static class ModuleRepresentation {
    private final Class<? extends Module> moduleClass;
    private final List<Class<?>> argTypes;
    private final List<String> argValues;
    private Module instance;

    public ModuleRepresentation(Class<? extends Module> moduleClass,
        List<Class<?>> argTypes, List<String> argValues) {
      this.moduleClass = moduleClass;
      this.argTypes = argTypes != null ? argTypes : new ArrayList<Class<?>>();
      this.argValues = argValues != null ? argValues : new ArrayList<String>();
      this.instance = null;
    }
    
    public ModuleRepresentation(Module module) {
      this.moduleClass = module.getClass();
      this.argTypes = null;
      this.argValues = null;
      this.instance = module;
    }

    public Module getInstance() throws IllegalAccessException,
        InvocationTargetException, NoSuchMethodException,
        InstantiationException {
      if (instance == null) {
        Class<?>[] argumentTypes = new Class<?>[argTypes.size()];
        Object[] argumentValues = new Object[argTypes.size()];
        int i = 0;
        for (Class<?> argType : argTypes) {
          argumentTypes[i] = argType;
          i++;
        }
        instance = moduleClass.getConstructor(argumentTypes).newInstance(argumentValues);
      }
      return instance;
    }

    public String getName() {
      return moduleClass.getName();
    }

    public List<String> getArgValues() {
      return argValues;
    }
  }

  private final String name;
  private final Set<ModuleRepresentation> modules;

  /**
   * Create a ModuleContextSnippet with the given modules.
   */
  public ModuleContextSnippet(Set<ModuleRepresentation> modules, String name) {
    super();
    this.modules = modules;
    this.name = name;
  }

  /**
   * Return the name of the context.
   */
  public String getName() {
    return name;
  }

  @Override
  public ModuleContextResult getResult() {
    return new ModuleContextResult(name, modules, problems);
  }

  /**
   * Runs the module context by parsing the arguments and creating a snippet,
   * running it and then printing the result to System.out as an object.
   * 
   * @param args the arguments: args[0] is the name of the context, args[1] is
   *        the number of modules, args[2] is the class of the first module,
   *        args[3] thru args[n] are the arguments for the first module, etc.
   */
  // Expects 1+n args, args[0] is context name, args[1] is number of modules,
  // args[2] is first module, etc.
  // all modules must have default constructors
  // if args[1] == -1 then we have a custom module
  // then args[2] is the name of a class with a default constructor
  // and args[3] is the name of a method in that class that takes no arguments
  // and returns an iterable of modules Iterable<com.google.inject.Module>
  // and args[4] is an optional method that returns an injector
  public static void main(String[] args) {
    OutputStream realSystemOut = System.out;
    System.setOut(System.err);
    runSnippet(realSystemOut, args);
  }

  @SuppressWarnings("unchecked")
  public static void runSnippet(OutputStream stream, String[] args) {
    ModuleContextSnippet snippet = null;
    String contextName = "Bad context name";
    try {
      Set<ModuleRepresentation> modules = new HashSet<ModuleRepresentation>();
      List<String> argsSet = new ArrayList<String>();
      for (String arg : args) {
        argsSet.add(arg);
      }
      Iterator<String> arguments = argsSet.iterator();
      contextName = arguments.next();
      int numModules = Integer.valueOf(arguments.next());
      if (numModules >= 0) {
        for (int i = 0; i < numModules; i++) {
          try {
            Class<?> aClass = Class.forName(arguments.next());
            aClass.asSubclass(Module.class);
            Class<? extends Module> moduleClass =
              (Class<? extends Module>) aClass;
            int numArgs = Integer.valueOf(arguments.next());
            List<Class<?>> argTypes = new ArrayList<Class<?>>();
            List<String> argValues = new ArrayList<String>();
            if (numArgs > 0) {
              for (int j = 0; j < numArgs; j++) {
                argTypes.add(Class.forName(arguments.next()));
                argValues.add(arguments.next());
              }
            }
            modules.add(new ModuleRepresentation(moduleClass, argTypes,
                argValues));
          } catch (Exception e) {
            i = numModules;
          }
        }
        snippet = new ModuleContextSnippet(modules, contextName);
      } else {
        Class<?> classToUse = Class.forName(arguments.next());
        Set<ModuleRepresentation> themodules = new HashSet<ModuleRepresentation>();
        Iterable<Module> moduleInstances;
        if (isIterableModule(classToUse)) {
          moduleInstances = (Iterable<Module>)classToUse.newInstance();
        } else {
          Method methodToCall =
            classToUse.getMethod(arguments.next(), (Class[]) null);
          Object result;
          if (Modifier.isStatic(methodToCall.getModifiers())) {
            result = methodToCall.invoke(null, (Object[])null);
          } else {
            result = methodToCall.invoke(classToUse.newInstance(), (Object[]) null);
          }
          moduleInstances = (Iterable<Module>)result;
        }
        for (Module module : moduleInstances) {
          themodules.add(new ModuleRepresentation(module));
        }
        snippet = new ModuleContextSnippet(themodules, contextName);
      }
    } catch (Throwable t) {
      if (snippet == null) {
        snippet =
          new ModuleContextSnippet(new HashSet<ModuleRepresentation>(), contextName);
      }
      snippet.addProblems(Collections.singleton(new CodeProblem(t)));
    }
    snippet.printResult(stream);
  }
  
  private static boolean isIterableModule(Type type) {
    if (type instanceof ParameterizedType) {
      ParameterizedType ptype = (ParameterizedType)type;
      if (ptype.getRawType() instanceof Class<?>
        && Iterable.class.isAssignableFrom((Class<?>)ptype.getRawType())) {
        Type[] args = ptype.getActualTypeArguments();
        if (args.length == 1 && args[0] instanceof Class<?>
          && Module.class.isAssignableFrom((Class<?>)args[0])) {
          return true;
        }
      }
    }
    return false;
  }
  
  private static boolean isIterableModule(Class<?> aClass) {
    try {
      Type type = aClass.getGenericSuperclass();
      if (isIterableModule(type)) return true;
      for (Type atype : aClass.getGenericInterfaces()) {
        if (isIterableModule(atype)) return true;
      }
      if (aClass.getSuperclass() != null) {
        if (isIterableModule(aClass.getSuperclass())) return true;
      }
      if (aClass.getInterfaces().length > 0) {
        for (Class<?> superclass : aClass.getInterfaces()) {
          if (isIterableModule(superclass)) return true;
        }
      }
    } catch (Throwable e) {}
    return false;
  }
}
