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

import com.google.inject.binder.AnnotatedBindingBuilder;
import com.google.inject.tools.ProgressHandler;
import com.google.inject.tools.MockingGuiceToolsModule.ProxyMock;
import com.google.inject.tools.ideplugin.module.ModuleSelectionView;
import com.google.inject.tools.ideplugin.results.ResultsHandler;
import com.google.inject.tools.ideplugin.results.ResultsView;

/**
 * Implementation of the {@link GuicePluginModule} that injects mock objects.
 * 
 * @author Darren Creutz <dcreutz@gmail.com>
 */
public class MockingGuicePluginModule extends GuicePluginModule {
  private boolean useRealResultsHandler = false;
  private boolean useRealBindingsEngine = false;
  
  private ResultsHandler resultsHandler = null;
  private ResultsView resultsView = null;
  private ModuleSelectionView moduleSelectionView = null;
  private ActionsHandler actionsHandler = null;
  private ProgressHandler progressHandler = null;
  
  /**
   * Create a purely mocked module.
   */
  public MockingGuicePluginModule() {
  }
  
  /**
   * Tell the module to use a real ResultsHandler.
   */
  public MockingGuicePluginModule useRealResultsHandler() {
    useRealResultsHandler = true;
    return this;
  }
  
  public MockingGuicePluginModule useRealBindingsEngine() {
    useRealBindingsEngine = true;
    return this;
  }
  
  public MockingGuicePluginModule useResultsHandler(ResultsHandler resultsHandler) {
    this.resultsHandler = resultsHandler;
    return this;
  }
  
  public MockingGuicePluginModule useResultsView(ResultsView resultsView) {
    this.resultsView = resultsView;
    return this;
  }
  
  public MockingGuicePluginModule useModuleSelectionView(ModuleSelectionView moduleSelectionView) {
    this.moduleSelectionView = moduleSelectionView;
    return this;
  }
  
  public MockingGuicePluginModule useActionsHandler(ActionsHandler actionsHandler) {
    this.actionsHandler = actionsHandler;
    return this;
  }
  
  public MockingGuicePluginModule useProgressHandler(ProgressHandler progressHandler) {
    this.progressHandler = progressHandler;
    return this;
  }
  
  @Override
  protected void bindProjectManager(AnnotatedBindingBuilder<ProjectManager> builder) {
    bindToMockInstance(builder, ProjectManager.class);
  }
  
  @Override
  protected void bindBindingsEngine(AnnotatedBindingBuilder<BindingsEngineFactory> builder) {
    if (useRealBindingsEngine) super.bindBindingsEngine(builder);
    else bindToMockInstance(builder, BindingsEngineFactory.class);
  }
  
  @Override
  protected void bindResultsHandler(AnnotatedBindingBuilder<ResultsHandler> builder) {
    if (resultsHandler!=null) bindToInstance(builder, resultsHandler);
    else if (useRealResultsHandler) super.bindResultsHandler(builder);
    else bindToMockInstance(builder, ResultsHandler.class);
  }
  
  @Override
  protected void bindResultsView(AnnotatedBindingBuilder<ResultsView> builder) {
    if (resultsView != null) bindToInstance(builder, resultsView);
    else bindToMockInstance(builder, ResultsView.class);
  }
  
  @Override
  protected void bindModuleSelectionView(AnnotatedBindingBuilder<ModuleSelectionView> builder) {
    if (moduleSelectionView != null) bindToInstance(builder, moduleSelectionView);
    else bindToMockInstance(builder, ModuleSelectionView.class);
  }
  
  @Override
  protected void bindActionsHandler(AnnotatedBindingBuilder<ActionsHandler> builder) {
    if (actionsHandler != null) bindToInstance(builder, actionsHandler);
    else bindToMockInstance(builder, ActionsHandler.class);
  }
  
  @Override
  protected void bindProgressHandler(AnnotatedBindingBuilder<ProgressHandler> builder) {
    if (progressHandler != null) bindToInstance(builder, progressHandler);
    else bindToMockInstance(builder, ProgressHandler.class);
  }
  
  @SuppressWarnings({"unchecked"})
  protected <T> void bindToMockInstance(AnnotatedBindingBuilder<T> builder, Class<T> theClass) {
    builder.toInstance(new ProxyMock<T>(theClass).getInstance());
  }
  
  @SuppressWarnings({"unchecked"})
  protected <T> void bindToInstance(AnnotatedBindingBuilder<T> builder,T instance) {
    builder.toInstance(instance);
  }
}