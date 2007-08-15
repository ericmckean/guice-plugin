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

package com.google.inject.tools.ideplugin.code;

import com.google.inject.tools.ideplugin.module.ModuleRepresentation;
import com.google.inject.tools.ideplugin.snippets.ModuleSnippet;

import java.util.List;
import java.util.ArrayList;

/**
 * A {@link CodeRunner.Runnable} that can be used by the {@link CodeRunner} to run a
 * {@link com.google.inject.tools.ideplugin.snippets.ModuleSnippet} in userspace.
 * 
 * @author Darren Creutz <dcreutz@gmail.com>
 */
public class RunModuleSnippet extends CodeRunner.Runnable {
  private final ModuleRepresentation module;
  
  public RunModuleSnippet(CodeRunner codeRunner, ModuleRepresentation module) {
    super(codeRunner);
    this.module = module;
  }
  
  @Override
  public String label() {
    return "Running module " + module.getName();
  }
  
  @Override
  protected String getFullyQualifiedSnippetClass() {
    return ModuleSnippet.class.getName();
  }
  
  @Override
  protected List<? extends Object> getSnippetArguments() {
    final List<Object> args = new ArrayList<Object>();
    args.add(module.getName());
    return args;
  }
}
