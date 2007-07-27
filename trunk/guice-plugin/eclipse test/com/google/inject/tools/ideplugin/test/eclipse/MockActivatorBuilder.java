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

package com.google.inject.tools.ideplugin.test.eclipse;

import com.google.inject.tools.ideplugin.eclipse.Activator;
import com.google.inject.tools.ideplugin.eclipse.EclipsePluginModule;
import com.google.inject.tools.ideplugin.test.eclipse.MockEclipsePluginModule;

/**
 * Builds mock activator objects (and hence modules) for testing purposes.
 * Methods are static to preserve Eclipse's model of having the Activator be statically referenced.
 * 
 * @author Darren Creutz <dcreutz@gmail.com>
 */
public class MockActivatorBuilder {
	/**
	 * Builds an activator from the given module.
	 * 
	 * @param module the module
	 */
	public static void buildActivator(EclipsePluginModule module) {
		@SuppressWarnings({"unused"})
		Activator activator = new Activator(module);
	}
	
	/**
	 * Builds an activator with mock objects per parameters.
	 * 
	 * @param useRealModuleManager whether to use a real ModuleManager
	 * @param useRealResultsHandler whether to use a real ResultsHandler
	 * @param useRealProblemsHandler whether to use a real ProblemsHandler
	 * @param useRealResultsView whether to use a real ResultsView
	 * @param useRealModuleSelectionView whether to use a real ModuleSelectionView
	 * @param useRealMessenger whether to use a real Messenger
	 */
	public static void buildMockActivator(
			boolean useRealModuleManager,
			boolean useRealResultsHandler,
			boolean useRealProblemsHandler,
			boolean useRealResultsView,
			boolean useRealModuleSelectionView,
			boolean useRealMessenger) {
		MockEclipsePluginModule module = new MockEclipsePluginModule();
		if (useRealModuleManager) module.useRealModuleManager();
		if (useRealResultsHandler) module.useRealResultsHandler();
		if (useRealProblemsHandler) module.useRealProblemsHandler();
		if (useRealResultsView) module.useRealResultsView();
		if (useRealModuleSelectionView) module.useRealModuleSelectionView();
		if (useRealMessenger) module.useRealMessenger();
		buildActivator(module);
	}
	
	/**
	 * Builds an activator using only mock objects.
	 */
	public static void buildPurelyMockActivator() {
		buildMockActivator(false,false,false,false,false,false);
	}
}
