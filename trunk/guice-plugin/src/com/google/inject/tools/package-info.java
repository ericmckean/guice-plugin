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
 * Tools for using guice.
 * 
 * <p>The tools suite is the infrastructure for writing guice tools, such as IDE plugins.
 * 
 * <p>To use the tools suite, simply extend the {@link com.google.inject.tools.GuiceToolsModule} to a concrete set
 * of bindings and then use guice to inject a {@link com.google.inject.tools.module.ModuleManager} in your code.
 * 
 * <p>The {@link com.google.inject.tools.JavaManager}, {@link com.google.inject.tools.ProblemsHandler} and {@link com.google.inject.tools.Messenger} must also
 * be implemented.
 *  
 * <p>The basic objects in our tool suite are:
 * <dl>
 *  <dt>{@link com.google.inject.tools.module.ModuleManager} <dd>manages the modules in the user's code
 *  <dt>{@link com.google.inject.tools.ProblemsHandler} <dd>notifies the user of guice related problems in the code
 *  <dt>{@link com.google.inject.tools.code.CodeRunner} <dd>runs {@link com.google.inject.tools.snippets.CodeSnippet}s in user space to resolve bindings
 * </dl>
 */

package com.google.inject.tools;