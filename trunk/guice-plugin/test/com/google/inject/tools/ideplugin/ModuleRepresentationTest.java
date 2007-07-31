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

import com.google.inject.tools.ideplugin.module.ModuleRepresentation;
import com.google.inject.tools.ideplugin.module.ModuleRepresentationImpl;
import com.google.inject.tools.ideplugin.test.MockGuicePluginModule;
import com.google.inject.tools.ideplugin.test.ModuleWithArguments;
import junit.framework.TestCase;

/**
 * Unit test the ModuleContextRepresentation object.
 * 
 * @author Darren Creutz <dcreutz@gmail.com>
 */
public class ModuleRepresentationTest extends TestCase {
	/*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() {}
	
	/**
	 * Test that a correctly written module is represented correctly.
	 */
	public void testModule() {
		ModuleRepresentation module = null;
		try {
			module = new ModuleRepresentationImpl(MockGuicePluginModule.class);
			assertNotNull(module);
			assertTrue(module.getConstructors().length > 0);
			assertTrue(module.hasDefaultConstructor());
			assertNotNull(module.getConstructor());
		} catch (Exception exception) {
			//exception.printStackTrace();
			assertTrue(false);
		}
		try {
			module.setConstructor((module.getConstructors())[0],null);
		} catch (Exception exception) {
			//exception.printStackTrace();
			assertTrue(false);
		}
		assertNotNull(module.getInstance());
		assertTrue(module.getInstance() instanceof MockGuicePluginModule);
	}
	
	/**
	 * Test that constructing a module representation from the name of the module as a string works.
	 */
	public void testModuleFromName() {
		try {
			ModuleRepresentation module = new ModuleRepresentationImpl("com.google.inject.tools.ideplugin.test.MockGuicePluginModule");
			assertNotNull(module.getInstance());
		} catch (Exception exception) {
			//exception.printStackTrace();
			assertTrue(false);
		}
	}
	
	/**
	 * Test that creating a representation of a class that is not a module fails correctly.
	 */
	public void testNotAModule() {
		try {
			new ModuleRepresentationImpl("com.google.inject.tools.ideplugin.test.MockJavaElement");
			assertTrue(false);
		} catch (ClassNotFoundException exception) {
			//exception.printStackTrace();
			assertTrue(false);
		} catch (ModuleRepresentation.ClassNotModuleException exception) {
			//test succeeded
		}
	}
	
	/**
	 * Test that creating a representation of a module with no default constructor behaves correctly.
	 */
	public void testModuleWithArguments() {
		try {
			ModuleRepresentation module = new ModuleRepresentationImpl(ModuleWithArguments.class);
			assertFalse(module.hasDefaultConstructor());
			assertTrue(module.getConstructors().length == 1);
		} catch (Exception exception) {
			//exception.printStackTrace();
			assertTrue(false);
		}
	}
}
