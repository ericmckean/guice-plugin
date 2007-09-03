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

package com.google.inject.tools.suite;

import com.google.inject.AbstractModule;
import com.google.inject.Provider;
import com.google.inject.binder.AnnotatedBindingBuilder;
import com.google.inject.tools.suite.code.CodeRunner;
import com.google.inject.tools.suite.code.CodeRunnerFactoryImpl;
import com.google.inject.tools.suite.module.ModuleManager;
import com.google.inject.tools.suite.module.ModuleManagerFactoryImpl;
import com.google.inject.tools.suite.module.ModulesSource;

/**
 * The guice module controlling the tools suite.
 * 
 * The general pattern is:
 * 
 * <code>protected abstract void bindFoo(AnnotatedBindingBuilder<Foo> bindFoo)</code>
 * 
 * should be implemented as:
 * 
 * <code>void bindFoo(AnnotatedBindingBuilder<Foo> bindFoo) {
 *   bindFoo.to(FooImpl.class);
 * }</code>
 * 
 * @author Darren Creutz (dcreutz@gmail.com)
 */
public abstract class GuiceToolsModule extends AbstractModule {
  public interface CodeRunnerFactory extends Provider<CodeRunner> {
    public CodeRunner create(JavaManager project);
  }

  public interface ModuleManagerFactory extends Provider<ModuleManager> {
    public ModuleManager create(JavaManager javaManager);
  }

  @Override
  protected void configure() {
    bindCodeRunnerFactory(bind(CodeRunnerFactory.class));
    bindCodeRunner(bind(CodeRunner.class));
    bindModuleManagerFactory(bind(ModuleManagerFactory.class));
    bindModuleManager(bind(ModuleManager.class));
    bindModulesSource(bind(ModulesSource.class));
    bindProblemsHandler(bind(ProblemsHandler.class));
    bindMessenger(bind(Messenger.class));
    bindJavaManager(bind(JavaManager.class));
    bindProgressHandler(bind(ProgressHandler.class));
  }

  protected void bindModuleManagerFactory(
      AnnotatedBindingBuilder<ModuleManagerFactory> bindModuleManagerFactory) {
    bindModuleManagerFactory.to(ModuleManagerFactoryImpl.class)
        .asEagerSingleton();
  }
  
  protected void bindModuleManager(
      AnnotatedBindingBuilder<ModuleManager> bindModuleManager) {
    bindModuleManager.toProvider(ModuleManagerFactoryImpl.class);
  }

  protected void bindProblemsHandler(
      AnnotatedBindingBuilder<ProblemsHandler> bindProblemsHandler) {
    bindProblemsHandler.to(DefaultProblemsHandler.class);
  }

  protected void bindModulesSource(
      AnnotatedBindingBuilder<ModulesSource> bindModulesSource) {
    bindModulesSource.to(DefaultModulesSource.class);
  }

  protected void bindMessenger(
      AnnotatedBindingBuilder<Messenger> bindMessenger) {
    bindMessenger.to(DefaultMessenger.class);
  }

  protected void bindCodeRunnerFactory(
      AnnotatedBindingBuilder<CodeRunnerFactory> bindCodeRunnerFactory) {
    bindCodeRunnerFactory.to(CodeRunnerFactoryImpl.class);
  }

  protected void bindCodeRunner(
      AnnotatedBindingBuilder<CodeRunner> bindCodeRunner) {
    bindCodeRunner.toProvider(CodeRunnerFactoryImpl.class);
  }

  protected void bindJavaManager(
      AnnotatedBindingBuilder<JavaManager> bindJavaManager) {
    bindJavaManager.to(DefaultJavaManager.class);
  }
  
  protected void bindProgressHandler(
      AnnotatedBindingBuilder<ProgressHandler> bindProgressHandler) {
    bindProgressHandler.to(BlockingProgressHandler.class);
  }
}
