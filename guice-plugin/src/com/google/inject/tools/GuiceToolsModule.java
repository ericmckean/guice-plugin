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

package com.google.inject.tools;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.binder.AnnotatedBindingBuilder;
import com.google.inject.tools.code.CodeRunner;
import com.google.inject.tools.code.CodeRunnerImpl;
import com.google.inject.tools.module.ModuleManager;
import com.google.inject.tools.module.ModuleManagerImpl;
import com.google.inject.tools.module.ModulesNotifier;

/**
 * The guice module controlling the tools suite.
 * 
 * The general pattern is:
 * <code>protected abstract void bindFoo(AnnotatedBindingBuilder<Foo> builder)</code>
 * 
 * should be implemented as:
 * <code>void bindFoo(AnnotatedBindingBuilder<Foo> builder) {
 *   builder.to(FooImpl.class);
 * }</code>
 * 
 * @author Darren Creutz <dcreutz@gmail.com>
 */
public abstract class GuiceToolsModule extends AbstractModule {
  /**
   * Factory for creating {@link CodeRunner}s.
   */
  public interface CodeRunnerFactory {
    /**
     * Create a {@link CodeRunner}.
     * @param project the {@link JavaProject} to run code in
     */
    public CodeRunner create(JavaProject project);
  }
  
  protected static class CodeRunnerFactoryImpl implements CodeRunnerFactory {
    private final Provider<ProgressHandler> progressHandlerProvider;
    private final Messenger messenger;
    @Inject
    public CodeRunnerFactoryImpl(Provider<ProgressHandler> progressHandlerProvider, Messenger messenger) {
      this.progressHandlerProvider = progressHandlerProvider;
      this.messenger = messenger;
    }
    public CodeRunner create(JavaProject project) {
      return new CodeRunnerImpl(project, progressHandlerProvider.get(), messenger);
    }
  }
  
  @Override
  protected void configure() {
    bindCodeRunner(bind(CodeRunnerFactory.class));
    bindModuleManager(bind(ModuleManager.class));
    bindModulesListener(bind(ModulesNotifier.class));
    bindProblemsHandler(bind(ProblemsHandler.class));
    bindMessenger(bind(Messenger.class));
  }
  
  /** 
   * Bind the {@link ModuleManager} implementation.
   */
  protected void bindModuleManager(AnnotatedBindingBuilder<ModuleManager> builder) {
    builder.to(ModuleManagerImpl.class).asEagerSingleton();
  }
  
  /**
   * Bind the {@link ProblemsHandler} implementation.
   */
  protected abstract void bindProblemsHandler(AnnotatedBindingBuilder<ProblemsHandler> builder);
  
  /**
   * Bind the {@link com.google.inject.tools.module.ModulesNotifier} implementation.
   */
  protected abstract void bindModulesListener(AnnotatedBindingBuilder<ModulesNotifier> builder);
  
  /**
   * Bind the {@link Messenger} implementation.
   */
  protected abstract void bindMessenger(AnnotatedBindingBuilder<Messenger> builder);
  
  /**
   * Bind the {@link CodeRunner} implementation.
   */
  protected void bindCodeRunner(AnnotatedBindingBuilder<CodeRunnerFactory> builder) {
    builder.to(CodeRunnerFactoryImpl.class);
  }
}
