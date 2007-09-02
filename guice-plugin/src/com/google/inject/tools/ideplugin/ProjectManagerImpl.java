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
import com.google.inject.Singleton;
import com.google.inject.tools.ideplugin.CustomContextDefinitionSource.CustomContextDefinitionListener;
import com.google.inject.tools.ideplugin.module.ModulesListener;
import com.google.inject.tools.ideplugin.JavaProject;
import com.google.inject.tools.suite.JavaManager;
import com.google.inject.tools.suite.GuiceToolsModule.ModuleManagerFactory;
import com.google.inject.tools.suite.module.ModuleManager;
import com.google.inject.tools.suite.module.ModulesSource;

import java.util.HashMap;
import java.util.Map;

/**
 * {@inheritDoc ProjectManager}
 * 
 * @author Darren Creutz (dcreutz@gmail.com)
 */
@Singleton
class ProjectManagerImpl implements ProjectManager,
    ModulesSource.ModulesSourceListener, CustomContextDefinitionListener {
  private final Map<JavaProject, ModuleManager> moduleManagers;
  private final ModuleManagerFactory moduleManagerFactory;
  private final ModulesSource modulesSource;
  private final CustomContextDefinitionSource customContextDefinitionSource;
  private JavaProject currentProject;
  private final Map<JavaProject, CustomContextsThread> initThreads;

  @Inject
  public ProjectManagerImpl(ModuleManagerFactory moduleManagerFactory,
      ModulesSource modulesSource,
      CustomContextDefinitionSource customContextDefinitionSource) {
    this.moduleManagerFactory = moduleManagerFactory;
    this.modulesSource = modulesSource;
    this.customContextDefinitionSource = customContextDefinitionSource;
    customContextDefinitionSource.addListener(this);
    this.moduleManagers = new HashMap<JavaProject, ModuleManager>();
    currentProject = null;
    modulesSource.addListener(this);
    initThreads = new HashMap<JavaProject, CustomContextsThread>();
    if (modulesSource instanceof ModulesListener) { // which it should be
      for (JavaProject project : ((ModulesListener) modulesSource)
          .getOpenProjects()) {
        createModuleManager(project);
        CustomContextsThread initThread = 
            new CustomContextsThread(moduleManagers.get(project),
                customContextDefinitionSource, project);
        initThreads.put(project, initThread);
        initThread.start();
      }
    }
  }

  private static class CustomContextsThread extends Thread {
    private final ModuleManager moduleManager;
    private final CustomContextDefinitionSource customContextDefinitionSource;
    private final JavaProject project;
    private volatile boolean done;

    public CustomContextsThread(ModuleManager moduleManager,
        CustomContextDefinitionSource customContextDefinitionSource,
        JavaProject project) {
      done = false;
      this.moduleManager = moduleManager;
      this.customContextDefinitionSource = customContextDefinitionSource;
      this.project = project;
    }

    @Override
    public void run() {
      try {
        moduleManager.waitForInitialization();
        for (String customContextName : customContextDefinitionSource
            .getContexts(project)) {
          moduleManager.addCustomContext(customContextName);
        }
      } catch (InterruptedException e) {}
      done = true;
    }
    
    public boolean isDone() {
      return done;
    }
  }

  public void moduleAdded(ModulesSource source, JavaManager javaManager,
      String module) {
    if (javaManager instanceof JavaProject) {
      JavaProject project = (JavaProject)javaManager;
      if (moduleManagers.get(project) == null) {
        projectOpened(project);
      }
      moduleManagers.get(project).initModuleName(module);
    }
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
      JavaProject javaManager, String context) {
    if (moduleManagers.get(javaManager) == null) {
      projectOpened(javaManager);
    }
    moduleManagers.get(javaManager).addCustomContext(context);
  }

  public void contextDefinitionChanged(CustomContextDefinitionSource source,
      JavaProject javaManager, String context) {
    moduleManagers.get(javaManager).customContextChanged(context);
  }

  public void contextDefinitionRemoved(CustomContextDefinitionSource source,
      JavaProject javaManager, String context) {
    moduleManagers.get(javaManager).removeCustomContext(context);
  }

  public ModuleManager getModuleManager(JavaProject javaManager) {
    if (initThreads.get(javaManager) != null
        && !initThreads.get(javaManager).isDone()) {
      try {
        initThreads.get(javaManager).join();
      } catch (InterruptedException e) {}
    }
    modulesSource.refresh(javaManager);
    customContextDefinitionSource.refresh(javaManager);
    return createModuleManager(javaManager);
  }

  public ModuleManager getModuleManager() {
    return createModuleManager(currentProject);
  }
  
  public void projectOpened(JavaProject javaProject) {
    ProjectSettings settings = javaProject.loadSettings();
    createModuleManager(javaProject);
    moduleManagers.get(javaProject).setActivateModulesByDefault(settings.activateByDefault);
    moduleManagers.get(javaProject).setRunAutomatically(settings.runAutomatically);
    //TODO: load contexts
  }
  
  public void projectClosed(JavaProject javaManager) {
    ProjectSettings settings = new ProjectSettings();
    settings.activateByDefault = moduleManagers.get(javaManager).activateModulesByDefault();
    settings.runAutomatically = moduleManagers.get(javaManager).runAutomatically();
    //TODO: save contexts
    javaManager.saveSettings(settings);
    moduleManagers.remove(javaManager);
  }

  public void javaManagerAdded(ModulesSource modulesSource, 
      JavaManager javaManager) {
    if (javaManager instanceof JavaProject) {
      projectOpened((JavaProject)javaManager);
    }
  }

  public void javaManagerRemoved(ModulesSource modulesSource, 
      JavaManager javaManager) {
    if (javaManager instanceof JavaProject) {
      projectClosed((JavaProject)javaManager);
    }
  }

  public JavaProject getCurrentProject() {
    return currentProject;
  }

  private ModuleManager createModuleManager(JavaProject javaManager) {
    currentProject = javaManager;
    if (moduleManagers.get(javaManager) == null) {
      moduleManagers.put(javaManager, moduleManagerFactory.create(javaManager));
    }
    return moduleManagers.get(javaManager);
  }
}
