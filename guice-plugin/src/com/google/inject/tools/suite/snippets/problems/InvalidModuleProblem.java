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

package com.google.inject.tools.suite.snippets.problems;

import com.google.inject.tools.suite.snippets.ModuleContextSnippet.ModuleRepresentation;

/**
 * Represents a problem that a module being used is not vaid.
 * 
 * @author Darren Creutz (dcreutz@gmail.com)
 */
public class InvalidModuleProblem extends CodeProblem {
  private final String moduleName;
  
  public InvalidModuleProblem(ModuleRepresentation module, Throwable throwable) {
    this(module.getName(), throwable);
  }
  
  public InvalidModuleProblem(String moduleName, Throwable throwable) {
    super(throwable);
    this.moduleName = moduleName;
  }
  
  public String moduleName() {
    return moduleName;
  }
  
  @Override
  public void accept(CodeProblemVisitor visitor) {
    visitor.visit(this);
  }
  
  @Override
  public String toString() {
    return "Guice Invalid Module Problem: " + moduleName + " " + getMessage();
  }
}
