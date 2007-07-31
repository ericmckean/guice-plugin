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
import com.google.inject.Scopes;
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
	/** 
	 * (non-Javadoc)
	 * @see com.google.inject.AbstractModule#configure()
	 */
	@Override
	protected void configure() {
		bindGuicePlugin();
		bindActionsHandler();
		bindModuleManager();
		bindResultsHandler();
		bindProblemsHandler();
		bindResultsView();
		bindModulesListener();
		bindModuleSelectionView();
		bindMessenger();
		bindProgressHandler();
	}
	
	/** 
	 * Bind the {@link ModuleManager} implementation.
	 */
	protected void bindModuleManager() {
		bind(ModuleManager.class).to(ModuleManagerImpl.class).in(Scopes.SINGLETON);
	}
	
	/** 
	 * Bind the {@link ResultsHandler} implementation.
	 */
	protected void bindResultsHandler() {
		bind(ResultsHandler.class).to(ResultsHandlerImpl.class).in(Scopes.SINGLETON);
	}
	
	/**
	 * Bind the {@link ProblemsHandler} implementation.
	 */
	protected void bindProblemsHandler() {
		bind(ProblemsHandler.class).to(ProblemsHandlerImpl.class).in(Scopes.SINGLETON);
	}
	
	/**
	 * Bind the {@link GuicePlugin} instance.
	 */
	protected abstract void bindGuicePlugin();
	
	/**
	 * Bind the {@link ResultsView} instance.
	 */
	protected abstract void bindResultsView();
	
	/**
	 * Bind the {@link ModulesListener} instance.
	 */
	protected abstract void bindModulesListener();
	
	/**
	 * Bind the {@link ModuleSelectionView} instance.
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
	 * Bind the {@link ProgressHandler} implementation.
	 */
	protected abstract void bindProgressHandler();
}
