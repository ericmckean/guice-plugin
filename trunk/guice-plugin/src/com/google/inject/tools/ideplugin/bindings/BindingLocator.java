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

package com.google.inject.tools.ideplugin.bindings;

import com.google.inject.tools.suite.module.ModuleContextRepresentation;
import com.google.inject.tools.suite.snippets.CodeLocation;
import com.google.inject.tools.suite.snippets.CodeProblem;
import com.google.inject.tools.suite.snippets.BindingCodeLocation.NoBindingLocation;

import java.util.Collections;
import java.util.Set;

/**
 * The BindingLocater performs the actual determination of what an interface is
 * bound to in a given module and where in the source code that happens.
 * 
 * @author Darren Creutz (dcreutz@gmail.com)
 */
class BindingLocator {
  private final String theClass;
  private final String annotatedWith;
  private final ModuleContextRepresentation moduleContext;
  private final Set<CodeLocation> locations;
  private final Set<? extends CodeProblem> problems;

  /**
   * Locate the bindings for the given class in the given context.
   */
  public BindingLocator(String theClass,
      ModuleContextRepresentation moduleContext) {
    this(theClass, null, moduleContext);
  }
  
  /**
   * Locate the bindings for the given class with the given annotations in the given
   * context.
   */
  public BindingLocator(String theClass,
      String annotatedWith,
      ModuleContextRepresentation moduleContext) {
    this.theClass = theClass;
    this.moduleContext = moduleContext;
    this.annotatedWith = annotatedWith;
    if (annotatedWith != null) {
      CodeLocation location = moduleContext.findLocation(theClass, annotatedWith);
      if (location != null) {
        this.locations = Collections.singleton(location);
      } else {
        this.locations = 
          Collections.singleton((CodeLocation)new NoBindingLocation(theClass));
      }
    } else {
      Set<CodeLocation> locations = moduleContext.findLocations(theClass);
      if (locations.isEmpty()) {
        this.locations = 
          Collections.singleton((CodeLocation)new NoBindingLocation(theClass));
      } else {
        this.locations = locations;
      }
    }
    this.problems = moduleContext.getProblems();
  }

  /**
   * Return the class we are finding bindings for.
   */
  public String getTheClass() {
    return theClass;
  }
  
  /**
   * Return the annotations on the class we are finding bindings for.
   */
  public String getAnnotations() {
    return annotatedWith;
  }

  /**
   * Return the module context we are running in.
   */
  public ModuleContextRepresentation getModuleContext() {
    return moduleContext;
  }

  /**
   * Return the code locations where the binding happens (and/or problems 
   * finding it).
   */
  public Set<CodeLocation> getCodeLocations() {
    return locations;
  }
  
  /**
   * Return the problems associated with this binding.
   */
  public Set<? extends CodeProblem> getProblems() {
    return problems;
  }
}
