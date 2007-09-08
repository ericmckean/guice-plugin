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

package com.google.inject.tools.suite.snippets.problems;

import java.io.Serializable;
import com.google.inject.tools.suite.snippets.CodeLocation;

/**
 * Represents a problem found involving the user's guice code, such as a
 * CreationException. These are passed to the
 * {@link com.google.inject.tools.suite.ProblemsHandler} for realtime notification to
 * the user as well as passed along with {@link CodeLocation}s for display as
 * results.
 * 
 * @author Darren Creutz (dcreutz@gmail.com)
 */
public class CodeProblem implements Serializable {
  private static final long serialVersionUID = 705475501616525997L;
  
  protected String message;
  protected final StackTraceElement[] stacktrace;

  /**
   * Create a CodeProblem representation.
   * 
   * @param moduleContext the module context the problem occurred in
   * @param exception the underlying exception
   */
  public CodeProblem(Throwable exception) {
    if (exception != null) {
      this.message = exception.toString();
      this.stacktrace = exception.getStackTrace();
    } else {
      this.message = null;
      this.stacktrace = null;
    }
  }

  @Override
  public String toString() {
    return "Guice Code Problem: " + message;
  }

  /**
   * Return the message from the problem.
   */
  public String getMessage() {
    return message;
  }

  /**
   * Return the stack trace of the problem.
   */
  public StackTraceElement[] getStackTrace() {
    return stacktrace;
  }
}
