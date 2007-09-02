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
import com.google.inject.tools.ideplugin.bindings.BindingsEngine;
import com.google.inject.tools.ideplugin.results.ResultsHandler;
import com.google.inject.tools.suite.GuiceToolsModule;
import com.google.inject.tools.ideplugin.JavaProject;
import com.google.inject.tools.suite.Messenger;
import com.google.inject.tools.suite.ProblemsHandler;
import com.google.inject.tools.suite.GuiceToolsModule.ModuleManagerFactory;
import com.google.inject.tools.suite.module.ModuleManager;
import com.google.inject.tools.ideplugin.module.ModuleSelectionView;

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

  /**
   * Create a (the) GuicePlugin.
   * 
   * @param module the (IDE specific) module to inject based on
   */
  public GuicePlugin(GuicePluginModule module, GuiceToolsModule toolsModule) {
    injector = Guice.createInjector(module, toolsModule);
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
    return getInstance(ModuleManagerFactory.class).create(javaProject);
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
