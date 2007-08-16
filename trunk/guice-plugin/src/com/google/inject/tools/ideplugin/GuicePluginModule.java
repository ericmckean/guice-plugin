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

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.binder.AnnotatedBindingBuilder;
import com.google.inject.tools.Messenger;
import com.google.inject.tools.ProblemsHandler;
import com.google.inject.tools.ProgressHandler;
import com.google.inject.tools.ideplugin.bindings.BindingsEngine;
import com.google.inject.tools.ideplugin.results.ResultsHandler;
import com.google.inject.tools.ideplugin.results.ResultsHandlerImpl;
import com.google.inject.tools.ideplugin.results.ResultsView;
import com.google.inject.tools.module.ModuleManager;
import com.google.inject.tools.ideplugin.module.ModuleSelectionView;

/** 
 * Abstract module for the plugin's dependency injection.  IDE specific implementations are
 * required.
 * 
 * The general pattern is:
 * <code>protected abstract void bindFoo(AnnotatedBindingBuilder<Foo> builder)</code>
 * 
 * should be implemented as:
 * <code>void bindFoo(AnnotatedBindingBuilder<Foo> builder) {
 *   builder.to(FooImpl.class);
 * }</code>
 * 
 * @author Darren Creutz <dcreutz@gmail.com>
 */
public abstract class GuicePluginModule extends AbstractModule {
  protected interface BindingsEngineFactory {
    public BindingsEngine create(JavaElement element);
  }
  
  protected static class BindingsEngineFactoryImpl implements BindingsEngineFactory {
    private final Provider<ModuleManager> moduleManagerProvider;
    private final Provider<ProblemsHandler> problemsHandlerProvider;
    private final Provider<ResultsHandler> resultsHandlerProvider;
    private final Provider<Messenger> messengerProvider;
    
    @Inject
    public BindingsEngineFactoryImpl(Provider<ModuleManager> moduleManagerProvider,
        Provider<ProblemsHandler> problemsHandlerProvider,
        Provider<ResultsHandler> resultsHandlerProvider,
        Provider<Messenger> messengerProvider) {
      this.moduleManagerProvider = moduleManagerProvider;
      this.problemsHandlerProvider = problemsHandlerProvider;
      this.resultsHandlerProvider = resultsHandlerProvider;
      this.messengerProvider = messengerProvider;
    }
    
    public BindingsEngine create(JavaElement element) {
      return new BindingsEngine(moduleManagerProvider.get(),
          problemsHandlerProvider.get(),
          resultsHandlerProvider.get(),
          messengerProvider.get(),
          element);
    }
  }
  
  /** 
   * (non-Javadoc)
   * @see com.google.inject.AbstractModule#configure()
   */
  @Override
  protected void configure() {
    bindBindingsEngine(bind(BindingsEngineFactory.class));
    bindProgressHandler(bind(ProgressHandler.class));
    bindActionsHandler(bind(ActionsHandler.class));
    bindResultsHandler(bind(ResultsHandler.class));
    bindResultsView(bind(ResultsView.class));
    bindModuleSelectionView(bind(ModuleSelectionView.class));
  }
  
  /**
   * Bind the {@link BindingsEngine} factory.
   */
  protected void bindBindingsEngine(AnnotatedBindingBuilder<BindingsEngineFactory> builder) {
    builder.to(BindingsEngineFactoryImpl.class).asEagerSingleton();
  }
  
  /** 
   * Bind the {@link ResultsHandler} implementation.
   */
  protected void bindResultsHandler(AnnotatedBindingBuilder<ResultsHandler> builder) {
    builder.to(ResultsHandlerImpl.class).asEagerSingleton();
  }
  
  /**
   * Bind the {@link com.google.inject.tools.ideplugin.results.ResultsView} instance.
   */
  protected abstract void bindResultsView(AnnotatedBindingBuilder<ResultsView> builder);

  /**
   * Bind the {@link com.google.inject.tools.ideplugin.module.ModuleSelectionView} instance.
   */
  protected abstract void bindModuleSelectionView(AnnotatedBindingBuilder<ModuleSelectionView> builder);
  
  /**
   * Bind the {@link ActionsHandler} implementation.
   */
  protected abstract void bindActionsHandler(AnnotatedBindingBuilder<ActionsHandler> builder);
  
  /**
   * Bind the {@link ProgressHandler} implementation.
   */
  protected abstract void bindProgressHandler(AnnotatedBindingBuilder<ProgressHandler> builder);
}
