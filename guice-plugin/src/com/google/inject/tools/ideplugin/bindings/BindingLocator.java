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

package com.google.inject.tools.ideplugin.bindings;

import com.google.inject.tools.module.ModuleContextRepresentation;
import com.google.inject.tools.snippets.BindingCodeLocation;

/**
 * The BindingLocater performs the actual determination of what an interface is bound to
 * in a given module and where in the source code that happens.
 * 
 * @author Darren Creutz <dcreutz@gmail.com>
 */
public class BindingLocator {
  private final String theClass;
  private final ModuleContextRepresentation moduleContext;
  private final BindingCodeLocation location;
  
  public BindingLocator(String theClass,ModuleContextRepresentation moduleContext) {
    this.theClass = theClass;
    this.moduleContext = moduleContext;
    this.location = moduleContext.findLocation(theClass);
  }
  
  /**
   * Return the class we are finding bindings for.
   */
  public String getTheClass() {
    return theClass;
  }
  
  /**
   * Return the module context we are running in.
   */
  public ModuleContextRepresentation getModuleContext() {
    return moduleContext;
  }
  
  /**
   * Return the code location where the binding happens (and/or problems finding it).
   */
  public BindingCodeLocation getCodeLocation() {
    return location;
  }
}