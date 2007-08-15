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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import junit.framework.TestCase;
import com.google.inject.Binding;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.tools.ideplugin.code.CodeRunner;
import com.google.inject.tools.ideplugin.module.ModuleContextRepresentation;
import com.google.inject.tools.ideplugin.module.ModuleContextRepresentationImpl;
import com.google.inject.tools.ideplugin.snippets.CodeProblem;
import com.google.inject.tools.ideplugin.snippets.CodeSnippetResult;
import com.google.inject.tools.ideplugin.snippets.ModuleContextSnippet;
/**
 * Unit test the {@link ModuleContextRepresentationImpl}.
 * 
 * @author Darren Creutz <dcreutz@gmail.com>
 */
public class ModuleContextRepresentationTest extends TestCase {
  public void testModuleContextRepresentation() throws Exception {
    ModuleContextRepresentation moduleContext = 
      new ModuleContextRepresentationImpl("Working Module Context");
    CodeRunner codeRunner = new SimulatedCodeRunner();
    moduleContext.clean(codeRunner);
    codeRunner.run("", true);
    codeRunner.waitFor();
    assertFalse(moduleContext.isDirty());
    assertTrue(moduleContext.getName().equals("Working Module Context"));
    assertTrue(moduleContext
        .findLocation("com.google.inject.tools.ideplugin.test.MockInjectedInterface")
        .bindTo().equals("com.google.inject.tools.ideplugin.test.MockInjectedInterfaceImpl"));
  }
  
  public class SimulatedCodeRunner implements CodeRunner {
    private CodeRunListener listener;
    public void addListener(CodeRunListener listener) {
      this.listener = listener;
    }
    
    public boolean isCancelled() {
      return false;
    }
    
    public boolean isDone() {
      return true;
    }
    
    public boolean isDone(Runnable runnable) {
      return true;
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
    
    public void run(String label, boolean backgroundAutomatically) {
      notifyResult(null,simulatedSnippetResult());
    }
    
    private ModuleContextSnippet.ModuleContextResult simulatedSnippetResult() {
      Map<Key<?>,Binding<?>> bindings = new HashMap<Key<?>,Binding<?>>();
      Binding<?> binding = new MockBinding<com.google.inject.tools.ideplugin.test.MockInjectedInterface>(com.google.inject.tools.ideplugin.test.MockInjectedInterface.class,com.google.inject.tools.ideplugin.test.MockInjectedInterfaceImpl.class);
      bindings.put(Key.get(com.google.inject.tools.ideplugin.test.MockInjectedInterface.class), binding);
      return new ModuleContextSnippet.ModuleContextResult("Working Module Context",
          bindings, new HashSet<CodeProblem>());
    }
    
    public class MockBinding<T> implements Binding<T> {
      private final Class<T> bindWhat;
      private final Class<? extends T> bindTo;
      public MockBinding(Class<T> bindWhat,Class<? extends T> bindTo) {
        this.bindWhat = bindWhat;
        this.bindTo = bindTo;
      }
      public StackTraceElement getSource() {
        return null;
      }
      public Provider<T> getProvider() {
        return new MockProvider<T>(bindTo);
      }
      public Key<T> getKey() {
        return Key.get(bindWhat);
      }
      
      public class MockProvider<T> implements Provider<T> {
        private final Class<? extends T> bindsTo;
        public MockProvider(Class<? extends T> bindsTo) {
          this.bindsTo = bindsTo;
        }
        public T get() {
          T result;
          try {
            result = bindsTo.newInstance();
          } catch (Exception e) {
            result = null;
          }
          return result;
        }
      }
    }
  }
}
