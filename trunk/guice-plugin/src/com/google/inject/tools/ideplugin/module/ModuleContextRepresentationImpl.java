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

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import com.google.inject.tools.ideplugin.snippets.BindingCodeLocation;
import com.google.inject.tools.ideplugin.snippets.CodeProblem;
import com.google.inject.tools.ideplugin.snippets.CodeSnippetResult;
import com.google.inject.tools.ideplugin.snippets.ModuleContextSnippet;
import com.google.inject.tools.ideplugin.code.CodeRunner;
import com.google.inject.tools.ideplugin.code.RunModuleContextSnippet;

/**
 * Standard implementation of the {@link ModuleContextRepresentation}.
 * 
 * @author Darren Creutz <dcreutz@gmail.com>
 */
public class ModuleContextRepresentationImpl implements ModuleContextRepresentation, CodeRunner.CodeRunListener {
  private final String title;
  private final Set<ModuleInstanceRepresentation> modules;
  private Map<String,BindingCodeLocation> bindings;
  private Set<? extends CodeProblem> problems;
  private boolean dirty;
  
  /**
   * Create a ModuleContextRepresentation with the given title.
   * 
   * @param title the name of the context
   */
  public ModuleContextRepresentationImpl(String title) {
    this.title = title;
    modules = new HashSet<ModuleInstanceRepresentation>();
    dirty = true;
  }
  
  /*
   * (non-Javadoc)
   * @see com.google.inject.tools.ideplugin.module.ModuleContextRepresentation#findLocation(java.lang.String)
   */
  public BindingCodeLocation findLocation(String theClass) {
    return bindings.get(theClass);
  }
  
  /*
   * (non-Javadoc)
   * @see com.google.inject.tools.ideplugin.module.ModuleContextRepresentation#markDirty()
   */
  public void markDirty() {
    dirty = true;
  }
  
  /*
   * (non-Javadoc)
   * @see com.google.inject.tools.ideplugin.module.ModuleContextRepresentation#isDirty()
   */
  public boolean isDirty() {
    return dirty;
  }
  
  /*
   * (non-Javadoc)
   * @see com.google.inject.tools.ideplugin.module.ModuleContextRepresentation#clean(com.google.inject.tools.ideplugin.code.CodeRunner)
   */
  public RunModuleContextSnippet clean(CodeRunner codeRunner) {
    codeRunner.addListener(this);
    RunModuleContextSnippet runnable = new RunModuleContextSnippet(codeRunner,this);
    codeRunner.queue(runnable);
    return runnable;
  }
  
  /*
   * (non-Javadoc)
   * @see com.google.inject.tools.ideplugin.code.CodeRunner.CodeRunListener#acceptCodeRunResult(com.google.inject.tools.ideplugin.snippets.CodeSnippetResult)
   */
  public void acceptCodeRunResult(CodeSnippetResult result) {
    if (result instanceof ModuleContextSnippet.ModuleContextResult) {
      ModuleContextSnippet.ModuleContextResult contextResult = (ModuleContextSnippet.ModuleContextResult)result;
      if (contextResult.getName().equals(this.getName())) {
        this.bindings = contextResult.getBindings();
        this.problems = contextResult.getProblems();
        dirty = false;
      } else {
        //TODO: what to do here?
      }
    } else {
      //TODO: what to do here?
    }
  }
  
  /*
   * (non-Javadoc)
   * @see com.google.inject.tools.ideplugin.code.CodeRunner.CodeRunListener#acceptUserCancelled()
   */
  public void acceptUserCancelled() {
    //do nothing
  }
  
  /*
   * (non-Javadoc)
   * @see com.google.inject.tools.ideplugin.code.CodeRunner.CodeRunListener#acceptDone()
   */
  public void acceptDone() {
    //do nothing
  }
  
  /*
   * (non-Javadoc)
   * @see com.google.inject.tools.ideplugin.module.ModuleContextRepresentation#getProblems()
   */
  public Set<? extends CodeProblem> getProblems() {
    return problems;
  }
  
  /**
   * (non-Javadoc)
   * @see com.google.inject.tools.ideplugin.module.ModuleContextRepresentation#getName()
   */
  public String getName() {
    return title;
  }
  
  /**
   * (non-Javadoc)
   * @see com.google.inject.tools.ideplugin.module.ModuleContextRepresentation#getModules()
   */
  public synchronized Set<ModuleInstanceRepresentation> getModules() {
    return new HashSet<ModuleInstanceRepresentation>(modules);
  }
  
  /**
   * (non-Javadoc)
   * @see com.google.inject.tools.ideplugin.module.ModuleContextRepresentation#add(com.google.inject.tools.ideplugin.module.ModuleInstanceRepresentation)
   */
  public synchronized ModuleContextRepresentation add(ModuleInstanceRepresentation module) {
    modules.add(module);
    return this;
  }
  
  /**
   * (non-Javadoc)
   * @see com.google.inject.tools.ideplugin.module.ModuleContextRepresentation#removeModule(com.google.inject.tools.ideplugin.module.ModuleInstanceRepresentation)
   */
  public synchronized void removeModule(ModuleInstanceRepresentation module) {
    modules.remove(module);
  }
  
  /**
   * (non-Javadoc)
   * @see com.google.inject.tools.ideplugin.module.ModuleContextRepresentation#contains(com.google.inject.tools.ideplugin.module.ModuleInstanceRepresentation)
   */
  public synchronized boolean contains(ModuleInstanceRepresentation module) {
    return modules.contains(module);
  }
  
  /**
   * (non-Javadoc)
   * @see com.google.inject.tools.ideplugin.module.ModuleContextRepresentation#contains(java.lang.String)
   */
  public synchronized boolean contains(String moduleName) {
    for (ModuleInstanceRepresentation module : modules) {
      if (module.getClassName().equals(moduleName)) return true;
    }
    return false;
  }
  
  @Override
  public boolean equals(Object object) {
    if (object instanceof ModuleContextRepresentation) {
      ModuleContextRepresentation otherModuleContext = (ModuleContextRepresentation)object;
      if (title.equals(otherModuleContext.getName())) {
        return modules.equals(otherModuleContext.getModules());
      }
    }
    return false;
  }
  
  @Override
  public int hashCode() {
    return title.hashCode();
  }
  
  @Override
  public String toString() {
    return "Module Context Representation [" + title + "]: " + modules;
  }
}
