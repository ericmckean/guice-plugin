/**
 * Copyright (C) 2007 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.inject.tools.ideplugin.eclipse;

import com.google.inject.tools.ideplugin.AllNonIDESpecificTests;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Run all the tests for the Eclipse version of the plugin.
 * 
 * @author Darren Creutz <dcreutz@gmail.com>
 */
public class EclipseAllTests extends TestCase {
  public static Test suite() {
    TestSuite suite = new TestSuite();

    suite.addTest(AllNonIDESpecificTests.suite());

    suite.addTestSuite(EclipseJavaElementTest.class);
    suite.addTestSuite(StartupTest.class);

    return suite;
  }
}
