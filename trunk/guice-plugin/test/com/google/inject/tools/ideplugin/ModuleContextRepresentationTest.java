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
import com.google.inject.Injector;
import com.google.inject.tools.ideplugin.module.ModuleContextRepresentation;
import com.google.inject.tools.ideplugin.module.ModuleContextRepresentationImpl;
import com.google.inject.tools.ideplugin.module.ModuleRepresentation;
import com.google.inject.tools.ideplugin.module.ModuleRepresentationImpl;
import com.google.inject.tools.ideplugin.test.WorkingModule;
import com.google.inject.tools.ideplugin.test.WorkingModule2;
import com.google.inject.tools.ideplugin.test.BrokenModule;
import com.google.inject.tools.ideplugin.test.ModuleWithArguments;
import com.google.inject.tools.ideplugin.test.MockInjectedInterface;
import com.google.inject.tools.ideplugin.test.MockInjectedInterfaceImpl;
import com.google.inject.tools.ideplugin.test.MockInjectedInterface2;
import com.google.inject.tools.ideplugin.test.MockInjectedInterface2Impl;

/**
 * Unit test the {@link ModuleContextRepresentation} object.
 * 
 * @author Darren Creutz <dcreutz@gmail.com>
 */
public class ModuleContextRepresentationTest extends TestCase {
	private static ModuleRepresentation workingModule = new ModuleRepresentationImpl(WorkingModule.class);
	private static ModuleRepresentation brokenModule = new ModuleRepresentationImpl(BrokenModule.class);
	private static ModuleRepresentation invalidModule = new ModuleRepresentationImpl(ModuleWithArguments.class);
	
	/*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() {}
	
	/**
	 * Test that constructing a working module context happens without problems.
	 */
	public void testConstructWorkingModuleContext() {
		ModuleContextRepresentation moduleContext = new ModuleContextRepresentationImpl("Working Module Context");
		try {
			moduleContext.add(workingModule);
		} catch (ModuleContextRepresentation.InvalidModuleException exception) {
			//exception.printStackTrace();
			assertTrue(false);
		}
		assertFalse(moduleContext.hasProblem());
		assertTrue(moduleContext.isValid());
	}
	
	/**
	 * Test that constructing a broken module context causes a {@link com.google.inject.tools.ideplugin.code.CodeProblem.CreationProblem}.
	 */
	public void testConstructBrokenModuleContext() {
		ModuleContextRepresentation moduleContext = new ModuleContextRepresentationImpl("Broken Module Context");
		try {
			moduleContext.add(brokenModule);
		} catch (ModuleContextRepresentation.InvalidModuleException exception) {
			//exception.printStackTrace();
			assertTrue(false);
		}
		assertTrue(moduleContext.hasProblem());
		assertFalse(moduleContext.isValid());
	}
	
	/**
	 * Test that constructing an invalid module context fails.
	 */
	public void testConstructInvalidModuleContext() {
		ModuleContextRepresentation moduleContext = new ModuleContextRepresentationImpl("Invalid Module Context");
		try {
			moduleContext.add(invalidModule);
			assertTrue(false);
		} catch (ModuleContextRepresentation.InvalidModuleException exception) {
		}
		assertFalse(moduleContext.isValid());
	}
	
	/**
	 * Test that the injector is created correctly.
	 */
	public void testMakeInjector() {
		ModuleContextRepresentation moduleContext = new ModuleContextRepresentationImpl("Working Module Context");
		moduleContext.add(workingModule);
		Injector injector = moduleContext.getInjector();
		assertNotNull(injector);
		MockInjectedInterface mockii = injector.getInstance(MockInjectedInterface.class);
		assertNotNull(mockii);
		assertTrue(mockii.getClass().equals(MockInjectedInterfaceImpl.class));
	}
	
	/**
	 * Test that using multiple modules in a single context works correctly.
	 */
	public void testMultipleModulesInjector() {
		ModuleContextRepresentation moduleContext = new ModuleContextRepresentationImpl("Working Module Context");
		moduleContext.add(workingModule);
		moduleContext.add(new ModuleRepresentationImpl(WorkingModule2.class));
		Injector injector = moduleContext.getInjector();
		assertNotNull(injector);
		MockInjectedInterface mockii = injector.getInstance(MockInjectedInterface.class);
		assertNotNull(mockii);
		assertTrue(mockii.getClass().equals(MockInjectedInterfaceImpl.class));
		MockInjectedInterface2 mockii2 = injector.getInstance(MockInjectedInterface2.class);
		assertNotNull(mockii2);
		assertTrue(mockii2.getClass().equals(MockInjectedInterface2Impl.class));
	}
}
