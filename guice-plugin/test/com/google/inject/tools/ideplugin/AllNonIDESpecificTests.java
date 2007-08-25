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

package com.google.inject.tools.ideplugin;

import com.google.inject.tools.suite.AllTests;

import junit.framework.TestSuite;
import junit.framework.Test;

/**
 * Test suite of all the tests for the guice plugin that are not IDE specific.
 * 
 * @author Darren Creutz <dcreutz@gmail.com>
 */
public class AllNonIDESpecificTests {
  public static Test suite() {
    TestSuite suite = new TestSuite();

    suite.addTest(AllTests.suite());

    suite.addTestSuite(CodeLocationsResultsTest.class);
    suite.addTestSuite(ResultsHandlerTest.class);

    return suite;
  }
}
