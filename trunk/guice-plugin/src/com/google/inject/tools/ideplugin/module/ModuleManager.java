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

import java.util.Set;

/** 
 * Responsible for tracking the modules which should be run when resolving bindings and injections.
 * The {@link ModulesListener} notifies the ModuleManager when these change.  The {@link ModuleSelectionView}
 * notifies the ModuleManager when the user changes what modules they want run.
 * 
 * @author Darren Creutz <dcreutz@gmail.com>
 */
public interface ModuleManager {
	/**
	 * Notify the ModuleManager that a module has been added by the user.
	 * 
	 * @param module the module added
	 */
	public void addModule(ModuleRepresentation module);
	
	/**
	 * Notify the ModuleManager that a module has been removed by the user.
	 * 
	 * @param module the module removed
	 */
	public void removeModule(ModuleRepresentation module);
	
	/**
	 * Notify the ModuleManager that a module has been added by the user.
	 * 
	 * @param moduleName the name of the module added
	 */
	public void addModule(String moduleName);
	
	/**
	 * Notify the ModuleManager that a module has been removed by the user.
	 * 
	 * @param moduleName the name of the module removed
	 */
	public void removeModule(String moduleName);
	
	/**
	 * Notify the ModuleManager that all modules should be cleared from memory.
	 */
	public void clearModules();
	
	/**
	 * Get all modules that have been added.
	 * 
	 * @return the {@link ModuleContextRepresentation}s
	 */
	public Set<ModuleRepresentation> getModules();
	
	/**
	 * Add a {@link ModuleContextRepresentation} to the manager.
	 * 
	 * @param moduleContext the module context
	 */
	public void addModuleContext(ModuleContextRepresentation moduleContext);
	
	/**
	 * Remove a {@link ModuleContextRepresentation} from the manager.
	 * 
	 * @param moduleContext the module context
	 */
	public void removeModuleContext(ModuleContextRepresentation moduleContext);
	
	/**
	 * Clear all {@link ModuleContextRepresentation}s from the manager.
	 */
	public void clearModuleContexts();
	
	/**
	 * Return a set of all {@link ModuleContextRepresentation}s the manager has.
	 * 
	 * @return the module contexts
	 */
	public Set<ModuleContextRepresentation> getModuleContexts();
	
	/**
	 * Notify the manager that a module has changed; it will tell the contexts.
	 * 
	 * @param module the module
	 */
	public void moduleChanged(String module);
}
