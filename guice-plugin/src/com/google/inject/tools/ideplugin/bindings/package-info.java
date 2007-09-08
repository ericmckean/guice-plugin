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
 * Classes for finding the bindings of a given Java element in the user's code.
 * 
 * <p>
 * When the user chooses the "Find Bindings" option from the Guice context menu
 * (or any other way), a
 * {@link com.google.inject.tools.ideplugin.bindings.BindingsEngine} object is
 * created to handle it. The real work is done by the
 * {@link com.google.inject.tools.ideplugin.bindings.BindingLocator} which
 * actually uses guice to find the bindings and their locations in source code.
 * The results, which consist of
 * {@link com.google.inject.tools.suite.snippets.BindingCodeLocation} objects are
 * created as a
 * {@link com.google.inject.tools.ideplugin.results.CodeLocationsResults} object
 * which is then passed to the
 * {@link com.google.inject.tools.ideplugin.results.ResultsView}.
 * 
 * @author Darren Creutz (dcreutz@gmail.com)
 */

package com.google.inject.tools.ideplugin.bindings;