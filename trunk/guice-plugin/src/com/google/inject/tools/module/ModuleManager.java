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

package com.google.inject.tools.module;

import java.util.Set;

/**
 * Responsible for tracking the modules which should be run when resolving
 * bindings and injections. The {@link ModulesSource} notifies the ModuleManager
 * when these change.
 * 
 * Users should call updateModules() periodically and then
 * getActiveModuleContexts() and use the resulting objects to find information
 * about guice modules.
 * 
 * @author Darren Creutz <dcreutz@gmail.com>
 */
public interface ModuleManager {
  public static class NoJavaManagerException extends RuntimeException {
    /**
     * 
     */
    private static final long serialVersionUID = 5092824035153637390L;
    private final ModuleManager moduleManager;

    public NoJavaManagerException(ModuleManager moduleManager) {
      this.moduleManager = moduleManager;
    }

    @Override
    public String toString() {
      return "No java manager for this ModuleManager: " + moduleManager;
    }
  }

  /**
   * Notify the ModuleManager that a module has been added by the user.
   * 
   * @param module the module added
   * @param createContext true if the manager should create a new context for
   *        this module
   */
  public void addModule(ModuleRepresentation module, boolean createContext)
      throws NoJavaManagerException;

  /**
   * Notify the ModuleManager that a module has been removed by the user.
   * 
   * @param module the module removed
   */
  public void removeModule(ModuleRepresentation module)
      throws NoJavaManagerException;

  /**
   * Notify the ModuleManager that a module has been added by the user.
   * 
   * @param moduleName the name of the module added
   * @param createContext true if the manager should create a new context for
   *        this module
   */
  public void addModule(String moduleName, boolean createContext)
      throws NoJavaManagerException;

  /**
   * Notify the manager that a new module name is available. This should only be
   * called the {@link ModulesSource}.
   */
  public void initModuleName(String moduleName);

  /**
   * Notify the ModuleManager that a module has been removed by the user.
   * 
   * @param moduleName the name of the module removed
   */
  public void removeModule(String moduleName) throws NoJavaManagerException;

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
   * Add a {@link ModuleContextRepresentation} to the manager.
   * 
   * @param moduleContext the module context
   * @param active true if the context should be marked as active
   */
  public void addModuleContext(ModuleContextRepresentation moduleContext,
      boolean active) throws NoJavaManagerException;

  /**
   * Remove a {@link ModuleContextRepresentation} from the manager.
   * 
   * @param moduleContext the module context
   */
  public void removeModuleContext(ModuleContextRepresentation moduleContext)
      throws NoJavaManagerException;

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
   * Update the modules by rerunning any dirty.
   * 
   * @param waitFor true if the current thread should wait for the update
   * @return true if the update succeeded (false if the user canceled the
   *         operation)
   */
  public boolean updateModules(boolean waitFor, boolean backgroundAutomatically);

  /**
   * Wait for the manager to clean the modules in the current project.
   * 
   * @return true if the update succeeded (false if the user cancelled the
   *         operation)
   */
  public boolean updateModules();

  public interface PostUpdater {
    public void execute(boolean success);
  }

  public void updateModules(PostUpdater postUpdater,
      boolean backgroundAutomatically);

  /**
   * (Re)run the modules by marking them all as dirty and then updating.
   * 
   * @param waitFor true if the current thread should wait for the update
   * @return true if the update succeeded (false on user cancel)
   */
  public boolean rerunModules(boolean waitFor, boolean backgroundAutomatically);

  public boolean rerunModules();

  public void rerunModules(PostUpdater postUpdater,
      boolean backgroundAutomatically);

  /**
   * (Re)run the modules in user to find any new context options.
   * 
   * @param waitFor true if the current thread should wait for the update
   * @return true if the operation succeeded (false if the user canceled it)
   */
  public boolean findNewContexts(boolean waitFor,
      boolean backgroundAutomatically);

  public boolean findNewContexts();

  public void findNewContexts(PostUpdater postUpdater,
      boolean backgroundAutomatically);

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
  public void activateModuleContext(ModuleContextRepresentation moduleContext);

  /**
   * Deactivate a module context.
   */
  public void deactivateModuleContext(ModuleContextRepresentation moduleContext);

  public void addCustomContext(String contextName);

  public void removeCustomContext(String contextName);

  public void customContextChanged(String contextName);
}
