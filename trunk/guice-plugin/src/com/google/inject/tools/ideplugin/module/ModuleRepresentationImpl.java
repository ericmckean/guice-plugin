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

package com.google.inject.tools.ideplugin.module;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import com.google.inject.Module;

/**
 * Represents a single module in the module context.
 * 
 * @author Darren Creutz <dcreutz@gmail.com>
 */
public class ModuleRepresentationImpl implements ModuleRepresentation {
	private String name;
	private final Class<? extends Module> moduleClass;
	private Constructor<? extends Module> constructor = null;
	private Constructor<? extends Module> defaultConstructor = null;
	private Object[] arguments = null;
	private Module instance = null;
	private boolean isValid = false;

	/**
	 * Create a ModuleRepresentationImpl from a Class type.
	 * 
	 * @param moduleClass the class of the module to represent
	 * @throws ClassNotModuleException if the class does not implement {@link Module}
	 */
	public ModuleRepresentationImpl(Class<? extends Module> moduleClass) throws ClassNotModuleException {
		if (isModule(moduleClass)) {
			this.moduleClass = moduleClass;
			initialize();
		} else {
			throw new ClassNotModuleException(moduleClass);
		}
	}

	/**
	 * Create a ModuleRepresentationImpl from a class name string.
	 * 
	 * @param className the class name
	 * @throws ClassNotFoundException if there is no such class
	 * @throws ClassNotModuleException if the class does not implement {@link Module}
	 */
	@SuppressWarnings("unchecked")
	public ModuleRepresentationImpl(String className) throws ClassNotFoundException,ClassNotModuleException {
		Class<?> aClass = Class.forName(className);
		if (isModule(aClass)) {
			this.moduleClass = (Class<? extends Module>)aClass;
			initialize();
		} else {
			throw new ClassNotModuleException(aClass);
		}
	}

	private boolean isModule(Class<?> aClass) {
		return (Module.class).isAssignableFrom(aClass);
	}

	private void initialize() {
		this.name = moduleClass.getName();
		try {
			defaultConstructor = moduleClass.getConstructor((Class[])null);
		} catch (NoSuchMethodException exception) {
			defaultConstructor = null;
		}
		try {
			setDefaultConstructor();
		} catch (Exception exception) {
			defaultConstructor = null;
			constructor = null;
		}
	}

	/**
	 * (non-Javadoc)
	 * @see com.google.inject.tools.ideplugin.module.ModuleRepresentation#getConstructors()
	 */
	@SuppressWarnings("unchecked")
	public Constructor<? extends Module>[] getConstructors() {
		return moduleClass.getConstructors();
	}

	/**
	 * (non-Javadoc)
	 * @see com.google.inject.tools.ideplugin.module.ModuleRepresentation#setConstructor(java.lang.reflect.Constructor, java.lang.Object[])
	 */
	public void setConstructor(Constructor<? extends Module> constructor,Object[] arguments) throws IllegalAccessException,IllegalArgumentException,InstantiationException,InvocationTargetException {
		this.constructor = constructor;
		this.arguments = arguments;
		this.instance = constructor.newInstance(arguments);
		if (instance != null) isValid = true;
	}

	/**
	 * (non-Javadoc)
	 * @see com.google.inject.tools.ideplugin.module.ModuleRepresentation#getConstructor()
	 */
	public Constructor<? extends Module> getConstructor() {
		return constructor;
	}

	/**
	 * (non-Javadoc)
	 * @see com.google.inject.tools.ideplugin.module.ModuleRepresentation#getArguments()
	 */
	public Object[] getArguments() {
		return arguments;
	}

	/**
	 * (non-Javadoc)
	 * @see com.google.inject.tools.ideplugin.module.ModuleRepresentation#getInstance()
	 */
	public Module getInstance() {
		return instance;
	}

	/**
	 * (non-Javadoc)
	 * @see com.google.inject.tools.ideplugin.module.ModuleRepresentation#hasDefaultConstructor()
	 */
	public boolean hasDefaultConstructor() {
		return (defaultConstructor != null);
	}

	private void setDefaultConstructor() throws 
		IllegalAccessException, IllegalArgumentException, InstantiationException, InvocationTargetException {
		if (defaultConstructor != null) setConstructor(defaultConstructor,null);
	}
	
	/**
	 * (non-Javadoc)
	 * @see com.google.inject.tools.ideplugin.module.ModuleRepresentation#getName()
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * (non-Javadoc)
	 * @see com.google.inject.tools.ideplugin.module.ModuleRepresentation#setName(java.lang.String)
	 */
	public void setName(String moduleName) {
		this.name = moduleName;
	}
	
	/**
	 * (non-Javadoc)
	 * @see com.google.inject.tools.ideplugin.module.ModuleRepresentation#isValid()
	 */
	public boolean isValid() {
		return isValid;
	}
	
	/**
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object object) {
		if (object instanceof ModuleRepresentation) {
			ModuleRepresentation module = (ModuleRepresentation)object;
			if (name.equals(module.getName())) {
				if (moduleClass.equals(module.getClass())) {
					if (arguments != null) {
						return arguments.equals(module.getArguments());
					} else {
						return module.getArguments() == null;
					}
				}
			}
		}
		return false;
	}
	
	/**
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Module Representation [" + name + " (" + moduleClass + ")] " + arguments;
	}
}