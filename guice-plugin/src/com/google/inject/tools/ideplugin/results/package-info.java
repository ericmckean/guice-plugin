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
 * Classes for representing and displaying the results of user queries to the plugin.
 * 
 * 
 * The {@link com.google.inject.tools.ideplugin.results.ResultsHandler} is notified by for example the {@link com.google.inject.tools.ideplugin.bindings.BindingsEngine}
 * when results to a query by the user are available.  It then passes them to the {@link com.google.inject.tools.ideplugin.results.ResultsView} 
 * (more accurately to the IDE specific implementation of the com.google.inject.tools.ideplugin.results.ResultsView) for display.
 * 
 * Results to user's queries are stored as {@link com.google.inject.tools.ideplugin.results.Results} objects which usually consist of 
 * {@link com.google.inject.tools.ideplugin.results.CodeLocation} objects on a per module context basis.
 */

package com.google.inject.tools.ideplugin.results;