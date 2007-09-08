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

import com.google.inject.tools.suite.snippets.bindings.BindingRepresentation;
import com.google.inject.tools.suite.snippets.bindings.KeyRepresentation;
import com.google.inject.tools.suite.snippets.problems.CodeProblem;

/**
 * Represents the location in code of where a binding occurs.
 * 
 * @author Darren Creutz (dcreutz@gmail.com)
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
          .<CodeProblem>emptySet());
      this.theClass = theClass;
    }
    
    public String getTheClass() {
      return theClass;
    }
  }
  
  /**
   * Represents an implicit binding.
   */
  public static class ImplicitBindingLocation extends CodeLocation {
    private static final long serialVersionUID = -5617466328980845643L;
    
    private final String theClass;
    
    public ImplicitBindingLocation(String theClass) {
      super(new StackTraceElement[0], null, -1, "is implicitly bound",
          Collections.<CodeProblem>emptySet());
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
  private final String bindToProvider;
  private final String bindToInstance;

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
      String bindTo, String bindToProvider, String bindToInstance,
      String moduleContext, String file, int location,
      String locationDescription, Set<? extends CodeProblem> problems) {
    super(stackTrace, file, location, locationDescription, problems);
    this.bindWhat = bindWhat;
    this.annotatedWith = annotatedWith;
    this.bindTo = bindTo;
    this.bindToInstance = bindToInstance;
    this.bindToProvider = bindToProvider;
    this.moduleContext = moduleContext;
  }
  
  public BindingCodeLocation(String moduleContext, KeyRepresentation key, BindingRepresentation binding) {
    super(null, binding.file(), binding.location(), binding.locationDescription(), binding.problems());
    this.bindWhat = key.bindWhat();
    this.annotatedWith = key.annotatedWith();
    this.bindTo = binding.boundTo();
    if (binding.boundConstant() != null) {
      this.bindToInstance = binding.boundConstant();
    } else {
      this.bindToInstance = binding.boundInstance();
    }
    this.bindToProvider = binding.boundProvider();
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
  
  /**
   * Return what provider this binding binds to.
   */
  public String bindToProvider() {
    return bindToProvider;
  }
  
  /**
   * Return what instance this binding binds to.
   */
  public String bindToInstance() {
    return bindToInstance;
  }
  
  @Override
  public String toString() {
    return "BindingCodeLocation: " + bindWhat + " annotated with " + annotatedWith +
        " is bound to " + bindTo + " at " + super.toString();
  }
}
