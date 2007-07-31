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

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.tools.ideplugin.bindings.BindingsEngine;
import com.google.inject.tools.ideplugin.module.ModuleManager;
import com.google.inject.tools.ideplugin.module.ModuleSelectionView;
import com.google.inject.tools.ideplugin.problem.ProblemsHandler;
import com.google.inject.tools.ideplugin.results.ResultsHandler;
import com.google.inject.tools.ideplugin.results.ResultsView;

/** 
 * The main object of the plugin.  Unfortunately, it must be created in IDE specific ways.
 * Responsible for creating the Injector and building the various objects the plugin needs.
 * 
 * @author Darren Creutz <dcreutz@gmail.com>
 */
public abstract class GuicePlugin {
	private final Injector injector;
	private final ResultsHandler resultsHandler;
	private final ModuleManager moduleManager;
	private final ProblemsHandler problemsHandler;
	private final Messenger messenger;
	private final ActionsHandler actionsHandler;
	
	/** 
	 * Create a (the) GuicePlugin.
	 * 
	 * @param module the (IDE specific) module to inject based on
	 */
	public GuicePlugin(GuicePluginModule module) {
		injector = Guice.createInjector(module);
		moduleManager = injector.getInstance(ModuleManager.class);
		resultsHandler = injector.getInstance(ResultsHandler.class);
		problemsHandler = injector.getInstance(ProblemsHandler.class);
		messenger = injector.getInstance(Messenger.class);
		actionsHandler = injector.getInstance(ActionsHandler.class);
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
	 * Create a {@link BindingsEngine}.  The GuicePlugin acts as a BindingsEngine factory.
	 * 
	 * @return the {@link BindingsEngine}
	 */
	public BindingsEngine getBindingsEngine(JavaElement element) {
	  messenger.display("Mkaing Bindings Engine");
		return new BindingsEngine(moduleManager,problemsHandler,resultsHandler,injector.getInstance(ProgressHandler.class),element);
	}
	
	/** 
	 * Return the {@link ResultsHandler}.
	 * 
	 * @return the {@link ResultsHandler}
	 */
	public ResultsHandler getResultsHandler() {
		return resultsHandler;
	}
	
	/**
	 * Return the {@link ModuleManager}.
	 * 
	 * @return the {@link ModuleManager}
	 */
	public ModuleManager getModuleManager() {
		return moduleManager;
	}
	
	/**
	 * Return the {@link ProblemsHandler}.
	 * 
	 * @return the {@link ProblemsHandler}
	 */
	public ProblemsHandler getProblemsHandler() {
		return problemsHandler;
	}
	
	/**
	 * Return the {@link Messenger}.
	 * 
	 * @return the {@link Messenger}
	 */
	public Messenger getMessenger() {
		return messenger;
	}
	
	/**
	 * Return the {@link ActionsHandler}.
	 * 
	 * @return the {@link ActionsHandler}
	 */
	public ActionsHandler getActionsHandler() {
		return actionsHandler;
	}
	
	/**
	 * Return the {@link ResultsView}.
	 * 
	 * @return the {@link ResultsView}
	 */
	public abstract ResultsView getResultsView();
	
	/**
	 * Return the {@link ModuleSelectionView}.
	 * 
	 * @return the {@link ModuleSelectionView}
	 */
	public abstract ModuleSelectionView getModuleSelectionView();
}
