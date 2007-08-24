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

package com.google.inject.tools.ideplugin;

import com.google.inject.Inject;
import com.google.inject.tools.JavaManager;
import com.google.inject.tools.GuiceToolsModule.ModuleManagerFactory;
import com.google.inject.tools.ideplugin.CustomContextDefinitionSource.CustomContextDefinitionListener;
import com.google.inject.tools.ideplugin.module.ModulesListener;
import com.google.inject.tools.module.ModuleManager;
import com.google.inject.tools.module.ModuleManagerImpl;
import com.google.inject.tools.module.ModulesSource;

import java.util.HashMap;
import java.util.Map;

/**
 * {@inheritDoc}
 * 
 * @author Darren Creutz <dcreutz@gmail.com>
 */
public class ProjectManagerImpl implements ProjectManager,
    ModulesSource.ModulesSourceListener, CustomContextDefinitionListener {
  private final Map<JavaManager, ModuleManager> moduleManagers;
  private final ModuleManagerFactory moduleManagerFactory;
  private JavaManager currentProject;

  @Inject
  public ProjectManagerImpl(ModuleManagerFactory moduleManagerFactory,
      ModulesSource modulesSource,
      CustomContextDefinitionSource customContextDefinitionSource) {
    this.moduleManagerFactory = moduleManagerFactory;
    customContextDefinitionSource.addListener(this);
    this.moduleManagers = new HashMap<JavaManager, ModuleManager>();
    currentProject = null;
    modulesSource.addListener(this);
    if (modulesSource instanceof ModulesListener) { // which it should be
      for (JavaManager project : ((ModulesListener) modulesSource)
          .getOpenProjects()) {
        createModuleManager(project);
        new CustomContextsThread(moduleManagers.get(project),
            customContextDefinitionSource, project).start();
      }
    }
  }

  private static class CustomContextsThread extends Thread {
    private final ModuleManager moduleManager;
    private final CustomContextDefinitionSource customContextDefinitionSource;
    private final JavaManager project;

    public CustomContextsThread(ModuleManager moduleManager,
        CustomContextDefinitionSource customContextDefinitionSource,
        JavaManager project) {
      this.moduleManager = moduleManager;
      this.customContextDefinitionSource = customContextDefinitionSource;
      this.project = project;
    }

    @Override
    public void run() {
      // TODO: either waitFor is in MM or this goes
      ((ModuleManagerImpl) moduleManager).waitForInitThread();
      for (String customContextName : customContextDefinitionSource
          .getContexts(project)) {
        moduleManager.addCustomContext(customContextName);
      }
    }
  }

  // TODO: somehow need to wait for this to finish before allowing user to do
  // stuff

  public void moduleAdded(ModulesSource source, JavaManager javaManager,
      String module) {
    if (moduleManagers.get(javaManager) == null) {
      projectOpened(javaManager);
    }
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

  public void contextDefinitionAdded(CustomContextDefinitionSource source,
      JavaManager javaManager, String context) {
    if (moduleManagers.get(javaManager) == null) {
      projectOpened(javaManager);
    }
    moduleManagers.get(javaManager).addCustomContext(context);
  }

  public void contextDefinitionChanged(CustomContextDefinitionSource source,
      JavaManager javaManager, String context) {
    moduleManagers.get(javaManager).customContextChanged(context);
  }

  public void contextDefinitionRemoved(CustomContextDefinitionSource source,
      JavaManager javaManager, String context) {
    moduleManagers.get(javaManager).removeCustomContext(context);
  }

  public ModuleManager getModuleManager(JavaManager javaManager) {
    return createModuleManager(javaManager);
  }

  public ModuleManager getModuleManager() {
    return createModuleManager(currentProject);
  }

  public void projectOpened(JavaManager javaManager) {
    createModuleManager(javaManager);
  }

  public void projectClosed(JavaManager javaManager) {
    // TODO: save settings...
    moduleManagers.remove(javaManager);
  }

  public JavaManager getCurrentProject() {
    return currentProject;
  }

  private ModuleManager createModuleManager(JavaManager javaManager) {
    currentProject = javaManager;
    if (moduleManagers.get(javaManager) == null) {
      moduleManagers.put(javaManager, moduleManagerFactory.create(javaManager));
    }
    return moduleManagers.get(javaManager);
  }
}
