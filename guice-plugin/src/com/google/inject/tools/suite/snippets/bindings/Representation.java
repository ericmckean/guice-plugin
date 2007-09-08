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

package com.google.inject.tools.suite.snippets.bindings;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import com.google.inject.tools.suite.snippets.problems.CodeProblem;

/**
 * Abstract class allowing for Serializable representation of Guice classes.
 * 
 * @author Darren Creutz (dcreutz@gmail.com)
 */
public abstract class Representation implements Serializable {
  protected final Set<CodeProblem> problems;
  
  public Representation() {
    this.problems = new HashSet<CodeProblem>();
  }
  
  public Set<? extends CodeProblem> problems() {
    return problems;
  }
}
