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
import com.google.inject.tools.ideplugin.bindings.BindingsEngine;
import com.google.inject.tools.ideplugin.code.CodeRunner;
import com.google.inject.tools.ideplugin.code.CodeRunnerImpl;
import com.google.inject.tools.ideplugin.module.ModuleManager;
import com.google.inject.tools.ideplugin.module.ModuleManagerImpl;
import com.google.inject.tools.ideplugin.problem.ProblemsHandler;
import com.google.inject.tools.ideplugin.problem.ProblemsHandlerImpl;
import com.google.inject.tools.ideplugin.results.ResultsHandler;
import com.google.inject.tools.ideplugin.results.ResultsHandlerImpl;

/** 
 * Abstract module for the plugin's dependency injection.  IDE specific implementations are
 * required.
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
		
		@Inject
		public BindingsEngineFactoryImpl(Provider<ModuleManager> moduleManagerProvider,
				Provider<ProblemsHandler> problemsHandlerProvider,
				Provider<ResultsHandler> resultsHandlerProvider) {
			this.moduleManagerProvider = moduleManagerProvider;
			this.problemsHandlerProvider = problemsHandlerProvider;
			this.resultsHandlerProvider = resultsHandlerProvider;
		}
		
		public BindingsEngine create(JavaElement element) {
			return new BindingsEngine(moduleManagerProvider.get(),
					problemsHandlerProvider.get(),
					resultsHandlerProvider.get(),
					element);
		}
	}
  
  protected static class CodeRunnerFactoryImpl implements CodeRunnerFactory {
    @Inject
    public CodeRunnerFactoryImpl() {}
    public CodeRunner create(JavaProject project) {
      return new CodeRunnerImpl(project);
    }
  }
	
	/** 
	 * (non-Javadoc)
	 * @see com.google.inject.AbstractModule#configure()
	 */
	@Override
	protected void configure() {
		bindBindingsEngine();
		bindCodeRunner();
    bindProgressHandler();
		bindActionsHandler();
		bindModuleManager();
		bindResultsHandler();
		bindProblemsHandler();
		bindResultsView();
		bindModulesListener();
		bindModuleSelectionView();
		bindMessenger();
	}
	
	/**
	 * Bind the {@link BindingsEngine} factory.
	 */
	protected void bindBindingsEngine() {
		bind(BindingsEngineFactory.class).to(BindingsEngineFactoryImpl.class).asEagerSingleton();
	}
	
	/** 
	 * Bind the {@link ModuleManager} implementation.
	 */
	protected void bindModuleManager() {
		bind(ModuleManager.class).to(ModuleManagerImpl.class).asEagerSingleton();
	}
	
	/** 
	 * Bind the {@link ResultsHandler} implementation.
	 */
	protected void bindResultsHandler() {
		bind(ResultsHandler.class).to(ResultsHandlerImpl.class).asEagerSingleton();
	}
	
	/**
	 * Bind the {@link ProblemsHandler} implementation.
	 */
	protected void bindProblemsHandler() {
		bind(ProblemsHandler.class).to(ProblemsHandlerImpl.class).asEagerSingleton();
	}
	
	/**
	 * Bind the {@link com.google.inject.tools.ideplugin.results.ResultsView} instance.
	 */
	protected abstract void bindResultsView();
	
	/**
	 * Bind the {@link com.google.inject.tools.ideplugin.module.ModulesListener} instance.
	 */
	protected abstract void bindModulesListener();
	
	/**
	 * Bind the {@link com.google.inject.tools.ideplugin.module.ModuleSelectionView} instance.
	 */
	protected abstract void bindModuleSelectionView();
	
	/**
	 * Bind the {@link Messenger} implementation.
	 */
	protected abstract void bindMessenger();
	
	/**
	 * Bind the {@link ActionsHandler} implementation.
	 */
	protected abstract void bindActionsHandler();
	
	/**
	 * Bind the {@link CodeRunner} implementation.
	 */
	protected void bindCodeRunner() {
	  bind(CodeRunnerFactory.class).to(CodeRunnerFactoryImpl.class);
  }
  
  /**
   * Bind the {@link ProgressHandler} implementation.
   */
  protected abstract void bindProgressHandler();
}
