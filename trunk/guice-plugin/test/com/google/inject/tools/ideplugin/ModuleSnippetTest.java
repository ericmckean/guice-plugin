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

import com.google.inject.tools.ideplugin.sample.MockInjectedInterface;
import com.google.inject.tools.ideplugin.sample.ModuleWithArguments;
import com.google.inject.tools.ideplugin.sample.WorkingModule;
import com.google.inject.tools.ideplugin.snippets.CodeProblem;
import com.google.inject.tools.ideplugin.snippets.ModuleSnippet;
import junit.framework.TestCase;

/**
 * Unit test the ModuleContextRepresentation object.
 * 
 * @author Darren Creutz <dcreutz@gmail.com>
 */
public class ModuleSnippetTest extends TestCase {
  /**
   * Test that a correctly written module is represented correctly.
   */
  public void testModule() {
    ModuleSnippet<WorkingModule> module = new ModuleSnippet<WorkingModule>(WorkingModule.class.getCanonicalName());
    assertNotNull(module);
    assertTrue(module.getConstructors().size() > 0);
    assertTrue(module.hasDefaultConstructor());
    assertNotNull(module.getInstance());
  }
  
  /**
   * Test that creating a representation of a class that is not a module fails correctly.
   */
  public void testNotAModule() {
    @SuppressWarnings("unchecked")
    ModuleSnippet module = new ModuleSnippet(MockInjectedInterface.class.getCanonicalName());
    assertFalse(module.isValid());
    assertTrue(module.getProblems().size() == 1);
    assertTrue(module.getProblems().iterator().next() instanceof CodeProblem.InvalidModuleProblem);
  }
  
  /**
   * Test that creating a representation of a module with no default constructor behaves correctly.
   */
  public void testModuleWithArguments() {
    ModuleSnippet<ModuleWithArguments> module = new ModuleSnippet<ModuleWithArguments>(ModuleWithArguments.class.getCanonicalName());
    assertFalse(module.hasDefaultConstructor());
    assertTrue(module.getConstructors().size() == 1);
  }
}
