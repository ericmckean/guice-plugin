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
import com.google.inject.tools.ideplugin.test.eclipse.MockActivatorBuilder;

/** 
 * Test the Editor Action for "Find Bindings".
 * 
 * @author Darren Creutz <dcreutz@gmail.com>
 */
public class BindingsEditorActionTest extends TestCase {
	/** 
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() {
		MockActivatorBuilder.buildPurelyMockActivator();
	}
	
	/** 
	 * Test the run routine of the BindingsEditorAction
	 */
	public void testRunAction() {}
}
