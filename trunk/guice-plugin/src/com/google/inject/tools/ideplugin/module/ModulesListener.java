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

import java.util.HashSet;
import java.util.Set;
import com.google.inject.Inject;
import com.google.inject.tools.JavaManager;
import com.google.inject.tools.Messenger;
import com.google.inject.tools.module.ModuleManager;
import com.google.inject.tools.module.ModulesNotifier;

/**
 * Abstract implementation of the {@link ModulesNotifier} for the IDE plugins.
 * 
 * @author Darren Creutz <dcreutz@gmail.com>
 */
public abstract class ModulesListener implements ModulesNotifier {
  protected final Messenger messenger;
  protected final HashSet<String> modules;
  protected final ModuleManager moduleManager;
  protected JavaManager javaManager;
  
  /**
   * Create an EclipseModulesListener.  This should be injected.
   */
  @Inject
  public ModulesListener(ModuleManager moduleManager,Messenger messenger) {
    this.moduleManager = moduleManager;
    this.messenger = messenger;
    modules = new HashSet<String>();
    try {
      initialize();
    } catch (Throwable throwable) {
      hadProblem(throwable);
    }
  }
  
  /**
   * (non-Javadoc)
   * @see com.google.inject.tools.module.ModulesNotifier#projectChanged(com.google.inject.tools.JavaManager)
   */
  public void projectChanged(JavaManager manager) {
    javaManager = manager;
    try {
      initialize();
    } catch (Throwable throwable) {
      hadProblem(throwable);
    }
  }
  
  /**
   * Initialize the module listening for a new java project.
   */
  protected abstract void initialize() throws Throwable;
  
  /**
   * Locate the modules.
   */
  protected abstract Set<String> locateModules() throws Throwable;
  
  /**
   * (non-Javadoc)
   * @see com.google.inject.tools.module.ModulesNotifier#findModules()
   */
  public Set<String> findModules() {
    try {
      keepModulesByName(locateModules());
    } catch (Throwable throwable) {
      hadProblem(throwable);
    }
    return new HashSet<String>(modules);
  }
  
  protected synchronized void keepModulesByName(Set<String> modulesNames) {
    Set<String> newModules = new HashSet<String>(modulesNames);
    for (String module : modules) {
      boolean keep = false;
      for (String name : modulesNames) {
        if (name.equals(module)) {
          keep = true;
          newModules.remove(name);
        }
      }
      if (!keep) {
        modules.remove(module);
        moduleManager.removeModule(module);
      }
    }
    for (String moduleName : newModules) {
      moduleManager.initModuleName(moduleName);
      modules.add(moduleName);
    }
  }
  
  protected void hadProblem(Throwable exception) {
    messenger.logException("Modules Listener error", exception);
  }
  
  /**
   * (non-Javadoc)
   * @see com.google.inject.tools.module.ModulesNotifier#findChanges()
   */
  public void findChanges() {
    if (javaManager != null) findModules();
  }
}
