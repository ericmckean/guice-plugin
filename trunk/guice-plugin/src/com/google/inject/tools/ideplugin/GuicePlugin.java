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

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.tools.ideplugin.bindings.BindingsEngine;
import com.google.inject.tools.ideplugin.results.ResultsHandler;
import com.google.inject.tools.suite.GuiceToolsModule;
import com.google.inject.tools.ideplugin.JavaProject;
import com.google.inject.tools.suite.Messenger;
import com.google.inject.tools.suite.ProblemsHandler;
import com.google.inject.tools.suite.ProgressHandler;
import com.google.inject.tools.suite.module.ModuleManagerFactory;
import com.google.inject.tools.suite.ProgressHandler.ProgressMonitor;
import com.google.inject.tools.suite.module.ModuleManager;

/**
 * The main object of the plugin. Unfortunately, it must be created in IDE
 * specific ways. Responsible for creating the {@link Injector} and building the
 * various objects the plugin needs. This object can be thought of as a wrapper
 * for the {@link Injector}.
 * 
 * @author Darren Creutz (dcreutz@gmail.com)
 */
public abstract class GuicePlugin {
  private final Injector injector;
  private final Provider<ProgressHandler> progressHandlerProvider;

  /**
   * Create a (the) GuicePlugin.
   * 
   * @param module the (IDE specific) module to inject based on
   */
  public GuicePlugin(GuicePluginModule module, GuiceToolsModule toolsModule) {
    injector = Guice.createInjector(module, toolsModule);
    progressHandlerProvider = injector.getProvider(ProgressHandler.class);
  }

  /**
   * Allow subclasses to request instances from the injector.
   * 
   * @param type the type to get an instance of
   * @return the instance
   */
  protected <T> T getInstance(Class<T> type) {
    return injector.getInstance(type);
  }

  /**
   * Return the {@link ProjectManager}.
   */
  public ProjectManager getProjectManager() {
    return getInstance(ProjectManager.class);
  }

  /**
   * Create a {@link BindingsEngine}.
   * 
   * @param element the java element to find bindings of
   */
  public BindingsEngine getBindingsEngine(JavaElement element,
      JavaProject javaProject) {
    return getInstance(GuicePluginModule.BindingsEngineFactory.class).create(
        element, javaProject);
  }
  
  private void runAction(ProgressHandler.ProgressStep step, boolean backgroundAutomatically,
      Runnable executeAfter) {
    ProgressHandler progressHandler = progressHandlerProvider.get();
    progressHandler.step(step);
    progressHandler.executeAfter(executeAfter);
    progressHandler.go(step.label(), backgroundAutomatically);
  }
  
  /**
   * Show the Configure dialog.
   * 
   * @param project the JavaProject to configure
   */
  public void configurePlugin(JavaProject project, boolean backgroundAutomatically) {
    runAction(new ConfigurePluginAction(project), backgroundAutomatically,
        new ConfigurePluginActionComplete(project));
  }
  
  class ConfigurePluginActionComplete implements Runnable {
    private final JavaProject project;
    
    public ConfigurePluginActionComplete(JavaProject project) {
      this.project = project;
    }
    
    public void run() {
      getModuleSelectionView().show(project);
    }
  }
  
  class ConfigurePluginAction implements ProgressHandler.ProgressStep {
    private final JavaProject project;
    private boolean done;
    
    public ConfigurePluginAction(JavaProject project) {
      this.project = project;
      done = false;
    }
    
    public void cancel() {
      done = true;
    }

    public void complete() {
      done = true;
    }

    public boolean isDone() {
      return done;
    }

    public String label() {
      return "Configuring Guice Plugin for " + project.getName();
    }

    public void run(ProgressMonitor monitor) {
      done = false;
      getProjectManager().getModuleManager(project);
    }
  }
  
  class RunModulesNowAction implements ProgressHandler.ProgressStep {
    private final JavaProject project;
    private boolean done;
    
    public RunModulesNowAction(JavaProject project) {
      this.project = project;
      done = false;
    }
    
    public void cancel() {
      done = true;
    }

    public void complete() {
      done = true;
    }

    public boolean isDone() {
      return done;
    }

    public String label() {
      return "Configuring Guice Plugin for " + project.getName();
    }

    public void run(ProgressMonitor monitor) {
      done = false;
      getProjectManager().getModuleManager(project).rerunModules(false, false);
    }
  }
  
  /**
   * Run the modules now.
   * 
   * @param project the JavaProject to run modules in
   */
  public void runModulesNow(JavaProject project, boolean backgroundAutomatically) {
    runAction(new RunModulesNowAction(project), backgroundAutomatically, null);
  }

  /**
   * Return the {@link ResultsHandler}.
   */
  public ResultsHandler getResultsHandler() {
    return getInstance(ResultsHandler.class);
  }

  /**
   * Return the {@link ModuleManager}.
   */
  public ModuleManager getModuleManager(JavaProject javaProject) {
    return getInstance(ModuleManagerFactory.class).create(javaProject,
        javaProject.loadSettings());
  }

  /**
   * Return the {@link ProblemsHandler}.
   */
  public ProblemsHandler getProblemsHandler() {
    return getInstance(ProblemsHandler.class);
  }

  /**
   * Return the {@link Messenger}.
   */
  public Messenger getMessenger() {
    return getInstance(Messenger.class);
  }

  /**
   * Return the {@link ActionsHandler}.
   */
  public ActionsHandler getActionsHandler() {
    return getInstance(ActionsHandler.class);
  }

  /**
   * Return the {@link ModuleSelectionView}.
   */
  public ModuleSelectionView getModuleSelectionView() {
    return getInstance(ModuleSelectionView.class);
  }
}
