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

import junit.framework.TestCase;

import com.google.inject.tools.ideplugin.module.ModuleContextRepresentation;
import com.google.inject.tools.ideplugin.sample.BrokenModule;
import com.google.inject.tools.ideplugin.sample.MockInjectedInterface;
import com.google.inject.tools.ideplugin.sample.MockInjectedInterfaceImpl;
import com.google.inject.tools.ideplugin.sample.ModuleWithArguments;
import com.google.inject.tools.ideplugin.sample.WorkingModule;
import com.google.inject.tools.ideplugin.sample.WorkingModule2;
import com.google.inject.tools.ideplugin.snippets.BindingCodeLocation;
import com.google.inject.tools.ideplugin.snippets.CodeProblem;
import com.google.inject.tools.ideplugin.snippets.ModuleContextSnippet;
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
    public ThreadWithStream(OutputStream stream,String[] args) {
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
    new ThreadWithStream(os,args).start();
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
    args[2] = WorkingModule.class.getCanonicalName();
    args[3] = "0"; // number of args to WorkingModule
    Object obj = runASnippet(args);
    assertTrue(obj instanceof ModuleContextSnippet.ModuleContextResult);
    ModuleContextSnippet.ModuleContextResult result = (ModuleContextSnippet.ModuleContextResult)obj;
    System.out.println(result.getBindings());
    System.out.println(result.getProblems().iterator().next());
    assertTrue(result.getProblems().isEmpty());
    assertNotNull(result.getBindings());
    BindingCodeLocation location = result.getBindings().get(MockInjectedInterface.class.getCanonicalName());
    assertNotNull(location);
    assertTrue(location.bindTo().equals(MockInjectedInterfaceImpl.class.getCanonicalName()));
    assertTrue(location.file().equals("WorkingModule.java"));
    assertTrue(location.location() == WorkingModuleBindLocation);
  }
  
  private static final int WorkingModuleBindLocation = 36;
  
  /**
   * Test that constructing a broken module context causes a {@link com.google.inject.tools.ideplugin.snippets.CodeProblem.CreationProblem}.
   */
  public void testConstructBrokenModuleContext() throws Exception {
    String[] args = new String[4];
    args[0] = "Broken Module Context";
    args[1] = "1";
    args[2] = BrokenModule.class.getCanonicalName();
    args[3] = "0";
    Object obj = runASnippet(args);
    ModuleContextSnippet.ModuleContextResult result = (ModuleContextSnippet.ModuleContextResult)obj;
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
    args[2] = ModuleWithArguments.class.getCanonicalName();
    args[3] = "0";
    Object obj = runASnippet(args);
    ModuleContextSnippet.ModuleContextResult result = (ModuleContextSnippet.ModuleContextResult)obj;
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
    args[2] = WorkingModule.class.getCanonicalName();
    args[3] = "0"; // number of args to WorkingModule
    args[4] = WorkingModule2.class.getCanonicalName();
    args[5] = "0";
    Object obj = runASnippet(args);
    assertTrue(obj instanceof ModuleContextSnippet.ModuleContextResult);
    ModuleContextSnippet.ModuleContextResult result = (ModuleContextSnippet.ModuleContextResult)obj;
    assertTrue(result.getProblems().isEmpty());
    assertNotNull(result.getBindings());
    BindingCodeLocation location = result.getBindings().get(MockInjectedInterface.class.getCanonicalName());
    assertNotNull(location);
    assertTrue(location.bindTo().equals(MockInjectedInterfaceImpl.class.getCanonicalName()));
    assertTrue(location.file().equals("WorkingModule.java"));
    assertTrue(location.location() == WorkingModuleBindLocation);
  }
}
