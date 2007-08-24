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

package com.google.inject.tools.snippets;

import java.util.Set;
import java.util.HashSet;
import java.io.OutputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;

/**
 * A CodeSnippet is a piece of code to be executed by the
 * {@link com.google.inject.tools.code.CodeRunner} in user space. Any code we
 * wish to execute in userspace must extend this and be in the snippets package.
 * A snippet *must* implement the main method as that is what the CodeRunner
 * will run. The main method should print the
 * {@link com.google.inject.tools.snippets.CodeSnippetResult} for itself to
 * System.out.
 * 
 * @author Darren Creutz <dcreutz@gmail.com>
 */
public abstract class CodeSnippet {
  protected Set<CodeProblem> problems;

  public CodeSnippet() {
    problems = new HashSet<CodeProblem>();
  }

  /**
   * Return all the {@link CodeProblem}s associated with this snippet.
   */
  public Set<? extends CodeProblem> getProblems() {
    return problems;
  }

  /**
   * Add the given problems to this code snippet.
   */
  public void addProblems(Set<? extends CodeProblem> newProblems) {
    problems.addAll(newProblems);
  }

  /**
   * Return a {@link CodeSnippetResult} for this snippet.
   */
  public abstract CodeSnippetResult getResult();

  /**
   * Prints out the result of this snippet to the given stream as an object.
   * 
   * @param out the output stream to print to (usually System.out)
   */
  public void printResult(OutputStream out) {
    try {
      ObjectOutputStream os = new ObjectOutputStream(out);
      CodeSnippetResult result = getResult();
      os.writeObject(result);
    } catch (IOException exception) {
      // do nothing, this will be found on the other end
    }
  }

  // public static abstract void main(String[] args);
}
