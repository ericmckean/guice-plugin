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

package com.google.inject.tools.suite.module;

import com.google.inject.tools.suite.module.ModuleContextRepresentation;
import com.google.inject.tools.suite.module.ModuleManager;
import com.google.inject.tools.suite.module.ModuleRepresentation;

import java.util.Set;

/**
 * Responsible for tracking the modules which should be run when resolving
 * bindings and injections.
 * 
 * Users should call updateModules() periodically and then
 * getActiveModuleContexts() and use the resulting objects to find information
 * about guice modules.
 * 
 * @author Darren Creutz (dcreutz@gmail.com)
 */
public interface ModuleManager {
  /**
   * Notify the ModuleManager that a module has been added by the user.
   * 
   * @param moduleName the name of the module added
   * @param createContext true if the manager should create a new context for
   *        this module
   */
  public void addModule(String moduleName, boolean createContext);

  /**
   * Notify the ModuleManager that a module has been removed by the user.
   * 
   * @param moduleName the name of the module removed
   */
  public void removeModule(String moduleName);

  /**
   * Notify the ModuleManager that all modules for the current project should be
   * cleared from memory.
   */
  public void clearModules();

  /**
   * Get all modules that have been added for the current project.
   * 
   * @return the {@link ModuleContextRepresentation}s
   */
  public Set<ModuleRepresentation> getModules();

  /**
   * Clear all {@link ModuleContextRepresentation}s from the manager for the
   * current project.
   */
  public void clearModuleContexts();

  /**
   * Return a set of all {@link ModuleContextRepresentation}s the manager has
   * for the current project.
   * 
   * @return the module contexts
   */
  public Set<ModuleContextRepresentation> getModuleContexts();

  /**
   * Return the active contexts.
   */
  public Set<ModuleContextRepresentation> getActiveModuleContexts();

  /**
   * Notify the manager that a module has changed; it will tell the contexts.
   * 
   * @param module the module
   */
  public void moduleChanged(String module);

  /**
   * Update the module contexts by rerunning any dirty.
   * 
   * @param waitFor true if the current thread should wait for the update
   * @return true if the update succeeded (false if the user canceled the
   *         operation)
   */
  public boolean update(boolean waitFor, boolean backgroundAutomatically);

  /**
   * Wait for the manager to clean the module contexts.
   * 
   * @return true if the update succeeded (false if the user cancelled the
   *         operation)
   */
  public boolean updateModules();
  
  /**
   * Update the modules by rerunning any dirty.
   * 
   * @param waitFor true if the current thread should wait for the update
   * @return true if the update succeeded (false if the user canceled the
   *         operation)
   */
  public boolean updateModules(boolean waitFor, boolean backgroundAutomatically);

  /**
   * Wait for the manager to clean the modules.
   * 
   * @return true if the update succeeded (false if the user cancelled the
   *         operation)
   */
  public boolean update();

  /**
   * A piece of code to run after an update has completed.
   */
  public interface PostUpdater {
    public void execute(boolean success);
  }

  /**
   * Clean the modules and then execute the given code.
   * 
   * @param postUpdater the code to execute on completion
   * @param backgroundAutomatically true if the process should run in the
   * background
   */
  public void update(PostUpdater postUpdater,
      boolean backgroundAutomatically);

  /**
   * (Re)run the modules by marking them all as dirty and then updating.
   * 
   * @param waitFor true if the current thread should wait for the update
   * @return true if the update succeeded (false on user cancel)
   */
  public boolean rerunModules(boolean waitFor, boolean backgroundAutomatically);

  /**
   * Wait for the manager to (re)run all the modules.
   * 
   * @return true if the rerun succeeded (false if the user cancelled)
   */
  public boolean rerunModules();

  /**
   * (Re)run the modules and then execute the given code.
   * 
   * @param postUpdater the code to execute on completion
   * @param backgroundAutomatically true if the process should run in the
   * background
   */
  public void rerunModules(PostUpdater postUpdater,
      boolean backgroundAutomatically);

  /**
   * True if the module contexts should be run automatically.
   */
  public boolean runAutomatically();
  
  /**
   * Tell the module manager to run modules automatically as needed.
   */
  public void setRunAutomatically(boolean run);

  /**
   * True if newly created modules should be activated by default.
   */
  public boolean activateModulesByDefault();

  /**
   * Set default activation of modules.
   */
  public void setActivateModulesByDefault(boolean activateByDefault);

  /**
   * Activate a module context.
   */
  public void activateModuleContext(String contextName);

  /**
   * Deactivate a module context.
   */
  public void deactivateModuleContext(String contextName);

  /**
   * Notify the manager that the user has added a custom context.
   * 
   * @param contextName the context name
   */
  public void addCustomContext(String contextName);
  
  /**
   * Add a custom context to the module manager.
   * 
   * @param name the context name
   * @param classToUse the class in user code to use
   * @param methodToCall the method of that class that returns the modules
   */
  public void addCustomContext(String name, String classToUse, String methodToCall);

  /**
   * Notify the manager that the user has removed a custom context.
   */
  public void removeCustomContext(String contextName);

  /**
   * Add an application context to the module manager.
   */
  public void addApplicationContext(String contextName, String className);
  
  /**
   * Add an application context to the module manager.
   */
  public void addApplicationContext(String className);

  /**
   * Notify the manager that the user has removed an application context.
   */
  public void removeApplicationContext(String contextName);

  /**
   * Notify the manager that an application context has changed.
   */
  public void moduleContextChanged(String contextName);
  
  /**
   * Create a new (empty) module context.
   */
  public ModuleContextRepresentation createModuleContext(String name);
  
  /**
   * Return the module context with the given name.
   */
  public ModuleContextRepresentation getModuleContext(String name);
}
