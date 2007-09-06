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
 * Tools integrating guice with development environments.
 * 
 * <p>
 * The {@link com.google.inject.tools.ideplugin.GuicePlugin} object is the
 * equivalent of the main() routine for standard applcations; it creates the
 * injector and starts the plugin. IDEs create plugin parts themselves and hand
 * them to us as static objects (or instances accessible in static objects) so
 * we must bind these instances in our
 * {@link com.google.inject.tools.ideplugin.GuicePluginModule} subclasses (which
 * are IDE specific).
 * 
 * <p>
 * The basic objects in our plugin are:
 * <dl>
 * <dt>{@link com.google.inject.tools.suite.module.ModuleManager}
 * <dd>manages the modules in the user's code
 * <dt>{@link com.google.inject.tools.suite.ProblemsHandler}
 * <dd>notifies the user of guice related problems in the code
 * <dt>{@link com.google.inject.tools.ideplugin.results.ResultsHandler}
 * <dd>manages results of user queries such as finding bindings
 * <dt>{@link com.google.inject.tools.ideplugin.bindings.BindingsEngine}
 * <dd>performs the actual lookup of bindings in the code
 * <dt>{@link com.google.inject.tools.suite.code.CodeRunner}
 * <dd>runs {@link com.google.inject.tools.suite.snippets.CodeSnippet}s in user
 * space to resolve bindings
 * <dt>{@link com.google.inject.tools.ideplugin.ProjectManager}
 * <dd>manages projects in the IDE
 * </dl>
 * 
 * <p>
 * Key objects which must be created in IDE specific manner are:
 * <dl>
 * <dt>{@link com.google.inject.tools.ideplugin.ModulesSource}
 * <dd>listen for changes in the user's code involving modules
 * <dt>{@link com.google.inject.tools.ideplugin.ModuleSelectionView}
 * <dd>allow the user to configure what modules the plugin runs
 * <dt>{@link com.google.inject.tools.ideplugin.results.ResultsView}
 * <dd>displays results of searches, e.g. for bindings
 * </dl>
 * 
 * <p>
 * IDE specific objects for creating the guice context menu, e.g. "Find
 * Bindings" are also necessary.
 * 
 * <p>
 * The plugin is intended to be written simultaneously for all IDEs and use
 * guice to inject the IDE specific implementations at runtime. See
 * {@link com.google.inject.tools.ideplugin.GuicePluginModule} for more details.
 */

package com.google.inject.tools.ideplugin;