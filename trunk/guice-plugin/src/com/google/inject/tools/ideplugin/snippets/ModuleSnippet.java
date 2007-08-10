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

package com.google.inject.tools.ideplugin.snippets;

import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import com.google.inject.Module;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;

/**
 * A code snippet for running a module to find its constructor information and validity.
 * 
 * @author Darren Creutz <dcreutz@gmail.com>
 */
public class ModuleSnippet<T extends Module> extends CodeSnippet {
  /**
   * Representation of a constructor for a module.
   */
	public static class ConstructorRepresentation {
		private final List<String> argumentTypes;
		private final Set<String> exceptionTypes;
		public ConstructorRepresentation(List<Class<?>> argumentTypes,Set<Class<?>> exceptionTypes) {
      if (argumentTypes != null) {
        this.argumentTypes = new ArrayList<String>();
        for (Class<?> argument : argumentTypes) {
          this.argumentTypes.add(argument.getName());
        }
      } else {
        this.argumentTypes = null;
      }
      if (exceptionTypes != null) {
        this.exceptionTypes = new HashSet<String>();
        for (Class<?> exception : exceptionTypes) {
          this.exceptionTypes.add(exception.getName());
        }
      } else {
        this.exceptionTypes = null;
      }
		}
		public List<String> getArgumentTypes() {
			return argumentTypes;
		}
		public Set<String> getExceptionTypes() {
			return exceptionTypes;
		}
    @Override
    public boolean equals(Object object) {
      if (!(object instanceof ConstructorRepresentation)) return false;
      ConstructorRepresentation constructor = (ConstructorRepresentation)object;
      return argumentTypes.equals(constructor.getArgumentTypes()) &&
        exceptionTypes.equals(constructor.getExceptionTypes());
    }
	}
  
  /**
   * Represents a default constructor for a module.
   */
  public static class DefaultConstructorRepresentation extends ConstructorRepresentation {
    public DefaultConstructorRepresentation() {
      super(null,null);
    }
  }
	
  /**
   * The result of a module snippet run.
   */
	public static class ModuleResult extends CodeSnippetResult {
		private final String name;
		private final Set<ConstructorRepresentation> constructors;
		private final boolean hasDefaultConstructor;
		public ModuleResult(String name,Set<? extends CodeProblem> problems,boolean hasDefaultConstructor,Set<ConstructorRepresentation> constructors) {
			super(problems);
			this.name = name;
			this.constructors = constructors;
			this.hasDefaultConstructor = hasDefaultConstructor;
		}
		public String getName() {
			return name;
		}
		public Set<ConstructorRepresentation> getConstructors() {
			return constructors;
		}
		public boolean hasDefaultConstructor() {
			return hasDefaultConstructor;
		}
	}
	
	private final Class<T> moduleClass;
	private final String className;
	private T instance;
	private Constructor<T> defaultConstructor = null;
	
  /**
   * Create a module snippet for the module with the given class.
   */
	public ModuleSnippet(String className) {
		super();
		this.className = className;
		moduleClass = findClass(className);
		if (moduleClass != null) {
			if (isModule(moduleClass)) {
				try {
					defaultConstructor = moduleClass.getConstructor((Class[])null);
				} catch (NoSuchMethodException exception) {
					defaultConstructor = null;
				}
				try {
					setDefaultConstructor();
				} catch (Exception exception) {
					defaultConstructor = null;
				}
			} else {
				problems.add(new CodeProblem.InvalidModuleProblem(className));
				defaultConstructor = null;
			}
		}
	}
		
	@SuppressWarnings("unchecked")	
	private Class<T> findClass(String className) {
		try {
			 return (Class<T>)Class.forName(className);
		} catch (Exception exception) {
			problems.add(new CodeProblem.BadClassProblem(className,exception));
			return null;
		}
	}
	
	private boolean isModule(Class<?> aClass) {
		try {
			aClass.asSubclass(Module.class);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
   * Return the constructors of the module.
	 */
	public Set<ConstructorRepresentation> getConstructors() {
		final Set<ConstructorRepresentation> cons = new HashSet<ConstructorRepresentation>();
		for (Constructor<T> constructor : moduleClass.getConstructors()) {
			List<Class<?>> argumentTypes = new ArrayList<Class<?>>();
			Set<Class<?>> exceptionTypes = new HashSet<Class<?>>();
			for (Object arg : constructor.getParameterTypes()) {
				argumentTypes.add(arg.getClass());
			}
			for (Object exc : constructor.getExceptionTypes()) {
				exceptionTypes.add(exc.getClass());
			}
			cons.add(new ConstructorRepresentation(argumentTypes,exceptionTypes));
		}
		return cons;
	}

	/**
   * Try to set the constructor for the module.  This will alert us to any problems.
   * 
   * @param constructor the constructor to try
   * @param arguments the arguments to pass it
	 */
	public void setConstructor(Constructor<T> constructor,Object[] arguments) throws IllegalAccessException,IllegalArgumentException,InstantiationException,InvocationTargetException {
		this.instance = constructor.newInstance(arguments);
	}

	/**
   * Return true if the module has a default (no argument) constructor.
	 */
	public boolean hasDefaultConstructor() {
		return (defaultConstructor != null);
	}

	private void setDefaultConstructor() throws 
		IllegalAccessException, IllegalArgumentException, InstantiationException, InvocationTargetException {
		if (defaultConstructor != null) setConstructor(defaultConstructor,null);
	}
	
	@Override
	public ModuleResult getResult() {
		return new ModuleResult(className,problems,hasDefaultConstructor(),getConstructors());
	}
	
	public String getName() {
		return className;
	}
	
	public boolean isValid() {
		return instance!=null;
	}
  
  public T getInstance() {
    return instance;
  }
	
  /**
   * Runs a module snippet.
   * 
   * @param args args[0] shuld be the class of the module to run
   */
	//Expects 1 arg: the class name
  public static void main(String[] args) {
    runSnippet(System.out,args);
  }
  
	@SuppressWarnings("unchecked")
	public static void runSnippet(OutputStream stream, String[] args) {
		ModuleSnippet<? extends Module> snippet = new ModuleSnippet(args[0]);
		snippet.printResult(stream);
	}
}
