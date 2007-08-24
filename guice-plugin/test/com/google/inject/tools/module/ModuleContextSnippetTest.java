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

package com.google.inject.tools.module;

import junit.framework.TestCase;

import com.google.inject.tools.SampleModuleScenario;
import com.google.inject.tools.SampleModuleScenario.BrokenModule;
import com.google.inject.tools.SampleModuleScenario.MockInjectedInterface;
import com.google.inject.tools.SampleModuleScenario.MockInjectedInterface2;
import com.google.inject.tools.SampleModuleScenario.MockInjectedInterface2Impl;
import com.google.inject.tools.SampleModuleScenario.MockInjectedInterfaceImpl;
import com.google.inject.tools.SampleModuleScenario.ModuleWithArguments;
import com.google.inject.tools.SampleModuleScenario.WorkingModule;
import com.google.inject.tools.SampleModuleScenario.WorkingModule2;
import com.google.inject.tools.module.ModuleContextRepresentation;
import com.google.inject.tools.snippets.BindingCodeLocation;
import com.google.inject.tools.snippets.CodeProblem;
import com.google.inject.tools.snippets.ModuleContextSnippet;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

/**
 * Unit test the {@link ModuleContextRepresentation} object.
 * 
 * @author Darren Creutz <dcreutz@gmail.com>
 */
public class ModuleContextSnippetTest extends TestCase {
  public void testModuleContextSnippetModuleRepresentation() throws Exception {
    ModuleContextSnippet.ModuleRepresentation module =
        new ModuleContextSnippet.ModuleRepresentation(WorkingModule.class,
            null, null);
    assertNotNull(module.getInstance());
  }

  private class ThreadWithStream extends Thread {
    private final OutputStream stream;
    private final String[] args;

    public ThreadWithStream(OutputStream stream, String[] args) {
      this.stream = stream;
      this.args = args;
    }

    @Override
    public void run() {
      ModuleContextSnippet.runSnippet(stream, args);
    }
  }

  private Object runASnippet(String[] args) throws Exception {
    PipedInputStream is = new PipedInputStream();
    Object obj = null;
    PipedOutputStream os = new PipedOutputStream(is);
    new ThreadWithStream(os, args).start();
    ObjectInputStream ois = new ObjectInputStream(is);
    return ois.readObject();
  }

  /**
   * Test that constructing a working module context happens without problems
   * and that the correct binding location is constructed.
   */
  public void testConstructWorkingModuleContext() throws Exception {
    String[] args = new String[4];
    args[0] = "Working Module Context";
    args[1] = "1"; // number of modules
    args[2] = WorkingModule.class.getName();
    args[3] = "0"; // number of args to WorkingModule
    Object obj = runASnippet(args);
    assertTrue(obj instanceof ModuleContextSnippet.ModuleContextResult);
    ModuleContextSnippet.ModuleContextResult result =
        (ModuleContextSnippet.ModuleContextResult) obj;
    assertTrue(result.getProblems().isEmpty());
    assertNotNull(result.getBindings());
    BindingCodeLocation location =
        result.getBindings().get(MockInjectedInterface.class.getName());
    assertNotNull(location);
    assertTrue(location.bindTo().equals(
        MockInjectedInterfaceImpl.class.getName()));
    assertTrue(location.file().equals(WorkingModuleBindFile));
    assertTrue(location.location() == WorkingModuleBindLocation);
  }

  private static final int WorkingModuleBindLocation = 42;
  private static final String WorkingModuleBindFile =
      "SampleModuleScenario.java";
  private static final int WorkingModuleBindLocation2 = 77;
  private static final String WorkingModuleBindFile2 =
      "SampleModuleScenario.java";

  /**
   * Test that constructing a broken module context causes a
   * {@link com.google.inject.tools.snippets.CodeProblem.CreationProblem}.
   */
  public void testConstructBrokenModuleContext() throws Exception {
    String[] args = new String[4];
    args[0] = "Broken Module Context";
    args[1] = "1";
    args[2] = BrokenModule.class.getName();
    args[3] = "0";
    Object obj = runASnippet(args);
    ModuleContextSnippet.ModuleContextResult result =
        (ModuleContextSnippet.ModuleContextResult) obj;
    assertFalse(result.getProblems().isEmpty());
    assertTrue(result.getBindings().size() == 0);
    assertTrue(result.getProblems().iterator().next() instanceof CodeProblem.CreationProblem);
  }

  /**
   * Test that constructing an invalid module context fails.
   */
  public void testConstructInvalidModule() throws Exception {
    String[] args = new String[4];
    args[0] = "Invalid Module Context";
    args[1] = "1";
    args[2] = ModuleWithArguments.class.getName();
    args[3] = "0";
    Object obj = runASnippet(args);
    ModuleContextSnippet.ModuleContextResult result =
        (ModuleContextSnippet.ModuleContextResult) obj;
    assertFalse(result.getProblems().isEmpty());
    assertTrue(result.getProblems().iterator().next() instanceof CodeProblem.InvalidModuleProblem);
  }

  /**
   * Test that using multiple modules in a single context works correctly.
   */
  public void testMultipleModulesInjector() throws Exception {
    String[] args = new String[6];
    args[0] = "Working Module Context";
    args[1] = "2"; // number of modules
    args[2] = WorkingModule.class.getName();
    args[3] = "0"; // number of args to WorkingModule
    args[4] = WorkingModule2.class.getName();
    args[5] = "0";
    Object obj = runASnippet(args);
    assertTrue(obj instanceof ModuleContextSnippet.ModuleContextResult);
    ModuleContextSnippet.ModuleContextResult result =
        (ModuleContextSnippet.ModuleContextResult) obj;
    assertTrue(result.getProblems().isEmpty());
    assertNotNull(result.getBindings());
    BindingCodeLocation location =
        result.getBindings().get(MockInjectedInterface.class.getName());
    assertNotNull(location);
    assertTrue(location.bindTo().equals(
        MockInjectedInterfaceImpl.class.getName()));
    assertTrue(location.file().equals(WorkingModuleBindFile));
    assertTrue(location.location() == WorkingModuleBindLocation);
    BindingCodeLocation location2 =
        result.getBindings().get(MockInjectedInterface2.class.getName());
    assertNotNull(location);
    assertTrue(location2.bindTo().equals(
        MockInjectedInterface2Impl.class.getName()));
    assertTrue(location2.file().equals(WorkingModuleBindFile2));
    assertTrue(location2.location() == WorkingModuleBindLocation2);
  }

  public void testCustomModuleContext() throws Exception {
    String[] args = new String[4];
    args[0] = "Custom Context";
    args[1] = String.valueOf(-1);
    args[2] = SampleModuleScenario.CustomContextBuilder.class.getName();
    args[3] = "getModules";
    Object obj = runASnippet(args);
    assertTrue(obj instanceof ModuleContextSnippet.ModuleContextResult);
    ModuleContextSnippet.ModuleContextResult result =
        (ModuleContextSnippet.ModuleContextResult) obj;
    assertTrue(result.getProblems().isEmpty());
    assertNotNull(result.getBindings());
    BindingCodeLocation location =
        result.getBindings().get(MockInjectedInterface.class.getName());
    assertNotNull(location);
    assertTrue(location.bindTo().equals(
        MockInjectedInterfaceImpl.class.getName()));
    assertTrue(location.file().equals(WorkingModuleBindFile));
    assertTrue(location.location() == WorkingModuleBindLocation);
  }
  
  public void testStaticCustomModuleContext() throws Exception {
    String[] args = new String[4];
    args[0] = "Custom Context";
    args[1] = String.valueOf(-1);
    args[2] = SampleModuleScenario.StaticCustomContextBuilder.class.getName();
    args[3] = "getModules";
    Object obj = runASnippet(args);
    assertTrue(obj instanceof ModuleContextSnippet.ModuleContextResult);
    ModuleContextSnippet.ModuleContextResult result =
        (ModuleContextSnippet.ModuleContextResult) obj;
    assertTrue(result.getProblems().isEmpty());
    assertNotNull(result.getBindings());
    BindingCodeLocation location =
        result.getBindings().get(MockInjectedInterface.class.getName());
    assertNotNull(location);
    assertTrue(location.bindTo().equals(
        MockInjectedInterfaceImpl.class.getName()));
    assertTrue(location.file().equals(WorkingModuleBindFile));
    assertTrue(location.location() == WorkingModuleBindLocation);
  }
  
  public void testAnnotatedBindingCodeLocation() throws Exception {
    String[] args = new String[4];
    args[0] = "Working Module Context";
    args[1] = "1";
    args[2] = WorkingModule.class.getName();
    args[3] = "0";
    Object obj = runASnippet(args);
    assertTrue(obj instanceof ModuleContextSnippet.ModuleContextResult);
    ModuleContextSnippet.ModuleContextResult result =
      (ModuleContextSnippet.ModuleContextResult) obj;
    assertTrue(result.getProblems().isEmpty());
    assertNotNull(result.getBindings());
    //TODO: finish this test
  }
}
