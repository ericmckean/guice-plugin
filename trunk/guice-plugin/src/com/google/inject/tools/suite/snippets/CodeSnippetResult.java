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

package com.google.inject.tools.suite.snippets;

import java.util.Set;
import java.io.Serializable;

import com.google.inject.tools.suite.snippets.problems.CodeProblem;

/**
 * Abstract object holding the result of running a {@link CodeSnippet}. NOTE:
 * This must not involve any user code dependencies nor any outside the snippets
 * package.
 * 
 * @author Darren Creutz (dcreutz@gmail.com)
 */
public abstract class CodeSnippetResult implements Serializable {
  protected final Set<? extends CodeProblem> problems;

  /**
   * Create a new CodeSnippetResult object.
   * 
   * @param problems the problems associated with the snippet
   */
  public CodeSnippetResult(Set<? extends CodeProblem> problems) {
    this.problems = problems;
  }

  /**
   * Return the problems for the snippet.
   */
  public Set<? extends CodeProblem> getProblems() {
    return problems;
  }
  
  /**
   * Return all problems for the snippet.
   */
  public Set<? extends CodeProblem> getAllProblems() {
    return problems;
  }
}
