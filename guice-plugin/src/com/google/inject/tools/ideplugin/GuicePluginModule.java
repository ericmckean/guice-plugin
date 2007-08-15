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
import com.google.inject.tools.ideplugin.bindings.BindingsEngine;
import com.google.inject.tools.ideplugin.code.CodeRunner;
import com.google.inject.tools.ideplugin.code.CodeRunnerImpl;
import com.google.inject.tools.ideplugin.module.ModuleManager;
import com.google.inject.tools.ideplugin.module.ModuleManagerImpl;
import com.google.inject.tools.ideplugin.module.ModuleSelectionView;
import com.google.inject.tools.ideplugin.module.ModulesListener;
import com.google.inject.tools.ideplugin.problem.ProblemsHandler;
import com.google.inject.tools.ideplugin.problem.ProblemsHandlerImpl;
import com.google.inject.tools.ideplugin.results.ResultsHandler;
import com.google.inject.tools.ideplugin.results.ResultsHandlerImpl;
import com.google.inject.tools.ideplugin.results.ResultsView;

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
  
  /**
   * Factory for creating {@link CodeRunner}s.
   */
  public interface CodeRunnerFactory {
    /**
     * Create a {@link CodeRunner}.
     * @param project the {@link JavaProject} to run code in
     */
    public CodeRunner create(JavaProject project);
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
  
  protected static class CodeRunnerFactoryImpl implements CodeRunnerFactory {
    private final Provider<ProgressHandler> progressHandlerProvider;
    @Inject
    public CodeRunnerFactoryImpl(Provider<ProgressHandler> progressHandlerProvider) {
      this.progressHandlerProvider = progressHandlerProvider;
    }
    public CodeRunner create(JavaProject project) {
      return new CodeRunnerImpl(project, progressHandlerProvider.get());
    }
  }
  
  /** 
   * (non-Javadoc)
   * @see com.google.inject.AbstractModule#configure()
   */
  @Override
  protected void configure() {
    bindBindingsEngine(bind(BindingsEngineFactory.class));
    bindCodeRunner(bind(CodeRunnerFactory.class));
    bindProgressHandler(bind(ProgressHandler.class));
    bindActionsHandler(bind(ActionsHandler.class));
    bindModuleManager(bind(ModuleManager.class));
    bindResultsHandler(bind(ResultsHandler.class));
    bindProblemsHandler(bind(ProblemsHandler.class));
    bindResultsView(bind(ResultsView.class));
    bindModulesListener(bind(ModulesListener.class));
    bindModuleSelectionView(bind(ModuleSelectionView.class));
    bindMessenger(bind(Messenger.class));
  }
  
  /**
   * Bind the {@link BindingsEngine} factory.
   */
  protected void bindBindingsEngine(AnnotatedBindingBuilder<BindingsEngineFactory> builder) {
    builder.to(BindingsEngineFactoryImpl.class).asEagerSingleton();
  }
  
  /** 
   * Bind the {@link ModuleManager} implementation.
   */
  protected void bindModuleManager(AnnotatedBindingBuilder<ModuleManager> builder) {
    builder.to(ModuleManagerImpl.class).asEagerSingleton();
  }
  
  /** 
   * Bind the {@link ResultsHandler} implementation.
   */
  protected void bindResultsHandler(AnnotatedBindingBuilder<ResultsHandler> builder) {
    builder.to(ResultsHandlerImpl.class).asEagerSingleton();
  }
  
  /**
   * Bind the {@link ProblemsHandler} implementation.
   */
  protected void bindProblemsHandler(AnnotatedBindingBuilder<ProblemsHandler> builder) {
    builder.to(ProblemsHandlerImpl.class).asEagerSingleton();
  }
  
  /**
   * Bind the {@link com.google.inject.tools.ideplugin.results.ResultsView} instance.
   */
  protected abstract void bindResultsView(AnnotatedBindingBuilder<ResultsView> builder);
  
  /**
   * Bind the {@link com.google.inject.tools.ideplugin.module.ModulesListener} instance.
   */
  protected abstract void bindModulesListener(AnnotatedBindingBuilder<ModulesListener> builder);
  
  /**
   * Bind the {@link com.google.inject.tools.ideplugin.module.ModuleSelectionView} instance.
   */
  protected abstract void bindModuleSelectionView(AnnotatedBindingBuilder<ModuleSelectionView> builder);
  
  /**
   * Bind the {@link Messenger} implementation.
   */
  protected abstract void bindMessenger(AnnotatedBindingBuilder<Messenger> builder);
  
  /**
   * Bind the {@link ActionsHandler} implementation.
   */
  protected abstract void bindActionsHandler(AnnotatedBindingBuilder<ActionsHandler> builder);
  
  /**
   * Bind the {@link CodeRunner} implementation.
   */
  protected void bindCodeRunner(AnnotatedBindingBuilder<CodeRunnerFactory> builder) {
    builder.to(CodeRunnerFactoryImpl.class);
  }
  
  /**
   * Bind the {@link ProgressHandler} implementation.
   */
  protected abstract void bindProgressHandler(AnnotatedBindingBuilder<ProgressHandler> builder);
}
