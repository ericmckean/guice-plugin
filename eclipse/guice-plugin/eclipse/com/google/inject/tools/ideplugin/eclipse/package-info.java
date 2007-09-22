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

/**
 * Eclipse specific implementations forming an IDE plugin.
 * 
 * <p>The Eclipse classes consist of actions to be run in response to the user
 * selecting menu items and of views and dialogs displaying results and 
 * configuration options.
 * 
 * <p>The {@link com.google.inject.tools.ideplugin.eclipse.EclipseGuicePlugin} is a concrete 
 * implementation of a {@link com.google.inject.tools.ideplugin.GuicePlugin} that is the core object
 * of the IDE plugin.  The {@link com.google.inject.tools.ideplugin.eclipse.EclipsePluginModule} 
 * (and its inner submodule the 
 * {@link com.google.inject.tools.ideplugin.eclipse.EclipsePluginModule.EclipseGuiceToolsModule}) 
 * are concrete
 * implementations of the {@link com.google.inject.tools.ideplugin.GuicePluginModule} 
 * and {@link com.google.inject.tools.suite.GuiceToolsModule} that bind the Eclipse pieces.
 * 
 * @author Darren Creutz (dcreutz@gmail.com)
 */

package com.google.inject.tools.ideplugin.eclipse;