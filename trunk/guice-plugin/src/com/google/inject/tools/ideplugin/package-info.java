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

/**
 * Tools integrating guice with development environments.
 * 
 * 
 * The {@link com.google.inject.tools.ideplugin.GuicePlugin} object is the equivalent of the main() routine for standard applcations;
 * it creates the injector and starts the plugin.  IDEs create plugin parts themselves and hand them to
 * us as static objects (or instances accessible in static objects) so we must bind these instances in
 * our {@link com.google.inject.tools.ideplugin.GuicePluginModule} subclasses (which are IDE specific).
 * 
 * The basic objects in our plugin are:
 *  {@link com.google.inject.tools.ideplugin.module.ModuleManager} - manages the modules in the user's code
 *  {@link com.google.inject.tools.ideplugin.problem.ProblemsHandler} - notifies the user of guice related problems in the code
 *  {@link com.google.inject.tools.ideplugin.results.ResultsHandler} - manages results of user queries such as finding bindings
 *  {@link com.google.inject.tools.ideplugin.bindings.BindingsEngine} - performs the actual lookup of bindings in the code
 *  
 * Key objects which must be created in IDE specific manner are:
 *  {@link com.google.inject.tools.ideplugin.module.ModulesListener} - listen for changes in the user's code involving modules
 *  {@link com.google.inject.tools.ideplugin.module.ModuleSelectionView} - allow the user to configure what modules the plugin runs
 *  {@link com.google.inject.tools.ideplugin.results.ResultsView} - displays results of searches, e.g. for bindings
 *  
 * IDE specific objects for creating the guice context menu, e.g. "Find Bindings" are also necessary.
 * 
 * 
 * The plugin is intended to be written simultaneously for all IDEs and use guice to inject the IDE 
 * specific implementations at runtime.  See {@link com.google.inject.tools.ideplugin.GuicePluginModule} for more details.
 */

package com.google.inject.tools.ideplugin;