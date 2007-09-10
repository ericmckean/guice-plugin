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

import com.google.inject.tools.suite.snippets.BindingCodeLocation.ImplicitBindingLocation;
import com.google.inject.tools.suite.snippets.BindingCodeLocation.NoBindingLocation;
import com.google.inject.tools.suite.snippets.problems.CodeProblem;

/**
 * Represents a location in the user's code space.
 * 
 * @author Darren Creutz (dcreutz@gmail.com)
 */
public abstract class CodeLocation extends CodeSnippetResult {
  
  public interface CodeLocationVisitor {
    public void visit(BindingCodeLocation location);
    public void visit(ImplicitBindingLocation location);
    public void visit(NoBindingLocation location);
  }
  
  private final String file;
  private final int location;
  private final String description;
  private final StackTraceElement[] stackTrace;

  /**
   * Create a new CodeLocation.
   */
  public CodeLocation(StackTraceElement[] stackTrace, String file,
      int location, String description, Set<? extends CodeProblem> problems) {
    super(problems);
    this.stackTrace = stackTrace;
    this.file = file;
    this.location = location;
    this.description = description;
  }

  /**
   * Return the file where the code lives.
   * 
   * @return the file name
   */
  public String file() {
    return file;
  }

  /**
   * Return the location in the file of the code snippet.
   * 
   * @return the line number
   */
  public int location() {
    return location;
  }
  
  /**
   * Return a description of this location (in case there is no file).
   */
  public String locationDescription() {
    return description;
  }

  /**
   * Return the stack trace to get this location.
   */
  public StackTraceElement[] getStackTrace() {
    return stackTrace;
  }
  
  public abstract void accept(CodeLocationVisitor visitor);

  @Override
  public String toString() {
    return file + ":" + String.valueOf(location);
  }
}
