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

package com.google.inject.tools.ideplugin.test;

import com.google.inject.AbstractModule;

/**
 * Testing {@link com.google.inject.Module} that works correctly, binding {@link MockInjectedInterface} to 
 * {@link MockInjectedInterfaceImpl}.
 * 
 * NOTE: Modifying this file will break the {@link ModuleContextSnippet} test for line
 * number.
 * 
 * @author Darren Creutz <dcreutz@gmail.com>
 */
public class WorkingModule extends AbstractModule {
  /*
   * (non-Javadoc)
   * @see com.google.inject.AbstractModule#configure()
   */
  @Override
  protected void configure() {
    bind(MockInjectedInterface.class).to(MockInjectedInterfaceImpl.class);
  }
}
