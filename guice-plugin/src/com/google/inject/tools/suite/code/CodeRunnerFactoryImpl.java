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

package com.google.inject.tools.suite.code;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.binder.AnnotatedBindingBuilder;
import com.google.inject.tools.suite.JavaManager;
import com.google.inject.tools.suite.Messenger;
import com.google.inject.tools.suite.ProgressHandler;
import com.google.inject.tools.suite.GuiceToolsModule.CodeRunnerFactory;

/**
 * Standard implementation of a {@link CodeRunner} factory allowing creation
 * of CodeRunners from {@link JavaManager}s.
 * 
 * @author Darren Creutz <dcreutz@gmail.com>
 */
public class CodeRunnerFactoryImpl implements CodeRunnerFactory {
  private final Provider<ProgressHandler> progressHandlerProvider;
  private final Messenger messenger;

  /**
   * The factory should be injected.
   */
  @Inject
  public CodeRunnerFactoryImpl(
      Provider<ProgressHandler> progressHandlerProvider, Messenger messenger) {
    this.progressHandlerProvider = progressHandlerProvider;
    this.messenger = messenger;
  }

  /**
   * Create a CodeRunner from the given JavaManager.
   */
  public CodeRunner create(JavaManager project) {
    return new CodeRunnerImpl(project, progressHandlerProvider.get(),
        messenger);
  }
  
  /**
   * Bind the {@link CodeRunner} without a factory (for tools that do not
   * have a {@link JavaManager}).
   */
  public static void bindCodeRunner(AnnotatedBindingBuilder<CodeRunner> bindCodeRunner) {
    bindCodeRunner.to(CodeRunnerImpl.class);
  }
}