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
import java.util.Set;
import com.google.inject.CreationException;
import com.google.inject.spi.Message;
import com.google.inject.tools.Messenger;
import com.google.inject.tools.code.CodeRunner;
import com.google.inject.tools.snippets.CodeProblem;
import com.google.inject.tools.snippets.CodeSnippet;
import com.google.inject.tools.snippets.CodeSnippetResult;

/**
 * Fake objects for use in testing.
 * 
 * @author Darren Creutz <dcreutz@gmail.com>
 */
public class Fakes {
  public static class FakeCreationException extends CreationException {
    /**
     * Automatically generated serial version UID.
     */
    private static final long serialVersionUID = -6889671178292449161L;
    private static Set<Message> messages = Collections.singleton(new Message("Mock Guice Message."));
    
    /**
     * Create the Mock object.
     */
    public FakeCreationException() {
      super(messages);
    }
    
    /**
     * (non-Javadoc)
     * @see java.lang.Throwable#toString()
     */
    @Override
    public String toString() {
      return "Mock Creation Exception.";
    }
  }
  
  public static class TestSnippet extends CodeSnippet {
    public TestSnippet(int secsToTake) {
      super();
      try {
        Thread.sleep(secsToTake * 1000);
      } catch (Exception exception) {
        //do nothing
      }
    }
    
    public static class TestSnippetResult extends CodeSnippetResult {
      private final String blah = "blah";
      public TestSnippetResult() {
        super(Collections.<CodeProblem>emptySet());
      }
      public String getBlah() {
        return blah;
      }
    }
    
    @Override
    public CodeSnippetResult getResult() {
      return new TestSnippetResult();
    }
    public static void main(String[] args) {
      int secsToTake;
      if (args.length > 0) secsToTake = Integer.valueOf(args[0]);
      else secsToTake = -1;
      new TestSnippet(secsToTake).printResult(System.out);
    }
  }
  
  public static class FakeCodeRunner implements CodeRunner {
    public void addListener(CodeRunListener listener) {}
    public Messenger getMessenger() {
      return null;
    }
    public boolean isCancelled() {
      return false;
    }
    public boolean isDone() {
      return false;
    }
    public boolean isDone(Runnable runnable) {
      return false;
    }
    public void kill() {}
    public void kill(Runnable runnable) {}
    public void notifyResult(Runnable runnable, CodeSnippetResult result) {}
    public void queue(Runnable runnable) {}
    public void run(String label, boolean backgroundAutomatically) {}
    public void run(String label) {}
    public void waitFor() {}
    public void waitFor(Runnable runnable) {}
  }
  
  public static class FakeJavaManager implements JavaManager {
    public String getJavaCommand() throws Exception {
      return null;
    }
    public String getProjectClasspath() throws Exception {
      return null;
    }
    public String getSnippetsClasspath() throws Exception {
      return null;
    }
  }
}
