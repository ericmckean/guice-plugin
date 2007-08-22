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

package com.google.inject.tools.module;

import java.util.Set;

import com.google.inject.tools.code.CodeRunner;
import com.google.inject.tools.code.RunModuleSnippet;
import com.google.inject.tools.snippets.ModuleSnippet.ConstructorRepresentation;

/** 
 * Representation of a module context in the user's code.
 * 
 * @author Darren Creutz <dcreutz@gmail.com>
 */
public interface ModuleRepresentation {
  /**
   * Return the name of this module.
   * 
   * @return the module name
   */
  public String getName();
  
  /**
   * Return true if the module has a default (no argument) constructor.
   */
  public boolean hasDefaultConstructor();
  
  /**
   * Return all the constructors the module has.
   */
  public Set<? extends ConstructorRepresentation> getConstructors();
  
  /**
   * Return true if the module is dirty, i.e. has changed since it was last run.
   */
  public boolean isDirty();
  
  /**
   * Mark the module as dirty.  This is called by the {@link ModuleManager} 
   * in response to the {@link ModulesSource}.
   */
  public void makeDirty();
  
  /**
   * Tell the module to clean itself (run itself).
   * NOTE: The runnable returned will not actually be run until the client calls .run() on it.
   * 
   * @param codeRunner the {@link CodeRunner} to run the {@link com.google.inject.tools.snippets.ModuleSnippet} with.
   */
  public RunModuleSnippet clean(CodeRunner codeRunner);
}