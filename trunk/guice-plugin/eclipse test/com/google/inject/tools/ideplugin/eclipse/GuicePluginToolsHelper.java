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

package com.google.inject.tools.ideplugin.eclipse;

import com.google.inject.Module;
import com.google.inject.tools.ideplugin.GuiceIDEPluginContextDefinition;
import com.google.inject.tools.ideplugin.eclipse.EclipsePluginModule.EclipseGuiceToolsModule;

import java.util.HashSet;
import java.util.Set;

/**
 * Helper allowing us to use the plugin on our code.
 * 
 * @author Darren Creutz (dcreutz@gmail.com)
 */
public class GuicePluginToolsHelper implements GuiceIDEPluginContextDefinition {
  public Set<Module> getModuleContextDefinition() {
    Set<Module> modules = new HashSet<Module>();
    modules.add(new EclipseGuiceToolsModule());
    modules.add(new EclipsePluginModule());
    return modules;
  }
}