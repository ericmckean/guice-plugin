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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;
import junit.framework.TestCase;
import com.google.inject.tools.ideplugin.code.CodeRunner;
import com.google.inject.tools.ideplugin.code.CodeRunnerImpl;
import com.google.inject.tools.ideplugin.snippets.CodeSnippetResult;
import com.google.inject.tools.ideplugin.test.TestSnippet;

/**
 * Unit test the {@link CodeRunner}.
 * 
 * @author Darren Creutz <dcreutz@gmail.com>
 */
public class CodeRunnerTest extends TestCase implements CodeRunner.CodeRunListener {
  //TODO: dirty dirty dirty hack
  private static final String CLASSPATH = "/Users/d/Documents/workspace/Guice Plugin/bin";
  
  private boolean hitDone = false;
  private boolean hitResult = true;
  
  public void testCodeRunner() throws Exception {
    CodeRunner runner = new CodeRunnerImpl(new MockJavaProject());
    runner.addListener(this);
    CodeRunner.Runnable runnable = new TestRunnable(runner);
    runner.queue(runnable);
    runner.run();
    runner.waitFor(runnable);
    assertTrue(hitResult);
    assertTrue(hitDone);
  }
  
  public void acceptCodeRunResult(CodeSnippetResult result) {
    hitResult = true;
    assertTrue(result instanceof TestSnippet.TestSnippetResult);
    assertTrue(((TestSnippet.TestSnippetResult)result).getBlah().equals("blah"));
  }

  public void acceptDone() {
    hitDone = true;
  }

  public void acceptUserCancelled() {
    //do nothing
  }
  
  public static class MockJavaProject implements JavaProject {
    public String getJavaCommand() throws Exception {
      return "java";
    }

    public String getProjectClasspath() throws Exception {
      Bundle bundle = Platform.getBundle("guice plugin");
      return CLASSPATH;
    }

    public String getSnippetsClasspath() throws Exception {
      return "";
    }
  }
  
  public static class TestRunnable extends CodeRunner.Runnable {
    @Override
    protected List<? extends Object> getSnippetArguments() {
      return new ArrayList<Object>();
    }
    public TestRunnable(CodeRunner codeRunner) {
      super(codeRunner);
    }
    @Override
    public String getFullyQualifiedSnippetClass() {
      return "com.google.inject.tools.ideplugin.test.TestSnippet";
    }
    
    @Override
    public void caughtException(Exception e) {
      fail();
    }
    @Override
    public void gotErrorOutput(InputStream stream) {
      InputStreamReader ir = new InputStreamReader(stream);
      BufferedReader r = new BufferedReader(ir);
      String line;
      try {
      while ((line = r.readLine()) != null)
        fail();
      } catch (Exception e) {
      }
    }
  }
}