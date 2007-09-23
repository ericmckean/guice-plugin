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
import com.google.inject.binder.AnnotatedBindingBuilder;
import com.google.inject.tools.suite.code.CodeRunnerModule;
import com.google.inject.tools.suite.module.ModuleManagerModule;

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
  @Override
  protected void configure() {
    install(codeRunnerModule());
    install(moduleManagerModule());
    bindProblemsHandler(bind(ProblemsHandler.class));
    bindMessenger(bind(Messenger.class));
    bindJavaManager(bind(JavaManager.class));
    bindProgressHandler(bind(ProgressHandler.class));
    bindSettings(bind(Settings.class));
  }
  
  protected CodeRunnerModule codeRunnerModule() {
    return new CodeRunnerModule();
  }
  
  protected ModuleManagerModule moduleManagerModule() {
    return new ModuleManagerModule();
  }

  protected void bindProblemsHandler(
      AnnotatedBindingBuilder<ProblemsHandler> bindProblemsHandler) {
    bindProblemsHandler.to(DefaultProblemsHandler.class);
  }

  protected void bindMessenger(
      AnnotatedBindingBuilder<Messenger> bindMessenger) {
    bindMessenger.to(DefaultMessenger.class);
  }

  protected void bindJavaManager(
      AnnotatedBindingBuilder<JavaManager> bindJavaManager) {
    bindJavaManager.to(DefaultJavaManager.class);
  }
  
  protected void bindProgressHandler(
      AnnotatedBindingBuilder<ProgressHandler> bindProgressHandler) {
    bindProgressHandler.to(BlockingProgressHandler.class);
  }
  
  protected void bindSettings(
      AnnotatedBindingBuilder<Settings> bindSettings) {
    bindSettings.to(DefaultSettings.class);
  }
}
