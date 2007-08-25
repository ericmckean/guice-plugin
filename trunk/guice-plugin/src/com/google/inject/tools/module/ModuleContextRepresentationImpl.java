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

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import com.google.inject.tools.code.CodeRunner;
import com.google.inject.tools.code.RunModuleContextSnippet;
import com.google.inject.tools.snippets.BindingCodeLocation;
import com.google.inject.tools.snippets.CodeLocation;
import com.google.inject.tools.snippets.CodeProblem;
import com.google.inject.tools.snippets.CodeSnippetResult;
import com.google.inject.tools.snippets.ModuleContextSnippet;
import com.google.inject.tools.snippets.BindingCodeLocation.NoBindingLocation;
import com.google.inject.tools.snippets.ModuleContextSnippet.ModuleContextResult.KeyRepresentation;

/**
 * {@inheritDoc ModuleContextRepresentation}
 * 
 * @author Darren Creutz <dcreutz@gmail.com>
 */
class ModuleContextRepresentationImpl implements
    ModuleContextRepresentation, CodeRunner.CodeRunListener {
  private final String title;
  protected String longName;
  protected String shortName;
  private final Set<ModuleInstanceRepresentation> modules;
  private Map<KeyRepresentation, BindingCodeLocation> bindings;
  private Set<? extends CodeProblem> problems;
  private boolean dirty;

  public ModuleContextRepresentationImpl(String moduleClass) {
    this.title = moduleClass;
    this.shortName = ClassNameUtility.shorten(moduleClass);
    this.longName = "Guice.createInjector(new " + moduleClass + "())";
    modules = new HashSet<ModuleInstanceRepresentation>();
    dirty = true;
  }

  public String getShortName() {
    return shortName;
  }

  public String getLongName() {
    return longName;
  }

  public CodeLocation findLocation(String theClass, String annotatedWith) {
    KeyRepresentation key = new KeyRepresentation(theClass, annotatedWith);
    if (bindings.get(key) != null) {
      return bindings.get(key);
    } else {
      return new NoBindingLocation(theClass);
    }
  }
  
  public Set<CodeLocation> findLocations(String theClass) {
    Set<CodeLocation> locations = new HashSet<CodeLocation>();
    for (KeyRepresentation key : bindings.keySet()) {
      if (key.bindWhat.equals(theClass)) {
        locations.add(bindings.get(key));
      }
    }
    if (locations.isEmpty()) {
      locations.add(new NoBindingLocation(theClass));
    }
    return locations;
  }

  public void markDirty() {
    dirty = true;
  }

  public boolean isDirty() {
    return dirty;
  }

  public CodeRunner.Runnable clean(CodeRunner codeRunner) {
    codeRunner.addListener(this);
    RunModuleContextSnippet runnable =
        new RunModuleContextSnippet(codeRunner, this);
    codeRunner.queue(runnable);
    return runnable;
  }

  public void acceptCodeRunResult(CodeSnippetResult result) {
    if (result instanceof ModuleContextSnippet.ModuleContextResult) {
      ModuleContextSnippet.ModuleContextResult contextResult =
          (ModuleContextSnippet.ModuleContextResult) result;
      if (contextResult.getName().equals(this.getName())) {
        this.bindings = contextResult.getBindings();
        this.problems = contextResult.getProblems();
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

  public Set<? extends CodeProblem> getProblems() {
    return problems;
  }

  public String getName() {
    return title;
  }

  public synchronized Set<ModuleInstanceRepresentation> getModules() {
    return new HashSet<ModuleInstanceRepresentation>(modules);
  }

  public synchronized ModuleContextRepresentation add(
      ModuleInstanceRepresentation module) {
    modules.add(module);
    return this;
  }

  public synchronized void removeModule(ModuleInstanceRepresentation module) {
    modules.remove(module);
  }

  public synchronized boolean contains(ModuleInstanceRepresentation module) {
    return modules.contains(module);
  }

  public synchronized boolean contains(String moduleName) {
    for (ModuleInstanceRepresentation module : modules) {
      if (module.getClassName().equals(moduleName)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean equals(Object object) {
    if (object instanceof ModuleContextRepresentation) {
      ModuleContextRepresentation otherModuleContext =
          (ModuleContextRepresentation) object;
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
