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

import com.google.inject.tools.ideplugin.problem.CodeProblem;
import com.google.inject.Injector;
import java.util.Set;

/** 
 * Representation of a module context in the user's code.
 * 
 * @author Darren Creutz <dcreutz@gmail.com>
 */
public interface ModuleContextRepresentation {
	/**
	 * A runtime exception thrown when a {@link ModuleRepresentation} passed in is invalid.
	 */
	public static class InvalidModuleException extends RuntimeException {
		private static final long serialVersionUID = 4942665092517642767L; //autogenerated
		private final ModuleRepresentation module;
		
		/**
		 * Create a ClassNotModuleException.
		 * 
		 * @param module the module that is invalid
		 */
		public InvalidModuleException(ModuleRepresentation module) {
			this.module = module;
		}
		
		/**
		 * Return the class that does not implement Module.
		 * 
		 * @return the class
		 */
		public ModuleRepresentation getModule() {
			return module;
		}
		
		/**
		 * (non-Javadoc)
		 * @see java.lang.Throwable#toString()
		 */
		@Override
		public String toString() {
			return "InvalidModuleException: " + module.toString();
		}
	}
	
	/**
	 * Return an injector of the module context.
	 * 
	 * @return the {@link Injector} object
	 */
	public Injector getInjector();
	
	/**
	 * Does the context have a problem?
	 * 
	 * @return true if there is a problem
	 */
	public boolean hasProblem();
	
	/**
	 * Return the {@link com.google.inject.tools.ideplugin.problem.CodeProblem.CreationProblem} with this context.
	 * 
	 * @return the problem
	 */
	public CodeProblem.CreationProblem getProblem();
	
	/**
	 * Add the module with the given name to this context.
	 * 
	 * @param module the module
	 */
	public ModuleContextRepresentation add(ModuleRepresentation module) throws InvalidModuleException;
	
	/**
	 * Return the modules in this context.
	 */
	public Set<ModuleRepresentation> getModules();
	
	/**
	 * Remove the given module from this context.
	 * 
	 * @param module the module
	 */
	public void removeModule(ModuleRepresentation module);
	
	/**
	 * Tell the context that its module have changed.
	 */
	public void update();

	/**
	 * Return the name of this representation.
	 * 
	 * @return the name
	 */
	public String getName();
	
	/**
	 * Does this context have the given module in it?
	 * 
	 * @param module the module
	 * @return true if the module is in this context
	 */
	public boolean contains(ModuleRepresentation module);
	
	/**
	 * Does this context contain the module with this name?
	 * 
	 * @param moduleName the name of the module
	 * @return true if the module is in this context
	 */
	public boolean contains(String moduleName);
	
	/**
	 * Is the module context valid for running?
	 * 
	 * @return true if we can run this context
	 */
	public boolean isValid();
}