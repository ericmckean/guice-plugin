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

import java.util.Collections;
import java.util.HashSet;

import junit.framework.TestCase;

import com.google.inject.tools.ideplugin.code.CodeRunner;
import com.google.inject.tools.ideplugin.module.ModuleRepresentation;
import com.google.inject.tools.ideplugin.module.ModuleRepresentationImpl;
import com.google.inject.tools.ideplugin.snippets.CodeProblem;
import com.google.inject.tools.ideplugin.snippets.CodeSnippetResult;
import com.google.inject.tools.ideplugin.snippets.ModuleSnippet;
import com.google.inject.tools.ideplugin.snippets.ModuleSnippet.DefaultConstructorRepresentation;

/**
 * Unit test the {@link ModuleRepresentationImpl}.
 * 
 * @author Darren Creutz <dcreutz@gmail.com>
 */
public class ModuleRepresentationTest extends TestCase {
  public void testModuleRepresentation() {
    ModuleRepresentation module = new ModuleRepresentationImpl("com.google.inject.tools.ideplugin.test.WorkingModule");
    CodeRunner codeRunner = new SimulatedCodeRunner();
    module.clean(codeRunner);
    assertFalse(module.isDirty());
    assertTrue(module.hasDefaultConstructor());
    assertTrue(module.getName().equals("com.google.inject.tools.ideplugin.test.WorkingModule"));
    assertTrue(module.getConstructors().equals(Collections.singleton(new DefaultConstructorRepresentation())));
  }
  
  private class SimulatedCodeRunner implements CodeRunner {
    private CodeRunListener listener;
    public void addListener(CodeRunListener listener) {
      this.listener = listener;
    }
    
    public void kill() {
    }
    
    public void kill(Runnable runnable) {
    }
    
    public void waitFor() {
    }
    
    public void waitFor(Runnable runnable) {
    }
    
    public void notifyResult(Runnable runnable, CodeSnippetResult result) {
      listener.acceptCodeRunResult(result);
    }
    
    public void queue(Runnable runnable) {
    }
    
    public void run() {
      notifyResult(null,simulatedSnippetResult());
    }
    
    private ModuleSnippet.ModuleResult simulatedSnippetResult() {
      return new ModuleSnippet.ModuleResult("com.google.inject.tools.ideplugin.test.WorkingModule",
          new HashSet<CodeProblem>(), true, Collections.singleton(new DefaultConstructorRepresentation()));
    }
  }
}
