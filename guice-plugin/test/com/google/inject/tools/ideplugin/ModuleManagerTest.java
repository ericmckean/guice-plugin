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

import junit.framework.TestCase;
import org.easymock.EasyMock;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.tools.ideplugin.module.ModuleManager;
import com.google.inject.tools.ideplugin.module.ModuleContextRepresentation;
import com.google.inject.tools.ideplugin.module.ModuleContextRepresentationImpl;
import com.google.inject.tools.ideplugin.module.ModuleRepresentation;
import com.google.inject.tools.ideplugin.module.ModuleRepresentationImpl;
import com.google.inject.tools.ideplugin.module.ModulesListener;
import com.google.inject.tools.ideplugin.problem.ProblemsHandler;
import com.google.inject.tools.ideplugin.code.CodeProblem;
import com.google.inject.tools.ideplugin.test.WorkingModule;
import com.google.inject.tools.ideplugin.test.BrokenModule;
import com.google.inject.tools.ideplugin.test.ModuleWithArguments;
import com.google.inject.tools.ideplugin.test.MockGuicePluginModule;
import java.util.Set;
import java.util.HashSet;

/**
 * Unit test the ModuleManager implementation.
 * 
 * @author Darren Creutz <dcreutz@gmail.com>
 */
public class ModuleManagerTest extends TestCase {
	private static ModuleRepresentation workingModule =
		new ModuleRepresentationImpl(WorkingModule.class);
	private static ModuleRepresentation brokenModule =
		new ModuleRepresentationImpl(BrokenModule.class);
	private static ModuleRepresentation moduleWithArguments =
		new ModuleRepresentationImpl(ModuleWithArguments.class);
	
	private static ModuleContextRepresentation workingModuleContext = 
		new ModuleContextRepresentationImpl("Working Module Context").add(workingModule);
	private static ModuleContextRepresentation brokenModuleContext = 
		new ModuleContextRepresentationImpl("Broken Module Context").add(brokenModule);
	private static ModuleContextRepresentation emptyModuleContext = 
		new ModuleContextRepresentationImpl("Empty Module Context");
	
	private Injector injector;
	private ModulesListener modulesListener;
	private ProblemsHandler problemsHandler;
	
	/*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() {
		injector = Guice.createInjector((new MockGuicePluginModule()).useRealModuleManager());
		modulesListener = injector.getInstance(ModulesListener.class);
		EasyMock.expect(modulesListener.findModules()).andReturn(new HashSet<String>());
		EasyMock.replay(modulesListener);
		problemsHandler = injector.getInstance(ProblemsHandler.class);
	}
	
	/**
	 * Test that adding and removing module contexts works as expected.
	 */
	public void testAddRemoveModuleContexts() {
		ModuleManager moduleManager = injector.getInstance(ModuleManager.class);
		moduleManager.addModuleContext(workingModuleContext);
		assertTrue(moduleManager.getModuleContexts().contains(workingModuleContext));
		assertTrue(moduleManager.getModuleContexts().size() == 1);
		moduleManager.addModuleContext(emptyModuleContext);
		assertTrue(moduleManager.getModuleContexts().contains(workingModuleContext));
		assertTrue(moduleManager.getModuleContexts().contains(emptyModuleContext));
		assertTrue(moduleManager.getModuleContexts().size() == 2);
		moduleManager.removeModuleContext(workingModuleContext);
		assertTrue(moduleManager.getModuleContexts().contains(emptyModuleContext));
		assertTrue(moduleManager.getModuleContexts().size() == 1);
		moduleManager.addModuleContext(brokenModuleContext);
		assertTrue(moduleManager.getModuleContexts().contains(emptyModuleContext));
		assertTrue(moduleManager.getModuleContexts().contains(brokenModuleContext));
		assertTrue(moduleManager.getModuleContexts().size() == 2);
		moduleManager.clearModuleContexts();
		assertTrue(moduleManager.getModuleContexts().isEmpty());
	}
	
	/**
	 * Test that adding and removing modules works as expected.
	 */
	public void testAddRemoveModules() {
		ModuleManager moduleManager = injector.getInstance(ModuleManager.class);
		moduleManager.addModule(workingModule);
		assertTrue(moduleManager.getModules().contains(workingModule));
		assertTrue(moduleManager.getModules().size() == 1);
		moduleManager.addModule(moduleWithArguments);
		assertTrue(moduleManager.getModules().contains(workingModule));
		assertTrue(moduleManager.getModules().contains(moduleWithArguments));
		assertTrue(moduleManager.getModules().size() == 2);
		moduleManager.removeModule(workingModule);
		assertTrue(moduleManager.getModules().contains(moduleWithArguments));
		assertTrue(moduleManager.getModules().size() == 1);
		moduleManager.addModule(brokenModule);
		assertTrue(moduleManager.getModules().contains(moduleWithArguments));
		assertTrue(moduleManager.getModules().contains(brokenModule));
		assertTrue(moduleManager.getModules().size() == 2);
		moduleManager.clearModules();
		assertTrue(moduleManager.getModules().isEmpty());
	}
	
	/**
	 * Test that the ModuleManager correctly knows about {@link com.google.inject.tools.ideplugin.code.CodeProblem.CreationProblem}s.
	 */
	public void testFindsCreationProblems() {
		assertTrue(brokenModuleContext.hasProblem());
		assertTrue(brokenModuleContext.getProblem() instanceof CodeProblem.CreationProblem);
	}
	
	/**
	 * Test that the ModuleManager notifies the {@link ProblemsHandler} when the module context
	 * is broken.
	 */
	public void testNotifiesProblemsHandler() {
		ModuleManager moduleManager = injector.getInstance(ModuleManager.class);
		problemsHandler.foundProblem((CodeProblem)EasyMock.anyObject());
		EasyMock.replay(problemsHandler);
		moduleManager.addModuleContext(brokenModuleContext);
		EasyMock.verify(problemsHandler);
	}
	
	/**
	 * Test that the ModuleManager correctly adds and removes by name.
	 */
	public void testAddRemoveByName() {
		injector = Guice.createInjector(new MockGuicePluginModule().useRealModuleManager());
		modulesListener = injector.getInstance(ModulesListener.class);
		Set<String> moduleNames = new HashSet<String>();
		moduleNames.add("com.google.inject.tools.ideplugin.test.WorkingModule");
		EasyMock.expect(modulesListener.findModules()).andReturn(moduleNames);
		EasyMock.replay(modulesListener);
		ModuleManager moduleManager = injector.getInstance(ModuleManager.class);
		assertTrue(moduleManager.getModules().size() == 1);
		ModuleRepresentation module = moduleManager.getModules().iterator().next();
		assertTrue(module.getName().equals("com.google.inject.tools.ideplugin.test.WorkingModule"));
		moduleManager.removeModule("com.google.inject.tools.ideplugin.test.WorkingModule");
		assertTrue(moduleManager.getModules().isEmpty());
		moduleManager.addModule("com.google.inject.tools.ideplugin.test.WorkingModule");
		assertTrue(moduleManager.getModules().size() == 1);
		module = moduleManager.getModules().iterator().next();
		assertTrue(module.getName().equals("com.google.inject.tools.ideplugin.test.WorkingModule"));
	}
	
	/**
	 * Test that the ModuleManager correctly initializes modules.
	 */
	public void testInitializesModules() {
		injector = Guice.createInjector(new MockGuicePluginModule().useRealModuleManager());
		modulesListener = injector.getInstance(ModulesListener.class);
		Set<String> moduleNames = new HashSet<String>();
		moduleNames.add("com.google.inject.tools.ideplugin.test.WorkingModule");
		EasyMock.expect(modulesListener.findModules()).andReturn(moduleNames);
		EasyMock.replay(modulesListener);
		ModuleManager moduleManager = injector.getInstance(ModuleManager.class);
		assertTrue(moduleManager.getModules().size() == 1);
		ModuleRepresentation module = moduleManager.getModules().iterator().next();
		assertTrue(module.getName().equals("com.google.inject.tools.ideplugin.test.WorkingModule"));
	}
	
	/**
	 * Test that the ModuleManager correctly initializes working module contexts.
	 */
	public void testInitializesWorkingModuleContexts() {
		injector = Guice.createInjector(new MockGuicePluginModule().useRealModuleManager());
		modulesListener = injector.getInstance(ModulesListener.class);
		Set<String> moduleNames = new HashSet<String>();
		moduleNames.add("com.google.inject.tools.ideplugin.test.WorkingModule");
		EasyMock.expect(modulesListener.findModules()).andReturn(moduleNames);
		EasyMock.replay(modulesListener);
		ModuleManager moduleManager = injector.getInstance(ModuleManager.class);
		assertTrue(moduleManager.getModules().size() == 1);
		ModuleContextRepresentation moduleContext = moduleManager.getModuleContexts().iterator().next();
		assertTrue(moduleContext.getName().equals("com.google.inject.tools.ideplugin.test.WorkingModule"));
	}
	
	/**
	 * Test that the ModuleManager correctly fails to initialize broken module contexts.
	 */
	public void testInitializesBrokenModuleContexts() {
		injector = Guice.createInjector(new MockGuicePluginModule().useRealModuleManager());
		modulesListener = injector.getInstance(ModulesListener.class);
		Set<String> moduleNames = new HashSet<String>();
		moduleNames.add("com.google.inject.tools.ideplugin.test.BrokenModule");
		EasyMock.expect(modulesListener.findModules()).andReturn(moduleNames);
		EasyMock.replay(modulesListener);
		ModuleManager moduleManager = injector.getInstance(ModuleManager.class);
		assertTrue(moduleManager.getModuleContexts().isEmpty());
	}
}
