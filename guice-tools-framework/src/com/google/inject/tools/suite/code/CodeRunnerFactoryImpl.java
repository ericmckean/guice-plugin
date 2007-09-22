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
import com.google.inject.tools.suite.JavaManager;
import com.google.inject.tools.suite.Messenger;
import com.google.inject.tools.suite.ProgressHandler;
import com.google.inject.tools.suite.code.CodeRunnerFactory;

/**
 * Standard implementation of a {@link CodeRunner} factory allowing creation
 * of CodeRunners from {@link JavaManager}s.
 * 
 * @author Darren Creutz (dcreutz@gmail.com)
 */
class CodeRunnerFactoryImpl implements CodeRunnerFactory {
  private final Provider<ProgressHandler> progressHandlerProvider;
  private final Provider<Messenger> messengerProvider;
  private final Provider<JavaManager> javaManagerProvider;

  /**
   * The factory should be injected.
   */
  @Inject
  public CodeRunnerFactoryImpl(
      Provider<ProgressHandler> progressHandlerProvider, 
      Provider<Messenger> messengerProvider,
      Provider<JavaManager> javaManagerProvider) {
    this.progressHandlerProvider = progressHandlerProvider;
    this.messengerProvider = messengerProvider;
    this.javaManagerProvider = javaManagerProvider;
  }

  /**
   * Create a CodeRunner from the given JavaManager.
   */
  public CodeRunner create(JavaManager project) {
    return new CodeRunnerImpl(project, progressHandlerProvider.get(),
        messengerProvider.get());
  }
  
  public CodeRunner get() {
    return create(javaManagerProvider.get());
  }
}