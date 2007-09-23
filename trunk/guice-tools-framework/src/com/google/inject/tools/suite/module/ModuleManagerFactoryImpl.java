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

package com.google.inject.tools.suite.module;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.tools.suite.JavaManager;
import com.google.inject.tools.suite.Messenger;
import com.google.inject.tools.suite.ProblemsHandler;
import com.google.inject.tools.suite.Settings;
import com.google.inject.tools.suite.code.CodeRunnerFactory;
import com.google.inject.tools.suite.module.ModuleManager;
import com.google.inject.tools.suite.module.ModuleManagerImpl;

import java.util.HashMap;
import java.util.Map;

/**
 * Factory to create {@link ModuleManager}s with preconstructed
 * {@link JavaManager}s.
 * 
 * @author Darren Creutz (dcreutz@gmail.com)
 */
class ModuleManagerFactoryImpl implements ModuleManagerFactory {
  private final Provider<ProblemsHandler> problemsHandlerProvider;
  private final Provider<Messenger> messengerProvider;
  private final Provider<CodeRunnerFactory> codeRunnerFactoryProvider;
  private final Provider<JavaManager> javaManagerProvider;
  private final Provider<Settings> settingsProvider;
  private final Map<JavaManager, ModuleManager> moduleManagerInstances;

  @Inject
  public ModuleManagerFactoryImpl(
      Provider<ProblemsHandler> problemsHandlerProvider,
      Provider<Messenger> messengerProvider,
      Provider<CodeRunnerFactory> codeRunnerFactoryProvider,
      Provider<JavaManager> javaManagerProvider,
      Provider<Settings> settingsProvider) {
    this.problemsHandlerProvider = problemsHandlerProvider;
    this.messengerProvider = messengerProvider;
    this.codeRunnerFactoryProvider = codeRunnerFactoryProvider;
    this.javaManagerProvider = javaManagerProvider;
    this.settingsProvider = settingsProvider;
    this.moduleManagerInstances = new HashMap<JavaManager, ModuleManager>();
  }

  /**
   * Create a ModuleManager with the given JavaManager and Settings.
   */
  public ModuleManager create(JavaManager javaManager, Settings settings) {
    if (moduleManagerInstances.get(javaManager) == null) {
      moduleManagerInstances.put(javaManager, new ModuleManagerImpl(
          problemsHandlerProvider.get(),
          messengerProvider.get(), javaManager, codeRunnerFactoryProvider
          .get(), false, settings));
    }
    return moduleManagerInstances.get(javaManager);
  }
  
  /**
   * Create a ModuleManager with the given JavaManager.
   */
  public ModuleManager create(JavaManager javaManager) {
    if (moduleManagerInstances.get(javaManager) == null) {
      moduleManagerInstances.put(javaManager, new ModuleManagerImpl(
          problemsHandlerProvider.get(),
          messengerProvider.get(), javaManager, codeRunnerFactoryProvider
          .get(), false, settingsProvider.get()));
    }
    return moduleManagerInstances.get(javaManager);
  }
  
  /**
   * Create a ModuleManager with an injected JavaManager.
   */
  public ModuleManager get() {
    return new ModuleManagerImpl(problemsHandlerProvider.get(),
        messengerProvider.get(), javaManagerProvider.get(), codeRunnerFactoryProvider.get(),
        true, settingsProvider.get());
  }
}