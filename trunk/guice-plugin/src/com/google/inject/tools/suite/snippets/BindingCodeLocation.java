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

import java.util.Collections;
import java.util.Set;

/**
 * Represents the location in code of where a binding occurs.
 * 
 * @author Darren Creutz <dcreutz@gmail.com>
 */
public class BindingCodeLocation extends CodeLocation {
  private static final long serialVersionUID = -5452265177177754745L;

  /**
   * Represents the lack of a binding.
   */
  public static class NoBindingLocation extends CodeLocation {
    private static final long serialVersionUID = -5617466128980845643L;

    private final String theClass;
    
    public NoBindingLocation(String theClass) {
      super(new StackTraceElement[0], null, -1, "has no binding", Collections
          .<CodeProblem> emptySet());
      this.theClass = theClass;
    }
    
    public String getTheClass() {
      return theClass;
    }
  }

  private final String moduleContext;
  private final String bindWhat;
  private final String annotatedWith;
  private final String bindTo;

  /**
   * Create a new BindingCodeLocation.
   * 
   * @param bindWhat the class to bind
   * @param annotatedWith the annotation for this binding
   * @param bindTo what it is bound to
   * @param moduleContext the module context this binding happens in
   * @param file the file this happens in
   * @param location the line number in that file where this happens
   * @param problems any {@link CodeProblem}s that occurred during getting this
   *        binding
   */
  public BindingCodeLocation(StackTraceElement[] stackTrace, String bindWhat,
      String annotatedWith,
      String bindTo, String moduleContext, String file, int location,
      String locationDescription, Set<? extends CodeProblem> problems) {
    super(stackTrace, file, location, locationDescription, problems);
    this.bindWhat = bindWhat;
    this.annotatedWith = annotatedWith;
    this.bindTo = bindTo;
    this.moduleContext = moduleContext;
  }

  /**
   * Return the module context this binding occurred in.
   */
  public String getModuleContext() {
    return moduleContext;
  }

  /**
   * Return the Class being bound.
   */
  public String bindWhat() {
    return bindWhat;
  }
  
  /**
   * Return the annotations with this binding (null if there are none).
   */
  public String annotatedWith() {
    return annotatedWith;
  }

  /**
   * Return what this binding binds to.
   */
  public String bindTo() {
    return bindTo;
  }
}
