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

package com.google.inject.tools.ideplugin;

import com.google.inject.Module;

/**
 * Users can implement this interface in their code to create a module
 * context for the guice plugin.  The modules returned will be used to
 * create a (simulated) {@link com.google.inject.Injector} to resolve bindings.
 * 
 * The guice plugin will automatically load module contexts that are
 * defined by implementing this interface.
 * 
 * @author Darren Creutz (dcreutz@gmail.com)
 */
public interface GuiceIDEPluginContextDefinition {
  /**
   * Return the modules to use for the context.
   * 
   * This method must either be static or the implementing class must
   * have a default (zero argument) constructor.
   */
  public Iterable<Module> getModuleContextDefinition();
}
