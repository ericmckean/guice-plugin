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

package com.google.inject.tools;

import com.google.inject.tools.CodeRunnerTest;
import com.google.inject.tools.ModuleContextRepresentationTest;
import com.google.inject.tools.ModuleContextSnippetTest;
import com.google.inject.tools.ModuleManagerTest;
import com.google.inject.tools.ModuleRepresentationTest;
import com.google.inject.tools.ModuleSnippetTest;

import junit.framework.TestSuite;
import junit.framework.Test;

/**
 * Test suite of all the tests for the guice plugin that are not IDE specific.
 * 
 * @author Darren Creutz <dcreutz@gmail.com>
 */
public class AllTests {
  public static Test suite() {
    TestSuite suite = new TestSuite();
    
    suite.addTestSuite(CodeRunnerTest.class);
    suite.addTestSuite(ModuleContextRepresentationTest.class);
    suite.addTestSuite(ModuleRepresentationTest.class);
    suite.addTestSuite(ModuleManagerTest.class);
    suite.addTestSuite(ModuleSnippetTest.class);
    suite.addTestSuite(ModuleContextSnippetTest.class);
    
    return suite;
  }
}
