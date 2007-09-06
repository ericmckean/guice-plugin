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
import com.google.inject.tools.ideplugin.module.ModulesSource;
import com.google.inject.tools.ideplugin.JavaProject;
import com.google.inject.tools.suite.JavaManager;
import com.google.inject.tools.suite.ProgressHandler;
import com.google.inject.tools.suite.GuiceToolsModule.ModuleManagerFactory;
import com.google.inject.tools.suite.module.ModuleContextRepresentation;
import com.google.inject.tools.suite.module.ModuleManager;
import com.google.inject.tools.suite.module.ModuleRepresentation;
import com.google.inject.tools.suite.module.ModuleManager.PostUpdater;

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
  private final ProgressHandler progressHandler;
  private JavaProject currentProject;

  //TODO: make this a preference
  private static final boolean shouldListenForChanges = false;
  
  @Inject
  public ProjectManagerImpl(ModuleManagerFactory moduleManagerFactory,
      ModulesSource modulesSource, ProgressHandler progressHandler,
      CustomContextDefinitionSource customContextDefinitionSource) {
    this.moduleManagerFactory = moduleManagerFactory;
    this.modulesSource = modulesSource;
    this.customContextDefinitionSource = customContextDefinitionSource;
    this.progressHandler = progressHandler;
    customContextDefinitionSource.addListener(this);
    this.moduleManagers = new HashMap<JavaProject, ModuleManager>();
    currentProject = null;
    modulesSource.addListener(this);
    modulesSource.listenForChanges(shouldListenForChanges);
    customContextDefinitionSource.listenForChanges(shouldListenForChanges);
    initializeProjects();
  }
  
  private void initializeProjects() {
    for (JavaProject project : ((ModulesListener) modulesSource)
        .getOpenProjects()) {
      progressHandler.step(new InitializationProgressStep(project));
    }
    progressHandler.go("Guice Plugin Initialization", true);
  }
  
  private class InitializationProgressStep implements ProgressHandler.ProgressStep {
    private final JavaProject project;
    private boolean done;
    
    public InitializationProgressStep(JavaProject project) {
      this.project = project;
      done = false;
    }
    
    public void cancel() {
      //TODO: cancel initialization?
    }

    public void complete() {
      done = true;
    }

    public boolean isDone() {
      return done;
    }

    public String label() {
      return "Initializing project for guice plugin: " + project.getName();
    }

    public void run() {
      done = false;
      ModuleManager moduleManager = createModuleManager(project);
      for (String customContextName : customContextDefinitionSource
          .getContexts(project)) {
        moduleManager.addApplicationContext(customContextName);
      }
    }
  }

  public void moduleAdded(ModulesSource source, JavaManager javaManager,
      String module) {
    if (javaManager instanceof JavaProject) {
      JavaProject project = (JavaProject)javaManager;
      if (moduleManagers.get(project) == null) {
        projectOpened(project);
      }
      initModuleName(project, module);
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
    moduleManagers.get(javaManager).addApplicationContext(context);
  }

  public void contextDefinitionChanged(CustomContextDefinitionSource source,
      JavaProject javaManager, String context) {
    moduleManagers.get(javaManager).applicationContextChanged(context);
  }

  public void contextDefinitionRemoved(CustomContextDefinitionSource source,
      JavaProject javaManager, String context) {
    moduleManagers.get(javaManager).removeApplicationContext(context);
  }

  public ModuleManager getModuleManager(JavaProject javaManager) {
    try {
      progressHandler.waitFor();
    } catch (InterruptedException e) {}
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
      ModuleManager moduleManager = moduleManagerFactory.create(javaManager);
      moduleManagers.put(javaManager, moduleManager);
      synchronized (moduleManager) {
        initModules(javaManager);
        initContexts(moduleManager);
      }
    }
    return moduleManagers.get(javaManager);
  }
  
  /*
   * Ask the ModulesListener for all the modules in the user's code.
   */
  private synchronized void initModules(JavaManager javaManager) {
    if (javaManager != null) {
      for (String moduleName : modulesSource.getModules(javaManager)) {
        initModule(moduleManagers.get(javaManager), moduleName);
      }
    }
    if (moduleManagers.get(javaManager).runAutomatically()) {
      cleanAllModules(moduleManagers.get(javaManager), true, true);
    }
  }

  private void initModule(ModuleManager moduleManager, String moduleName) {
    moduleManager.addModule(moduleName, false);
  }
  
  public boolean findNewContexts(JavaManager javaManager, boolean waitFor,
      boolean backgroundAutomatically) {
    boolean result = cleanAllModules(moduleManagers.get(javaManager), waitFor, backgroundAutomatically);
    initContexts(moduleManagers.get(javaManager));
    return result;
  }
  
  public boolean findNewContexts(JavaManager javaManager) {
    return findNewContexts(javaManager, true, true);
  }

  /*
   * Create module contexts for each module that we can.
   */
  private void initContexts(ModuleManager moduleManager) {
    for (ModuleRepresentation module : moduleManager.getModules()) {
      if (module.hasDefaultConstructor()) {
        ModuleContextRepresentation context = moduleManager.createModuleContext(module.getName());
        context.addModule(module.getName());
        if (moduleManager.activateModulesByDefault()) {
          moduleManager.activateModuleContext(module.getName());
        }
      }
    }
    if (moduleManager.runAutomatically()) {
      cleanModuleContexts(moduleManager, true, true);
    }
  }
  
  private boolean cleanModuleContexts(ModuleManager moduleManager,
      boolean waitFor, boolean backgroundAutomatically) {
    return moduleManager.update(waitFor, backgroundAutomatically);
  }
  
  private boolean cleanAllModules(ModuleManager moduleManager,
      boolean waitFor, boolean backgroundAutomatically) {
    return moduleManager.updateModules(waitFor, backgroundAutomatically);
  }
  
  public void findNewContexts(JavaManager javaManager,
      final PostUpdater postUpdater,
      final boolean backgroundAutomatically) {
    new FindNewContextsThread(javaManager, postUpdater, backgroundAutomatically)
        .start();
  }
  
  private class FindNewContextsThread extends Thread {
    private final JavaManager javaManager;
    private final PostUpdater postUpdater;
    private final boolean backgroundAutomatically;
    public FindNewContextsThread(JavaManager javaManager,
        PostUpdater postUpdater, boolean backgroundAutomatically) {
      this.javaManager = javaManager;
      this.postUpdater = postUpdater;
      this.backgroundAutomatically = backgroundAutomatically;
    }

    @Override
    public void run() {
      postUpdater.execute(findNewContexts(javaManager, true, backgroundAutomatically));
    }
  }
  
  private void initModuleName(JavaManager project, String moduleName) {
    moduleManagers.get(project).addModule(moduleName, false);
  }
  
  public JavaManager getJavaManager(ModuleManager moduleManager) {
    for (JavaManager project : moduleManagers.keySet()) {
      if (moduleManagers.get(project).equals(moduleManager)) {
        return project;
      }
    }
    return null;
  }
}
