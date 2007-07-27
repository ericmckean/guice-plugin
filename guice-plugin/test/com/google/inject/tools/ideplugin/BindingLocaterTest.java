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
import com.google.inject.tools.ideplugin.bindings.BindingLocater;
import com.google.inject.tools.ideplugin.bindings.BindingCodeLocation;
import com.google.inject.tools.ideplugin.module.ModuleContextRepresentation;
import com.google.inject.tools.ideplugin.module.ModuleContextRepresentationImpl;
import com.google.inject.tools.ideplugin.module.ModuleRepresentationImpl;
import com.google.inject.tools.ideplugin.test.WorkingModule;
import com.google.inject.tools.ideplugin.code.CodeProblem;
import com.google.inject.tools.ideplugin.test.BrokenModule;
import com.google.inject.tools.ideplugin.test.MockInjectedInterface;

/**
 * Unit test the {@link BindingLocater}.
 * 
 * @author Darren Creutz <dcreutz@gmail.com>
 */
public class BindingLocaterTest extends TestCase {
	private static final ModuleContextRepresentation workingModule =
		new ModuleContextRepresentationImpl("Working Module Context")
			.add(new ModuleRepresentationImpl(WorkingModule.class));
	private static final ModuleContextRepresentation brokenModule =
		new ModuleContextRepresentationImpl("Broken Module Context")
			.add(new ModuleRepresentationImpl(BrokenModule.class));

	/*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() {}
	
	/**
	 * Test that the locater finds the correct binding from a properly built module.
	 */
	public void testFindsCorrectBinding() {
		BindingLocater locater = new BindingLocater(MockInjectedInterface.class,workingModule);
		assertTrue(locater.getProblems().isEmpty());
		BindingCodeLocation location = locater.getLocation();
		assertTrue(location.bindWhat().equals(MockInjectedInterface.class));
		assertTrue(location.getDisplayName().equals("com.google.inject.tools.ideplugin.test.MockInjectedInterfaceImpl"));
		assertTrue(location.file().equals("WorkingModule.java"));
		assertTrue(location.location() == 33);
	}
	
	/**
	 * Test that the locater correctly discovers when no binding is available.
	 */
	public void testNoBindingAvailable() {
		BindingLocater locater = new BindingLocater(JavaElement.class,workingModule);
		assertFalse(locater.getProblems().isEmpty());
		assertTrue(locater.getProblems().size() == 1);
		for (CodeProblem problem : locater.getProblems()) {
			assertTrue(problem instanceof CodeProblem.NoBindingProblem);
		}
	}
	
	/**
	 * Test that the locater correctly reports CreationException errors.
	 */
	public void testInvalidModule() {
		BindingLocater locater = new BindingLocater(JavaElement.class,brokenModule);
		assertTrue(locater.getProblems().size() == 1);
		for (CodeProblem problem : locater.getProblems()) {
			assertTrue(problem instanceof CodeProblem.InvalidModuleContextProblem);
		}
	}
}
