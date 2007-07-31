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

import com.google.inject.tools.ideplugin.GuicePlugin;
import com.google.inject.tools.ideplugin.module.ModuleSelectionView;
import com.google.inject.tools.ideplugin.results.ResultsView;

/**
 * Eclipse implementation of the GuicePlugin.
 * 
 * @author Darren Creutz <dcreutz@gmail.com>
 */
public class EclipseGuicePlugin extends GuicePlugin {
	private ResultsView resultsView;
	private ModuleSelectionView moduleSelectionView;
	
	/**
	 * Create an EclipseGuicePlugin.
	 * 
	 * @param module the guice module to inject from
	 */
	public EclipseGuicePlugin(EclipsePluginModule module) {
		super(module);
		this.resultsView = null;
		this.moduleSelectionView = null;
	}
	
	/**
	 * (non-Javadoc)
	 * @see com.google.inject.tools.ideplugin.GuicePlugin#getResultsView()
	 */
	@Override
	public ResultsView getResultsView() {
		return resultsView;
	}
	
	/**
	 * (non-Javadoc)
	 * @see com.google.inject.tools.ideplugin.GuicePlugin#getModuleSelectionView()
	 */
	@Override
	public ModuleSelectionView getModuleSelectionView() {
		return moduleSelectionView;
	}
	
	/**
	 * Set the ResultsView to use for the plugin.
	 * This is needed because of how Eclipse manages plugins.
	 * 
	 * @param resultsView the ResultsView
	 */
	public void setResultsView(ResultsView resultsView) {
		this.resultsView = resultsView;
	}
	
	/**
	 * Set the ModuleSelectionView to use for the plugin.
	 * This is needed because of how Eclipse manages plugins.
	 * 
	 * @param moduleSelectionView the ModuleSelectionView
	 */
	public void setModuleSelectionView(ModuleSelectionView moduleSelectionView) {
		this.moduleSelectionView = moduleSelectionView;
	}
}
