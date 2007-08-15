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

package com.google.inject.tools.ideplugin.eclipse;

import com.google.inject.binder.AnnotatedBindingBuilder;
import com.google.inject.tools.ideplugin.GuicePluginModule;
import com.google.inject.tools.ideplugin.Messenger;
import com.google.inject.tools.ideplugin.ActionsHandler;
import com.google.inject.tools.ideplugin.ProgressHandler;
import com.google.inject.tools.ideplugin.module.ModuleSelectionView;
import com.google.inject.tools.ideplugin.results.ResultsView;
import com.google.inject.tools.ideplugin.module.ModulesListener;

/** 
 * The module binding Eclipse implementations to interfaces.
 * 
 * @author Darren Creutz <dcreutz@gmail.com>
 */
public class EclipsePluginModule extends GuicePluginModule {
  private ResultsView resultsView;
  private ModuleSelectionView moduleSelectionView;
  
  /**
   * Create an Eclipse Plugin Module for injection.
   */
  public EclipsePluginModule() {
    super();
  }
  
  /**
   * (non-Javadoc)
   * @see com.google.inject.tools.ideplugin.GuicePluginModule#bindResultsView()
   */
  @Override
  protected void bindResultsView(AnnotatedBindingBuilder<ResultsView> builder) {
    builder.toInstance(resultsView);
  }
  
  /**
   * (non-Javadoc)
   * @see com.google.inject.tools.ideplugin.GuicePluginModule#bindModuleSelectionView()
   */
  @Override
  protected void bindModuleSelectionView(AnnotatedBindingBuilder<ModuleSelectionView> builder) {
    builder.toInstance(moduleSelectionView);
  }
  
  /**
   * (non-Javadoc)
   * @see com.google.inject.tools.ideplugin.GuicePluginModule#bindMessenger()
   */
  @Override
  protected void bindMessenger(AnnotatedBindingBuilder<Messenger> builder) {
    builder.to(EclipseMessenger.class).asEagerSingleton();
  }
  
  /**
   * (non-Javadoc)
   * @see com.google.inject.tools.ideplugin.GuicePluginModule#bindActionsHandler()
   */
  @Override
  protected void bindActionsHandler(AnnotatedBindingBuilder<ActionsHandler> builder) {
    builder.to(EclipseActionsHandler.class).asEagerSingleton();
  }
  
  /**
   * (non-Javadoc)
   * @see com.google.inject.tools.ideplugin.GuicePluginModule#bindModulesListener()
   */
  @Override
  protected void bindModulesListener(AnnotatedBindingBuilder<ModulesListener> builder) {
    builder.to(EclipseModulesListener.class).asEagerSingleton();
  }
  
  @Override
  protected void bindProgressHandler(AnnotatedBindingBuilder<ProgressHandler> builder) {
    builder.to(EclipseProgressHandler.class);
  }
  
  /**
   * Sets the results view; eclipse creates the instance.
   */
  public void setResultsView(ResultsView resultsView) {
    this.resultsView = resultsView;
  }
  
  /**
   * Sets the module selection view; eclipse creates the instance.
   */
  public void setModuleSelectionView(ModuleSelectionView moduleSelectionView) {
    this.moduleSelectionView = moduleSelectionView;
  }
  
  /**
   * Return the results view.
   */
  public ResultsView getResultsView() {
    return resultsView;
  }
  
  /**
   * Return the module selection view.
   */
  public ModuleSelectionView getModuleSelectionView() {
    return moduleSelectionView;
  }
}
