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

package com.google.inject.tools.ideplugin;

import com.google.inject.Inject;
import com.google.inject.tools.JavaManager;
import com.google.inject.tools.GuiceToolsModule.ModuleManagerFactory;
import com.google.inject.tools.module.ModuleManager;
import com.google.inject.tools.module.ModulesSource;

import java.util.HashMap;
import java.util.Map;

/**
 * {@inheritDoc}
 * 
 * @author Darren Creutz <dcreutz@gmail.com>
 */
public class ProjectManagerImpl implements ProjectManager, ModulesSource.ModulesSourceListener {
  private final Map<JavaManager, ModuleManager> moduleManagers;
  private final ModuleManagerFactory moduleManagerFactory;
  private JavaManager currentProject;
  
  @Inject
  public ProjectManagerImpl(ModuleManagerFactory moduleManagerFactory, ModulesSource modulesSource) {
    this.moduleManagerFactory = moduleManagerFactory;
    this.moduleManagers = new HashMap<JavaManager, ModuleManager>();
    currentProject = null;
    modulesSource.addListener(this);
  }

  public void moduleAdded(ModulesSource source, JavaManager javaManager,
      String module) {
    if (moduleManagers.get(javaManager) == null) projectOpened(javaManager);
    moduleManagers.get(javaManager).initModuleName(module);
  }

  public void moduleChanged(ModulesSource source, JavaManager javaManager,
      String module) {
    moduleManagers.get(javaManager).moduleChanged(module);
  }

  public void moduleRemoved(ModulesSource source, JavaManager javaManager,
      String module) {
    moduleManagers.get(javaManager).removeModule(module);
  }

  public ModuleManager getModuleManager(JavaManager javaManager) {
    return createModuleManager(javaManager);
  }
  
  public ModuleManager getModuleManager() {
    return createModuleManager(currentProject);
  }

  public void projectOpened(JavaManager javaManager) {
    currentProject = javaManager;
    createModuleManager(javaManager);
  }
  
  public void projectClosed(JavaManager javaManager) {
    //TODO: save settings...
    moduleManagers.remove(javaManager);
  }
  
  public JavaManager getCurrentProject() {
    return currentProject;
  }
  
  private ModuleManager createModuleManager(JavaManager javaManager) {
    if (moduleManagers.get(javaManager) == null) moduleManagers.put(javaManager, moduleManagerFactory.create(javaManager));
    return moduleManagers.get(javaManager);
  }
}
