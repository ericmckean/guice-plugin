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
import com.google.inject.tools.ideplugin.code.CodeRunner;
import com.google.inject.tools.ideplugin.code.RunModuleSnippet;
import com.google.inject.tools.ideplugin.snippets.CodeSnippetResult;
import com.google.inject.tools.ideplugin.snippets.ModuleSnippet;
import com.google.inject.tools.ideplugin.snippets.ModuleSnippet.ConstructorRepresentation;
import com.google.inject.tools.ideplugin.snippets.ModuleSnippet.DefaultConstructorRepresentation;

/**
 * Represents a single module in the module context.
 * 
 * @author Darren Creutz <dcreutz@gmail.com>
 */
public class ModuleRepresentationImpl implements ModuleRepresentation, CodeRunner.CodeRunListener {
	private String name;
	private ConstructorRepresentation constructor;
  private boolean dirty;
  private Set<ConstructorRepresentation> constructors;
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
   * @see com.google.inject.tools.ideplugin.module.ModuleRepresentation#getConstructors()
   */
  public Set<ConstructorRepresentation> getConstructors() {
    return constructors;
  }
  
  /**
   * (non-Javadoc)
   * @see com.google.inject.tools.ideplugin.module.ModuleRepresentation#hasDefaultConstructor()
   */
  public boolean hasDefaultConstructor() {
    return hasDefaultConstructor;
  }
	
	/**
	 * (non-Javadoc)
	 * @see com.google.inject.tools.ideplugin.module.ModuleRepresentation#getName()
	 */
	public String getName() {
		return name;
	}
  
  /**
   * (non-Javadoc)
   * @see com.google.inject.tools.ideplugin.module.ModuleRepresentation#isDirty()
   */
  public boolean isDirty() {
    return dirty;
  }
  
  /**
   * (non-Javadoc)
   * @see com.google.inject.tools.ideplugin.module.ModuleRepresentation#makeDirty()
   */
  public void makeDirty() {
    dirty = true;
  }
  
  /**
   * (non-Javadoc)
   * @see com.google.inject.tools.ideplugin.module.ModuleRepresentation#clean(com.google.inject.tools.ideplugin.code.CodeRunner)
   */
  public void clean(CodeRunner codeRunner) {
    codeRunner.addListener(this);
    codeRunner.queue(new RunModuleSnippet(codeRunner,this));
    codeRunner.run();
  }
  
  /**
   * (non-Javadoc)
   * @see com.google.inject.tools.ideplugin.code.CodeRunner.CodeRunListener#acceptCodeRunResult(com.google.inject.tools.ideplugin.snippets.CodeSnippetResult)
   */
  public void acceptCodeRunResult(CodeSnippetResult result) {
    if (result instanceof ModuleSnippet.ModuleResult) {
      ModuleSnippet.ModuleResult moduleResult = (ModuleSnippet.ModuleResult)result;
      if (moduleResult.getName() == this.getName()) {
        this.constructors = moduleResult.getConstructors();
        this.hasDefaultConstructor = moduleResult.hasDefaultConstructor();
        if (!this.constructors.contains(constructor)) {
          if (this.hasDefaultConstructor) {
            this.constructor = new DefaultConstructorRepresentation();
          }
        }
        dirty = false;
      } else {
        //TODO: what to do here?
      }
    } else {
      //TODO: what to do here?
    }
  }
  
  /**
   * (non-Javadoc)
   * @see com.google.inject.tools.ideplugin.code.CodeRunner.CodeRunListener#acceptUserCancelled()
   */
  public void acceptUserCancelled() {
    //do nothing
  }
  
  /**
   * (non-Javadoc)
   * @see com.google.inject.tools.ideplugin.code.CodeRunner.CodeRunListener#acceptDone()
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
}