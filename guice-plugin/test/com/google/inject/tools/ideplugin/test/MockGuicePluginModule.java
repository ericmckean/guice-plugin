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

package com.google.inject.tools.ideplugin.test;

import org.easymock.EasyMock;
import com.google.inject.tools.ideplugin.Messenger;
import com.google.inject.tools.ideplugin.ActionsHandler;
import com.google.inject.tools.ideplugin.GuicePluginModule;
import com.google.inject.tools.ideplugin.module.ModuleManager;
import com.google.inject.tools.ideplugin.module.ModuleSelectionView;
import com.google.inject.tools.ideplugin.problem.ProblemsHandler;
import com.google.inject.tools.ideplugin.results.ResultsHandler;
import com.google.inject.tools.ideplugin.results.ResultsView;

/**
 * Guice {@link com.google.inject.Module} that mocks the plugin dependencies.
 * 
 * @author Darren Creutz <dcreutz@gmail.com>
 */
public class MockGuicePluginModule extends GuicePluginModule {
	private boolean useRealModuleManager = false;
	private boolean useRealResultsHandler = false;
	private boolean useRealProblemsHandler = false;
	
	/**
	 * Create a purely mocked module.
	 */
	public MockGuicePluginModule() {
	}
	
	/**
	 * Tell the module to use a real ModuleManager.
	 */
	public MockGuicePluginModule useRealModuleManager() {
		useRealModuleManager = true;
		return this;
	}
	
	/**
	 * Tell the module to use a real ResultsHandler.
	 */
	public MockGuicePluginModule useRealResultsHandler() {
		useRealResultsHandler = true;
		return this;
	}
	
	/**
	 * Tell the module to use a real ProblemsHandler.
	 */
	public MockGuicePluginModule useRealProblemsHandler() {
		useRealProblemsHandler = true;
		return this;
	}
	
	/**
	 * (non-Javadoc)
	 * @see com.google.inject.tools.ideplugin.GuicePluginModule#bindGuicePlugin()
	 */
	protected void bindGuicePlugin() {
		
	}
	
	/**
	 * (non-Javadoc)
	 * @see com.google.inject.tools.ideplugin.GuicePluginModule#bindModuleManager()
	 */
	@Override
	protected void bindModuleManager() {
		if (!useRealModuleManager) bindToEasyMockInstance(ModuleManager.class);
		else super.bindModuleManager();
	}
	
	/**
	 * (non-Javadoc)
	 * @see com.google.inject.tools.ideplugin.GuicePluginModule#bindResultsHandler()
	 */
	@Override
	protected void bindResultsHandler() {
		if (!useRealResultsHandler) bindToEasyMockInstance(ResultsHandler.class);
		else super.bindResultsHandler();
	}
	
	@Override
	protected void bindProblemsHandler() {
		if (!useRealProblemsHandler) bindToEasyMockInstance(ProblemsHandler.class);
		else super.bindProblemsHandler();
	}
	
	/**
	 * (non-Javadoc)
	 * @see com.google.inject.tools.ideplugin.GuicePluginModule#bindResultsView()
	 */
	protected void bindResultsView() {
		bindToEasyMockInstance(ResultsView.class);
	}
	
	/**
	 * (non-Javadoc)
	 * @see com.google.inject.tools.ideplugin.GuicePluginModule#bindModuleSelectionView()
	 */
	protected void bindModuleSelectionView() {
		bindToEasyMockInstance(ModuleSelectionView.class);
	}
	
	/**
	 * (non-Javadoc)
	 * @see com.google.inject.tools.ideplugin.GuicePluginModule#bindMessenger()
	 */
	protected void bindMessenger() {
		bindToEasyMockInstance(Messenger.class);
	}
	
	/**
	 * (non-Javadoc)
	 * @see com.google.inject.tools.ideplugin.GuicePluginModule#bindActionsHandler()
	 */
	protected void bindActionsHandler() {
		bindToEasyMockInstance(ActionsHandler.class);
	}
	
	@SuppressWarnings({"unchecked"})
	private void bindToEasyMockInstance(Class theClass) {
		bind(theClass).toInstance(EasyMock.createMock(theClass));
	}
}