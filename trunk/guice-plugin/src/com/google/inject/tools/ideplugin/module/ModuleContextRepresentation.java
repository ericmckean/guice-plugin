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

package com.google.inject.tools.ideplugin.module;

import java.util.Set;

import com.google.inject.tools.ideplugin.snippets.BindingCodeLocation;
import com.google.inject.tools.ideplugin.snippets.CodeProblem;
import com.google.inject.tools.ideplugin.code.CodeRunner;
import com.google.inject.tools.ideplugin.code.RunModuleContextSnippet;

/** 
 * Representation of a module context in the user's code.
 * 
 * @author Darren Creutz <dcreutz@gmail.com>
 */
public interface ModuleContextRepresentation {
  
  /**
   * Find the location in code where a binding occurs in this module context.
   * 
   * @param theClass the class to find the binding for
   * @return the location in code and/or problems finding the binding
   */
  public BindingCodeLocation findLocation(String theClass);
  
  /**
   * Add the module with the given name to this context.
   * 
   * @param module the module
   */
  public ModuleContextRepresentation add(ModuleInstanceRepresentation module);
  
  /**
   * Return the modules in this context.
   */
  public Set<ModuleInstanceRepresentation> getModules();
  
  /**
   * Remove the given module from this context.
   * 
   * @param module the module
   */
  public void removeModule(ModuleInstanceRepresentation module);
  
  /**
   * Return the name of this representation.
   * 
   * @return the name
   */
  public String getName();
  
  /**
   * Does this context have the given module in it?
   * 
   * @param module the module
   * @return true if the module is in this context
   */
  public boolean contains(ModuleInstanceRepresentation module);
  
  /**
   * Does this context contain the module with this name?
   * 
   * @param moduleName the name of the module
   * @return true if the module is in this context
   */
  public boolean contains(String moduleName);
  
  /**
   * Mark the module context as dirty, i.e. needing to be rerun in userspace.
   */
  public void markDirty();
  
  /**
   * Is the module context dirty?
   */
  public boolean isDirty();
  
  /**
   * Clean the context by rerunning it in userspace.
   * NOTE: The runnable returned will not actually be run until the client calls .run() on it.
   * 
   * @param codeRunner the {@link CodeRunner} to run the module context with
   */
  public RunModuleContextSnippet clean(CodeRunner codeRunner);
  
  /**
   * Return the set of {@link CodeProblem}s occurring with this context.
   */
  public Set<? extends CodeProblem> getProblems();
}