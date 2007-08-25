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

package com.google.inject.tools.module;

import java.util.Set;
import com.google.inject.tools.code.CodeRunner;
import com.google.inject.tools.code.RunModuleSnippet;
import com.google.inject.tools.snippets.CodeSnippetResult;
import com.google.inject.tools.snippets.ModuleSnippet;
import com.google.inject.tools.snippets.ModuleSnippet.ConstructorRepresentation;
import com.google.inject.tools.snippets.ModuleSnippet.DefaultConstructorRepresentation;

/**
 * {@inheritDoc ModuleRepresentation}
 * 
 * @author Darren Creutz <dcreutz@gmail.com>
 */
class ModuleRepresentationImpl implements ModuleRepresentation,
    CodeRunner.CodeRunListener {
  private String name;
  private ConstructorRepresentation constructor;
  private boolean dirty;
  private Set<? extends ConstructorRepresentation> constructors;
  private boolean hasDefaultConstructor;

  /**
   * Create a ModuleRepresentationImpl from a class name string.
   * 
   * @param className the class name
   */
  public ModuleRepresentationImpl(String className) {
    name = className;
    dirty = true;
    constructor = null;
    constructors = null;
    hasDefaultConstructor = false;
  }

  public Set<? extends ConstructorRepresentation> getConstructors() {
    return constructors;
  }

  public boolean hasDefaultConstructor() {
    return hasDefaultConstructor;
  }

  public String getName() {
    return name;
  }

  public boolean isDirty() {
    return dirty;
  }

  public void makeDirty() {
    dirty = true;
  }

  public RunModuleSnippet clean(CodeRunner codeRunner) {
    codeRunner.addListener(this);
    RunModuleSnippet runnable = new RunModuleSnippet(codeRunner, this);
    codeRunner.queue(runnable);
    return runnable;
  }

  public void acceptCodeRunResult(CodeSnippetResult result) {
    if (result instanceof ModuleSnippet.ModuleResult) {
      ModuleSnippet.ModuleResult moduleResult =
          (ModuleSnippet.ModuleResult) result;
      if (moduleResult.getName().equals(this.getName())) {
        this.constructors = moduleResult.getConstructors();
        this.hasDefaultConstructor = moduleResult.hasDefaultConstructor();
        if (!this.constructors.contains(constructor)) {
          if (this.hasDefaultConstructor) {
            this.constructor = new DefaultConstructorRepresentation();
          }
        }
        dirty = false;
      }
    }
  }

  public void acceptUserCancelled() {
    // do nothing
  }

  public void acceptDone() {
    // do nothing
  }

  @Override
  public String toString() {
    return "Module Representation [" + constructor + "]";
  }

  @Override
  public boolean equals(Object object) {
    if (object instanceof ModuleRepresentation) {
      return ((ModuleRepresentation) object).getName().equals(getName());
    } else {
      return false;
    }
  }

  @Override
  public int hashCode() {
    return getName().hashCode();
  }
}
