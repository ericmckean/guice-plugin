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
import com.google.inject.binder.AnnotatedBindingBuilder;
import com.google.inject.tools.suite.JavaManager;
import com.google.inject.tools.suite.Messenger;
import com.google.inject.tools.suite.ProblemsHandler;
import com.google.inject.tools.suite.GuiceToolsModule.CodeRunnerFactory;
import com.google.inject.tools.suite.GuiceToolsModule.ModuleManagerFactory;
import com.google.inject.tools.suite.module.ModuleManager;
import com.google.inject.tools.suite.module.ModuleManagerImpl;
import com.google.inject.tools.suite.module.ModulesSource;

import java.util.HashMap;
import java.util.Map;

/**
 * Factory to create {@link ModuleManager}s with preconstructed
 * {@link JavaManager}s.
 * 
 * @author Darren Creutz (dcreutz@gmail.com)
 */
public class ModuleManagerFactoryImpl implements ModuleManagerFactory {
  private final Provider<ModulesSource> modulesSourceProvider;
  private final Provider<ProblemsHandler> problemsHandlerProvider;
  private final Provider<Messenger> messengerProvider;
  private final Provider<CodeRunnerFactory> codeRunnerFactoryProvider;
  private final Map<JavaManager, ModuleManager> moduleManagerInstances;

  @Inject
  public ModuleManagerFactoryImpl(
      Provider<ModulesSource> modulesSourceProvider,
      Provider<ProblemsHandler> problemsHandlerProvider,
      Provider<Messenger> messengerProvider,
      Provider<CodeRunnerFactory> codeRunnerFactoryProvider) {
    this.modulesSourceProvider = modulesSourceProvider;
    this.problemsHandlerProvider = problemsHandlerProvider;
    this.messengerProvider = messengerProvider;
    this.codeRunnerFactoryProvider = codeRunnerFactoryProvider;
    this.moduleManagerInstances = new HashMap<JavaManager, ModuleManager>();
  }

  /**
   * Create a ModuleManager.
   */
  public ModuleManager create(JavaManager javaManager) {
    if (moduleManagerInstances.get(javaManager) == null) {
      moduleManagerInstances.put(javaManager, new ModuleManagerImpl(
          modulesSourceProvider.get(), problemsHandlerProvider.get(),
          messengerProvider.get(), javaManager, codeRunnerFactoryProvider
          .get()));
    }
    return moduleManagerInstances.get(javaManager);
  }
  
  /**
   * Bind ModuleManager to an implementation for use without a JavaManager.
   */
  public static void bindModuleManager(
      AnnotatedBindingBuilder<ModuleManager> bindModuleManager) {
    bindModuleManager.to(ModuleManagerImpl.class);
  }
}