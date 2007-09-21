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

package com.google.inject.tools.ideplugin.results;

import com.google.inject.AbstractModule;
import com.google.inject.binder.AnnotatedBindingBuilder;

public class ResultsModule extends AbstractModule {
  @Override
  protected void configure() {
    bindResultsHandler(bind(ResultsHandler.class));
  }
  
  /**
   * Bind the {@link ResultsHandler} implementation.
   */
  protected void bindResultsHandler(
      AnnotatedBindingBuilder<ResultsHandler> bindResultsHandler) {
    bindResultsHandler.to(ResultsHandlerImpl.class).asEagerSingleton();
  }
}
