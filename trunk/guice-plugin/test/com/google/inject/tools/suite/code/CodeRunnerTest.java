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

package com.google.inject.tools.suite.code;

import com.google.inject.tools.suite.JavaManager;
import com.google.inject.tools.suite.Fakes.FakeMessenger;
import com.google.inject.tools.suite.Fakes.FakeProgressHandler;
import com.google.inject.tools.suite.Fakes.TestSnippet;
import com.google.inject.tools.suite.code.CodeRunner;
import com.google.inject.tools.suite.code.CodeRunnerImpl;
import com.google.inject.tools.suite.snippets.CodeSnippetResult;

import junit.framework.TestCase;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Unit test the {@link CodeRunner}.
 * 
 * @author Darren Creutz (dcreutz@gmail.com)
 */
public class CodeRunnerTest extends TestCase implements
    CodeRunner.CodeRunListener {
  private static final URL CODEURL = 
    TestSnippet.class.getProtectionDomain().getCodeSource().getLocation();

  private boolean hitDone = false;
  private boolean hitResult = false;
  private static String CLASSPATH;
  
  @Override
  public void setUp() {
    hitDone = false;
    hitResult = false;
    try {
      String codeLocation = CODEURL.toURI().getPath();
      CLASSPATH = codeLocation.substring(codeLocation.indexOf('/'));
    } catch (Throwable t) {
      CLASSPATH = null;
    }
  }

  public void testCodeRunnerSimple() throws Exception {
    CodeRunner runner = new CodeRunnerImpl(new FakeJavaProject(),
        new FakeProgressHandler(), new FakeMessenger());
    runner.addListener(this);
    CodeRunner.Runnable runnable = new TestRunnable(runner);
    runner.queue(runnable);
    runner.run("", false);
    runner.waitFor();
    assertTrue(hitResult);
    assertTrue(hitDone);
  }

  public void testCodeRunnerLongProcess() throws Exception {
    CodeRunner runner = new CodeRunnerImpl(new FakeJavaProject(),
        new FakeProgressHandler(), new FakeMessenger());
    runner.addListener(this);
    CodeRunner.Runnable runnable = new TestRunnable(runner, 2000);
    runner.queue(runnable);
    runner.run("", false);
    runner.waitFor();
    assertTrue(hitResult);
    assertTrue(hitDone);
  }

  public void testCodeRunnerMultiple() throws Exception {
    CodeRunner runner = new CodeRunnerImpl(new FakeJavaProject(),
        new FakeProgressHandler(), new FakeMessenger());
    runner.addListener(this);
    CodeRunner.Runnable runnable1 = new TestRunnable(runner, 200);
    CodeRunner.Runnable runnable2 = new TestRunnable(runner, 400);
    runner.queue(runnable1);
    runner.queue(runnable2);
    runner.run("", false);
    runner.waitFor();
    assertTrue(hitResult);
    assertTrue(hitDone);
  }

  public void testCodeRunnerFlurry() throws Exception {
    CodeRunner runner = new CodeRunnerImpl(new FakeJavaProject(),
        new FakeProgressHandler(), new FakeMessenger());
    runner.addListener(this);
    for (int i = 0; i < 20; i++) {
      runner.queue(new TestRunnable(runner, i < 10 ? 100 : 300));
    }
    runner.run("", false);
    runner.waitFor();
    assertTrue(hitDone);
  }

  public void acceptCodeRunResult(CodeSnippetResult result) {
    hitResult = true;
    assertTrue(result instanceof TestSnippet.TestSnippetResult);
    assertTrue(((TestSnippet.TestSnippetResult) result).getBlah()
        .equals("blah"));
  }

  public void acceptDone() {
    hitDone = true;
  }

  public void acceptUserCancelled() {
    // do nothing
  }

  public static class FakeJavaProject implements JavaManager {
    public String getJavaCommand() throws Exception {
      return "java";
    }

    public String getProjectClasspath() throws Exception {
      return CLASSPATH;
    }

    public String getSnippetsClasspath() throws Exception {
      return "";
    }
    
    public String getGuiceClasspath() throws Exception {
      return "";
    }
    
    public String getClasspathDelimiter() {
      return ":";
    }
  }

  public static class TestRunnable extends CodeRunner.Runnable {
    private final int millisecsToTake;

    @Override
    public String label() {
      return "TestRunnable";
    }

    @Override
    protected List<? extends Object> getSnippetArguments() {
      List<String> args = new ArrayList<String>();
      if (millisecsToTake != -1) {
        args.add(String.valueOf(millisecsToTake));
      }
      return args;
    }

    public TestRunnable(CodeRunner codeRunner) {
      super(codeRunner);
      this.millisecsToTake = -1;
    }

    public TestRunnable(CodeRunner codeRunner, int millisecsToTake) {
      super(codeRunner);
      this.millisecsToTake = millisecsToTake;
    }

    @Override
    public String getFullyQualifiedSnippetClass() {
      return TestSnippet.class.getName();
    }

    @Override
    public void caughtException(Throwable e) {
      fail();
    }

    @Override
    public void gotErrorOutput(InputStream stream) {
      InputStreamReader ir = new InputStreamReader(stream);
      BufferedReader r = new BufferedReader(ir);
      String line;
      try {
        while ((line = r.readLine()) != null) {
          fail();
        }
      } catch (Exception e) {
      }
    }
  }
}
