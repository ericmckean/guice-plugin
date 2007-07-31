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

import org.easymock.EasyMock;

import com.google.inject.tools.ideplugin.Messenger;
import com.google.inject.tools.ideplugin.ProgressHandler;
import com.google.inject.tools.ideplugin.eclipse.EclipsePluginModule;
import com.google.inject.tools.ideplugin.module.ModuleManager;
import com.google.inject.tools.ideplugin.module.ModuleSelectionView;
import com.google.inject.tools.ideplugin.module.ModulesListener;
import com.google.inject.tools.ideplugin.problem.ProblemsHandler;
import com.google.inject.tools.ideplugin.results.ResultsHandler;
import com.google.inject.tools.ideplugin.results.ResultsView;

/**
 * Guice module that mocks the eclipse plugin dependencies.
 * 
 * @author Darren Creutz <dcreutz@gmail.com>
 */
public class MockEclipsePluginModule extends EclipsePluginModule {
	private boolean useRealModuleManager = false;
	private boolean useRealResultsHandler = false;
	private boolean useRealProblemsHandler = false;
	private boolean useRealResultsView = false;
	private boolean useRealModuleSelectionView = false;
	private boolean useRealMessenger = false;
	private boolean useRealModulesListener = false;
	private boolean useRealProgressHandler = false;
	
	/**
	 * Create a purely mocked EclipsePluginModule.
	 */
	public MockEclipsePluginModule() {
	  super();
	}
	
	/**
	 * Tell the module to use a real ModuleManager.
	 */
	public MockEclipsePluginModule useRealModuleManager() {
		useRealModuleManager = true;
		return this;
	}
	
	/**
	 * Tell the module to use a real ResultsHandler.
	 */
	public MockEclipsePluginModule useRealResultsHandler() {
		useRealResultsHandler = true;
		return this;
	}
	
	/**
	 * Tell the module to use a real ErrorsHandler.
	 */
	public MockEclipsePluginModule useRealProblemsHandler() {
		useRealProblemsHandler = true;
		return this;
	}
	
	/**
	 * Tell the module to use a real ResultsView.
	 */
	public MockEclipsePluginModule useRealResultsView() {
		useRealResultsView = true;
		return this;
	}
	
	/**
	 * Tell the module to use a real ModuleSelectionView.
	 */
	public MockEclipsePluginModule useRealModuleSelectionView() {
		useRealModuleSelectionView = true;
		return this;
	}
	
	/**
	 * Tell the module to use a real Messenger.
	 */
	public MockEclipsePluginModule useRealMessenger() {
		useRealMessenger = true;
		return this;
	}
	
	/**
	 * Tell the module to use a real ModulesListener.
	 */
	public MockEclipsePluginModule useRealModulesListener() {
		useRealModulesListener = true;
		return this;
	}
	
	/**
	 * Tell the module to use a real ProgressHandler.
	 */
	public MockEclipsePluginModule useRealProgressHandler() {
		useRealProgressHandler = true;
		return this;
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
	
	/**
	 * (non-Javadoc)
	 * @see com.google.inject.tools.ideplugin.GuicePluginModule#bindProblemsHandler()
	 */
	@Override
	protected void bindProblemsHandler() {
		if (!useRealProblemsHandler) bindToEasyMockInstance(ProblemsHandler.class);
		else super.bindProblemsHandler();
	}
	
	/**
	 * (non-Javadoc)
	 * @see com.google.inject.tools.ideplugin.eclipse.EclipsePluginModule#bindResultsView()
	 */
	@Override
	protected void bindResultsView() {
		if (!useRealResultsView) bindToEasyMockInstance(ResultsView.class);
		else super.bindResultsView();
	}
	
	/**
	 * (non-Javadoc)
	 * @see com.google.inject.tools.ideplugin.eclipse.EclipsePluginModule#bindModuleSelectionView()
	 */
	@Override
	protected void bindModuleSelectionView() {
		if (!useRealModuleSelectionView) bindToEasyMockInstance(ModuleSelectionView.class);
		else super.bindModuleSelectionView();
	}
	
	/**
	 * (non-Javadoc)
	 * @see com.google.inject.tools.ideplugin.eclipse.EclipsePluginModule#bindMessenger()
	 */
	@Override
	protected void bindMessenger() {
		if (!useRealMessenger) bindToEasyMockInstance(Messenger.class);
		else super.bindMessenger();
	}
	
	/**
	 * (non-Javadoc)
	 * @see com.google.inject.tools.ideplugin.eclipse.EclipsePluginModule#bindModulesListener()
	 */
	@Override
	protected void bindModulesListener() {
		if (!useRealModulesListener) bindToEasyMockInstance(ModulesListener.class);
		else super.bindModulesListener();
	}
	
	/**
	 * (non-Javadoc)
	 * @see com.google.inject.tools.ideplugin.eclipse.EclipsePluginModule#bindProgressHandler()
	 */
	@Override
	protected void bindProgressHandler() {
		if (!useRealProgressHandler) bindToEasyMockInstance(ProgressHandler.class);
		else super.bindProgressHandler();
	}
	
	@SuppressWarnings({"unchecked"})
	private void bindToEasyMockInstance(Class theClass) {
		bind(theClass).toInstance(EasyMock.createMock(theClass));
	}
}