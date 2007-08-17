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

package com.google.inject.tools.ideplugin;

import com.google.inject.binder.AnnotatedBindingBuilder;
import com.google.inject.tools.GuiceToolsModule;
import com.google.inject.tools.ProblemsHandler;
import com.google.inject.tools.ideplugin.problem.ProblemsHandlerImpl;

/**
 * The abstract implementation of the tools module specific to the IDE plugin.
 * 
 * @author Darren Creutz <dcreutz@gmail.com>
 */
public abstract class GuiceToolsModuleImpl extends GuiceToolsModule {
  @Override
  protected void bindProblemsHandler(AnnotatedBindingBuilder<ProblemsHandler> bindProblemsHandler) {
    bindProblemsHandler.to(ProblemsHandlerImpl.class).asEagerSingleton();
  }
}
