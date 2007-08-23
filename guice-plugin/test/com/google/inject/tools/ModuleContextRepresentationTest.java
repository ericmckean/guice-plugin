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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;
import com.google.inject.Binding;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.tools.Messenger;
import com.google.inject.tools.code.CodeRunner;
import com.google.inject.tools.module.ModuleContextRepresentation;
import com.google.inject.tools.module.ModuleContextRepresentationImpl;
import com.google.inject.tools.snippets.CodeProblem;
import com.google.inject.tools.snippets.CodeSnippetResult;
import com.google.inject.tools.snippets.ModuleContextSnippet;

/**
 * Unit test the {@link ModuleContextRepresentationImpl}.
 * 
 * @author Darren Creutz <dcreutz@gmail.com>
 */
public class ModuleContextRepresentationTest extends TestCase {
  public void testModuleContextRepresentation() throws Exception {
    ModuleContextRepresentation moduleContext = 
      new ModuleContextRepresentationImpl("Working Module Context", "WMC", "WMC");
    CodeRunner codeRunner = new SimulatedCodeRunner();
    moduleContext.clean(codeRunner);
    codeRunner.run("", true);
    codeRunner.waitFor();
    assertFalse(moduleContext.isDirty());
    assertTrue(moduleContext.getName().equals("Working Module Context"));
    assertTrue(moduleContext
        .findLocation(SampleModuleScenario.MockInjectedInterface.class.getName())
        .bindTo().equals(SampleModuleScenario.MockInjectedInterfaceImpl.class.getName()));
    //TODO: rest of stuff
  }
  
  public class SimulatedCodeRunner implements CodeRunner {
    private CodeRunListener listener;
    public void addListener(CodeRunListener listener) {
      this.listener = listener;
    }
    
    public Messenger getMessenger() {
      return new Messenger() {
        public void display(String message) {}
        public void logException(String label, Throwable throwable) {}
        public void logMessage(String message) {}
      };
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
    
    public void run(String label) {
      run(label, true);
    }
    
    private ModuleContextSnippet.ModuleContextResult simulatedSnippetResult() {
      Map<Key<?>,Binding<?>> bindings = new HashMap<Key<?>,Binding<?>>();
      Binding<?> binding = new MockBinding<com.google.inject.tools.SampleModuleScenario.MockInjectedInterface>(com.google.inject.tools.SampleModuleScenario.MockInjectedInterface.class,com.google.inject.tools.SampleModuleScenario.MockInjectedInterfaceImpl.class);
      bindings.put(Key.get(com.google.inject.tools.SampleModuleScenario.MockInjectedInterface.class), binding);
      return new ModuleContextSnippet.ModuleContextResult("Working Module Context",
          bindings, Collections.<CodeProblem>emptySet());
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
