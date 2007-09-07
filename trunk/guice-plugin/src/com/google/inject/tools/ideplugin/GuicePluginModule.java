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

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.binder.AnnotatedBindingBuilder;
import com.google.inject.tools.ideplugin.bindings.BindingsEngine;
import com.google.inject.tools.ideplugin.results.ResultsHandler;
import com.google.inject.tools.ideplugin.results.ResultsHandlerImpl;
import com.google.inject.tools.ideplugin.results.ResultsView;
import com.google.inject.tools.ideplugin.JavaProject;
import com.google.inject.tools.suite.Messenger;
import com.google.inject.tools.suite.ProblemsHandler;
import com.google.inject.tools.suite.ProgressHandler;

/**
 * Abstract module for the plugin's dependency injection. IDE specific
 * implementations are required.
 * 
 * The general pattern is:
 * <code>protected abstract void bindFoo(AnnotatedBindingBuilder<Foo> builder)</code>
 * 
 * should be implemented as:
 * <code>void bindFoo(AnnotatedBindingBuilder<Foo> builder) {
 *   builder.to(FooImpl.class);
 * }</code>
 * 
 * @author Darren Creutz (dcreutz@gmail.com)
 */
public abstract class GuicePluginModule extends AbstractModule {
  protected interface BindingsEngineFactory {
    public BindingsEngine create(JavaElement element, JavaProject javaProject);
  }

  protected static class BindingsEngineFactoryImpl implements
      BindingsEngineFactory {
    private final ProjectManager projectManager;
    private final Provider<ProblemsHandler> problemsHandlerProvider;
    private final Provider<ResultsHandler> resultsHandlerProvider;
    private final Provider<Messenger> messengerProvider;
    private final Provider<ProgressHandler> progressHandlerProvider;

    @Inject
    public BindingsEngineFactoryImpl(ProjectManager projectManager,
        Provider<ProblemsHandler> problemsHandlerProvider,
        Provider<ResultsHandler> resultsHandlerProvider,
        Provider<Messenger> messengerProvider,
        Provider<ProgressHandler> progressHandlerProvider) {
      this.projectManager = projectManager;
      this.problemsHandlerProvider = problemsHandlerProvider;
      this.resultsHandlerProvider = resultsHandlerProvider;
      this.messengerProvider = messengerProvider;
      this.progressHandlerProvider = progressHandlerProvider;
    }

    public BindingsEngine create(JavaElement element, JavaProject javaProject) {
      return new BindingsEngine(projectManager, javaProject,
          problemsHandlerProvider.get(), resultsHandlerProvider.get(),
          progressHandlerProvider.get(), messengerProvider.get(), element);
    }
  }

  @Override
  protected void configure() {
    bindProjectManager(bind(ProjectManager.class));
    bindBindingsEngine(bind(BindingsEngineFactory.class));
    bindModulesSource(bind(ModulesSource.class));
    bindActionsHandler(bind(ActionsHandler.class));
    bindGotoCodeLocationHandler(bind(GotoCodeLocationHandler.class));
    bindGotoFileHandler(bind(GotoFileHandler.class));
    bindResultsHandler(bind(ResultsHandler.class));
    bindResultsView(bind(ResultsView.class));
    bindModuleSelectionView(bind(ModuleSelectionView.class));
    bindCustomContextDefinitionSource(bind(CustomContextDefinitionSource.class));
    bindProjectSource(bind(ProjectSource.class));
  }

  protected void bindProjectManager(
      AnnotatedBindingBuilder<ProjectManager> bindProjectManager) {
    bindProjectManager.to(ProjectManagerImpl.class).asEagerSingleton();
  }
  
  protected abstract void bindProjectSource(
      AnnotatedBindingBuilder<ProjectSource> bindProjectSource);

  /**
   * Bind the {@link BindingsEngine} factory.
   */
  protected void bindBindingsEngine(
      AnnotatedBindingBuilder<BindingsEngineFactory> bindBindingsEngine) {
    bindBindingsEngine.to(BindingsEngineFactoryImpl.class).asEagerSingleton();
  }

  /**
   * Bind the {@link ResultsHandler} implementation.
   */
  protected void bindResultsHandler(
      AnnotatedBindingBuilder<ResultsHandler> bindResultsHandler) {
    bindResultsHandler.to(ResultsHandlerImpl.class).asEagerSingleton();
  }

  /**
   * Bind the {@link com.google.inject.tools.ideplugin.results.ResultsView}
   * instance.
   */
  protected abstract void bindResultsView(
      AnnotatedBindingBuilder<ResultsView> bindResultsView);

  /**
   * Bind the
   * {@link com.google.inject.tools.ideplugin.ModuleSelectionView}
   * instance.
   */
  protected abstract void bindModuleSelectionView(
      AnnotatedBindingBuilder<ModuleSelectionView> bindModuleSelectionView);

  /**
   * Bind the {@link ActionsHandler} implementation.
   */
  protected void bindActionsHandler(
      AnnotatedBindingBuilder<ActionsHandler> bindActionsHandler) {
    bindActionsHandler.to(ActionsHandlerImpl.class).asEagerSingleton();
  }

  /**
   * Bind the {@link GotoCodeLocationHandler} implementation.
   */
  protected abstract void bindGotoCodeLocationHandler(
      AnnotatedBindingBuilder<GotoCodeLocationHandler> bindGotoCodeLocationHandler);

  /**
   * Bind the {@link GotoFileHandler} implementation.
   */
  protected abstract void bindGotoFileHandler(
      AnnotatedBindingBuilder<GotoFileHandler> bindGotoFileHandler);

  /**
   * Bind the {@link CustomContextDefinitionSource} implementation.
   */
  protected abstract void bindCustomContextDefinitionSource(
      AnnotatedBindingBuilder<CustomContextDefinitionSource> bindCustomContextDefinitionSource);
  
  protected abstract void bindModulesSource(
      AnnotatedBindingBuilder<ModulesSource> bindModulesSource);
}
