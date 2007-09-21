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

import com.google.inject.AbstractModule;
import com.google.inject.binder.AnnotatedBindingBuilder;

/**
 * A module binding code runner implementations.
 * 
 * @author Darren Creutz (dcreutz@gmail.com)
 */
public class CodeRunnerModule extends AbstractModule {
  @Override
  protected void configure() {
    bindCodeRunnerFactory(bind(CodeRunnerFactory.class));
    bindCodeRunner(bind(CodeRunner.class));
  }

  protected void bindCodeRunnerFactory(
      AnnotatedBindingBuilder<CodeRunnerFactory> bindCodeRunnerFactory) {
    bindCodeRunnerFactory.to(CodeRunnerFactoryImpl.class);
  }

  protected void bindCodeRunner(
      AnnotatedBindingBuilder<CodeRunner> bindCodeRunner) {
    bindCodeRunner.toProvider(CodeRunnerFactoryImpl.class);
  }
}
