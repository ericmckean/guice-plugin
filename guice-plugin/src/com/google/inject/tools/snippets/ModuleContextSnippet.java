/**
 * Copyright (C) 2007 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.inject.tools.snippets;

import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.google.inject.Binding;
import com.google.inject.CreationException;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;

/**
 * This code snippet runs a module context.  It creates an injector based on the context information
 * and finds all its bindings and any problems with creating the injector.
 * 
 * @author Darren Creutz <dcreutz@gmail.com>
 */
public class ModuleContextSnippet extends CodeSnippet {
  /**
   * The result of a module context snippet, containing all the information about the context.
   */
  public static class ModuleContextResult extends CodeSnippetResult {
    private final String name;
    private final Map<String,BindingCodeLocation> bindings;
    public ModuleContextResult(String name,Map<Key<?>,Binding<?>> moduleBindings,Set<? extends CodeProblem> problems) {
      super(problems);
      this.name = name;
      this.bindings = new HashMap<String,BindingCodeLocation>();
      if (moduleBindings!=null) {
        for (Key<?> key : moduleBindings.keySet()) {
          //TODO: type literals, int/Integer, etc.
          final String bindWhat = key.getTypeLiteral().toString();
          String bindTo = null;
          String file = null;
          int location = -1;
          Binding<?> binding = moduleBindings.get(key);
          Set<CodeProblem> locationProblems = new HashSet<CodeProblem>();
          BindingCodeLocation bindingCodeLocation;
          StackTraceElement source = null;
          if (binding == null) {
            locationProblems.add(new CodeProblem.NoBindingProblem(name,key.getTypeLiteral().toString()));
          } else {
            try {
              bindTo = binding.getProvider().get().getClass().getName();
              source = (StackTraceElement)binding.getSource();
              file = source.getFileName();
              location = source.getLineNumber();
            } catch (Throwable throwable) {
              locationProblems.add(new CodeProblem.BindingProblem(name,key.getTypeLiteral().toString(),throwable));
            }
          }
          StackTraceElement[] stackTrace = new StackTraceElement[1];
          stackTrace[0] = source;
          this.bindings.put(bindWhat,
              new BindingCodeLocation(stackTrace,bindWhat,bindTo,name,file,location,locationProblems));
        }
      }
    }
    public String getName() {
      return name;
    }
    public Map<String,BindingCodeLocation> getBindings() {
      return bindings;
    }
  }
  
  /**
   * Represents a module in this context internally.
   */
  public static class ModuleRepresentation {
    private final Class<? extends Module> moduleClass;
    private final List<Class<?>> argTypes;
    private final List<String> argValues;
    public ModuleRepresentation(Class<? extends Module> moduleClass,List<Class<?>> argTypes,List<String> argValues) {
      this.moduleClass = moduleClass;
      this.argTypes = argTypes!=null ? argTypes : new ArrayList<Class<?>>();
      this.argValues = argValues!=null ? argValues : new ArrayList<String>();
    }
    public Module getInstance() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, InstantiationException {
      Class<?>[] argumentTypes = new Class<?>[argTypes.size()];
      Object[] argumentValues = new Object[argTypes.size()];
      int i = 0;
      for (Class<?> argType : argTypes) {
        argumentTypes[i] = argType;
        //TODO: deal with arg values...
        i++;
      }
      return moduleClass.getConstructor(argumentTypes).newInstance(argumentValues);
    }
    public String getName() {
      return moduleClass.getName();
    }
  }
  
  private Injector injector;
  private final String name;
  private final Map<Key<?>,Binding<?>> bindings;
  private boolean isValid;
  
  /**
   * Create a ModuleContextSnippet with the given modules.
   */
  public ModuleContextSnippet(Set<ModuleRepresentation> modules,String name) {
    super();
    isValid = false;
    this.name = name;
    if (!modules.isEmpty()) {
      Set<Module> instances = new HashSet<Module>();
      for (ModuleRepresentation module : modules) {
        try {
          instances.add(module.getInstance());
        } catch (Throwable exception) {
          problems.add(new CodeProblem.InvalidModuleProblem(module.getName()));
        }
      }
      try {
        injector = Guice.createInjector(instances);
        isValid = true;
      } catch (CreationException exception) {
        problems.add(new CodeProblem.CreationProblem(getName(),exception));
      }
      if (isValid) {
        bindings = injector.getBindings();
      } else {
        bindings = null;
      }
    } else {
      bindings = null;
    }
  }
  
  /**
   * Return true if the context is valid, i.e. if an injector can be created.
   */
  public boolean isValid() {
    return isValid;
  }
  
  /**
   * Return the name of the context.
   */
  public String getName() {
    return name;
  }
  
  @Override
  public ModuleContextResult getResult() {
    return new ModuleContextResult(name,bindings,problems);
  }
  
  public Injector getInjector() {
    return injector;
  }
  
  /**
   * Runs the module context by parsing the arguments and creating a snippet, running it
   * and then printing the result to System.out as an object.
   * 
   * @param args the arguments: args[0] is the name of the context, args[1] is the number of
   * modules, args[2] is the class of the first module, args[3] thru args[n] are the arguments
   * for the first module, etc.
   */
  //Expects 1+n args, args[0] is context name, args[1] is number of modules
  //   each past is { module class, # args, arg types ..., args ...
  public static void main(String[] args) {
    runSnippet(System.out, args);
  }
  
  @SuppressWarnings("unchecked")
  public static void runSnippet(OutputStream stream, String[] args) {
    ModuleContextSnippet snippet = null;
    String contextName = "Bad context name";
    try {
      Set<ModuleRepresentation> modules = new HashSet<ModuleRepresentation>();
      List<String> argsSet = new ArrayList<String>();
      for (String arg : args) argsSet.add(arg);
      Iterator<String> arguments = argsSet.iterator();
      contextName = arguments.next();
      int numModules = Integer.valueOf(arguments.next());
      for (int i=0;i<numModules;i++) {
        try {
          Class<?> aClass = Class.forName(arguments.next());
          aClass.asSubclass(Module.class);
          Class<? extends Module> moduleClass = (Class<? extends Module>)aClass;
          int numArgs = Integer.valueOf(arguments.next());
          List<Class<?>> argTypes = new ArrayList<Class<?>>();
          List<String> argValues = new ArrayList<String>();
          if (numArgs > 0) {
            for (int j=0;j<numArgs;j++) {
              argTypes.add(Class.forName(arguments.next()));
              argValues.add(arguments.next());
            }
          }
          modules.add(new ModuleRepresentation(moduleClass,argTypes,argValues));
        } catch (Exception e) {
          i=numModules;
        }
      }
      snippet = new ModuleContextSnippet(modules,contextName);
    } catch(Throwable t) {
      if (snippet == null) {
        snippet = new ModuleContextSnippet(new HashSet<ModuleRepresentation>(), contextName);
        snippet.addProblems(Collections.singleton(new CodeProblem(contextName, t)));
      }
    }
    snippet.printResult(stream);
  }
}
