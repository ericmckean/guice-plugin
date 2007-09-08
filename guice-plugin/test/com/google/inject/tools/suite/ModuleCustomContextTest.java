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

package com.google.inject.tools.suite;

import com.google.inject.tools.ideplugin.eclipse.EclipseApplicationContext;
import com.google.inject.tools.suite.module.ClassNameUtility;
import com.google.inject.tools.suite.snippets.ModuleContextSnippet;

import junit.framework.TestCase;

import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

/**
 * Test that custom module contexts work.
 * 
 * @author Darren Creutz (dcreutz@gmail.com)
 */
public class ModuleCustomContextTest extends TestCase {
  public void testMyPluginHelper() throws Exception {
    String[] args = new String[3];
    args[0] = ClassNameUtility.shorten(EclipseApplicationContext.class.getName());
    args[1] = String.valueOf(-1);
    args[2] = EclipseApplicationContext.class.getName();
    Object obj = runASnippet(args);
    assertTrue(obj instanceof ModuleContextSnippet.ModuleContextResult);
    ModuleContextSnippet.ModuleContextResult result =
        (ModuleContextSnippet.ModuleContextResult) obj;
    assertTrue(result.getName().equals("EclipseApplicationContext"));
    assertTrue(result.getProblems().isEmpty());
    assertNotNull(result.getInjector());
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
}
