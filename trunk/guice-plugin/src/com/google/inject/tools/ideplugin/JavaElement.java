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

package com.google.inject.tools.ideplugin;

import com.google.inject.tools.suite.JavaManager;

/**
 * Representation of a Java element. Likely just a wrapper around IDE specific
 * representations, for instance EclipseJavaElement is a wrapper around
 * Eclipse's IJavaElement.
 * 
 * @author Darren Creutz <dcreutz@gmail.com>
 */
public interface JavaElement {
  /**
   * The types of Java elements that can be represented.
   */
  public enum Type {
    FIELD, PARAMETER
  }

  /**
   * Return the type of this element.
   * 
   * @return the type
   */
  public Type getType();

  /**
   * Return the name of the class of this element. For fields and variables this
   * is the class of the field/variable. For methods it is the class they are
   * part of.
   * 
   * @return the class name
   */
  public String getClassName();

  /**
   * Return the name of this element.
   * 
   * @return the name
   */
  public String getName();

  /**
   * Return the project that this element is part of.
   * 
   * @return the project
   */
  public JavaManager getJavaProject();

  /**
   * Return true if the java element is injected at this location in code.
   */
  public boolean isInjectionPoint();
  
  /**
   * Return the annotations on this element (if it is at an injection point).
   */
  public String getAnnotations();
}
