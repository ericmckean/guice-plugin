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

import junit.framework.TestCase;

/** 
 * Test the activator and therefore the plugin object and the module for our plugin for guice 
 * related errors.
 * This is important since if the plugin throws an uncaught exception, we do not get notified
 * instead Eclipse just runs without the plugin.
 * 
 * @author Darren Creutz <dcreutz@gmail.com>
 */
public class StartupTest extends TestCase {
	/**
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() {}
	
	/** 
	 * Create a new activator and therefore a new GuicePlugin.
	 */
	public void testActivatorConstructor() {
		@SuppressWarnings({"unused"})
		Activator activator = new Activator();
	}
}
