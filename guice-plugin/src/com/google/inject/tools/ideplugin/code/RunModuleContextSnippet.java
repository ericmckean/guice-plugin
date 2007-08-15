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

import com.google.inject.tools.ideplugin.module.ModuleContextRepresentation.ModuleInstanceRepresentation;
import com.google.inject.tools.ideplugin.module.ModuleContextRepresentation;
import java.util.List;
import java.util.ArrayList;

/**
 * A {@link CodeRunner.Runnable} that can be used by the {@link CodeRunner} to run a {@link
 * com.google.inject.tools.ideplugin.snippets.ModuleContextSnippet} in userspace.
 * 
 * @author Darren Creutz <dcreutz@gmail.com>
 */
public class RunModuleContextSnippet extends CodeRunner.Runnable {
  private final ModuleContextRepresentation moduleContext;
  
  public RunModuleContextSnippet(CodeRunner codeRunner, ModuleContextRepresentation moduleContext) {
    super(codeRunner);
    this.moduleContext = moduleContext;
  }
  
  @Override
  public String label() {
    return "Running module context " + moduleContext.getName();
  }
  
  @Override
  protected String getFullyQualifiedSnippetClass() {
    return "com.google.inject.tools.ideplugin.snippets.ModuleContextSnippet";
  }
  
  @Override
  protected List<? extends Object> getSnippetArguments() {
    final List<Object> args = new ArrayList<Object>();
    args.add(moduleContext.getName());
    args.add(moduleContext.getModules().size());
    for (ModuleInstanceRepresentation module : moduleContext.getModules()) {
      args.addAll(module.toStringList());
    }
    return args;
  }
}
