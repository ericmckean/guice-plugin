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
import com.google.inject.tools.snippets.CodeSnippetResult;
import com.google.inject.tools.snippets.ModuleSnippet;
import com.google.inject.tools.snippets.ModuleSnippet.ConstructorRepresentation;
import com.google.inject.tools.snippets.ModuleSnippet.DefaultConstructorRepresentation;

/**
 * Represents a single module in the module context.
 * 
 * @author Darren Creutz <dcreutz@gmail.com>
 */
public class ModuleRepresentationImpl implements ModuleRepresentation, CodeRunner.CodeRunListener {
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
  
  /**
   * (non-Javadoc)
   * @see com.google.inject.tools.module.ModuleRepresentation#getConstructors()
   */
  public Set<? extends ConstructorRepresentation> getConstructors() {
    return constructors;
  }
  
  /**
   * (non-Javadoc)
   * @see com.google.inject.tools.module.ModuleRepresentation#hasDefaultConstructor()
   */
  public boolean hasDefaultConstructor() {
    return hasDefaultConstructor;
  }
  
  /**
   * (non-Javadoc)
   * @see com.google.inject.tools.module.ModuleRepresentation#getName()
   */
  public String getName() {
    return name;
  }
  
  /**
   * (non-Javadoc)
   * @see com.google.inject.tools.module.ModuleRepresentation#isDirty()
   */
  public boolean isDirty() {
    return dirty;
  }
  
  /**
   * (non-Javadoc)
   * @see com.google.inject.tools.module.ModuleRepresentation#makeDirty()
   */
  public void makeDirty() {
    dirty = true;
  }
  
  /**
   * (non-Javadoc)
   * @see com.google.inject.tools.module.ModuleRepresentation#clean(com.google.inject.tools.code.CodeRunner)
   */
  public RunModuleSnippet clean(CodeRunner codeRunner) {
    codeRunner.addListener(this);
    RunModuleSnippet runnable = new RunModuleSnippet(codeRunner,this);
    codeRunner.queue(runnable);
    return runnable;
  }
  
  /**
   * (non-Javadoc)
   * @see com.google.inject.tools.code.CodeRunner.CodeRunListener#acceptCodeRunResult(com.google.inject.tools.snippets.CodeSnippetResult)
   */
  public void acceptCodeRunResult(CodeSnippetResult result) {
    if (result instanceof ModuleSnippet.ModuleResult) {
      ModuleSnippet.ModuleResult moduleResult = (ModuleSnippet.ModuleResult)result;
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
  
  /**
   * (non-Javadoc)
   * @see com.google.inject.tools.code.CodeRunner.CodeRunListener#acceptUserCancelled()
   */
  public void acceptUserCancelled() {
    //do nothing
  }
  
  /**
   * (non-Javadoc)
   * @see com.google.inject.tools.code.CodeRunner.CodeRunListener#acceptDone()
   */
  public void acceptDone() {
    //do nothing
  }
  
  /**
   * (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "Module Representation [" + constructor + "]";
  }
  
  @Override
  public boolean equals(Object object) {
    if (object instanceof ModuleRepresentation) {
      return ((ModuleRepresentation)object).getName().equals(getName());
    } else {
      return false;
    }
  }
  
  @Override
  public int hashCode() {
    return getName().hashCode();
  }
}