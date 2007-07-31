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

package com.google.inject.tools.ideplugin.bindings;

import com.google.inject.tools.ideplugin.JavaElement;
import com.google.inject.tools.ideplugin.module.ModuleManager;
import com.google.inject.tools.ideplugin.module.ModuleContextRepresentation;
import com.google.inject.tools.ideplugin.problem.ProblemsHandler;
import com.google.inject.tools.ideplugin.results.CodeLocationsResults;
import com.google.inject.tools.ideplugin.results.ResultsHandler;
import com.google.inject.tools.ideplugin.ProgressHandler;

/**
 * The BindingsEngine is the glue between the other objects; it is responsible
 * for the top-level logic of the user asking the plugin to locate bindings of
 * a java expression.
 * 
 * @author Darren Creutz <dcreutz@gmail.com>
 */
public final class BindingsEngine {
	private final Class<?> theClass;
	private final CodeLocationsResults results;
	
	/**
	 * Create a BindingsEngineImpl.  This should be created by the {@link com.google.inject.tools.ideplugin.GuicePlugin}.
	 * 
	 * @param resultsHandler the ResultsHandler to send results to (injected)
	 * @param problemsHandler the ProblemsHandler to notify with problems (injected)
	 * @param moduleManager the ModuleManager to ask for what context to run in (injected)
	 * @param element the JavaElement to find bindings for (not injected)
	 */
	//@AssistedInject
	public BindingsEngine(ModuleManager moduleManager,
							  ProblemsHandler problemsHandler,
							  ResultsHandler resultsHandler,
							  ProgressHandler progressHandler,
							  JavaElement element) {
		boolean userCancelled = false;
		theClass = element.getTheClass();
		results = new CodeLocationsResults("Bindings for " + element.toString());
		progressHandler.initialize(moduleManager.getModuleContexts().size());
		for (ModuleContextRepresentation moduleContext : moduleManager.getModuleContexts()) {
			userCancelled = !progressHandler.step("Finding Bindings for " + element.toString() + " in context " + moduleContext.getName());
			if (userCancelled) break;
			bindingsEnginePerModuleContext(moduleContext,problemsHandler);
		}
		if (userCancelled) results.userCancelled();
		resultsHandler.displayLocationsResults(results);
	}
	
	private void bindingsEnginePerModuleContext(ModuleContextRepresentation moduleContext,ProblemsHandler problemsHandler) {
		@SuppressWarnings("unchecked") //cannot parameterize new BindingLocater<theClass>
		BindingLocater<?> locater = new BindingLocater(theClass,moduleContext);
		BindingCodeLocation location = locater.getLocation();
		problemsHandler.foundProblems(locater.getProblems());
		results.put(moduleContext,location);
	}
}
