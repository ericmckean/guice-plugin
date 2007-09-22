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

package com.google.inject.tools.suite.module;

/**
 * Represents a custom module context defined by the user.
 * 
 * A custom context is defined by a class name and a method to call
 * that returns an iterable of {@link com.google.inject.Module}s.
 * 
 * @author Darren Creutz (dcreutz@gmail.com)
 */
public interface CustomModuleContextRepresentation extends
    ModuleContextRepresentation {
  /**
   * Return the name of the class to use.
   */
  public String getClassToUse();

  /**
   * Return the name of the method to call.
   */
  public String getMethodToCall();
}
