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
 * Tools for using guice.
 * 
 * <p>
 * The tools suite is the infrastructure for writing guice tools, such as IDE
 * plugins.
 * 
 * <p>
 * To use the tools suite, simply extend the
 * {@link com.google.inject.tools.suite.GuiceToolsModule} to a concrete set of
 * bindings and then use guice to inject a
 * {@link com.google.inject.tools.suite.module.ModuleManager} in your code.
 * 
 * <p>
 * The {@link com.google.inject.tools.suite.JavaManager},
 * {@link com.google.inject.tools.suite.ProblemsHandler},
 * {@link com.google.inject.tools.suite.ProgressHandler},
 * {@link com.google.inject.tools.suite.module.ModulesSource} and
 * {@link com.google.inject.tools.suite.Messenger} should also be implemented and bound
 * in a subclass of {@link com.google.inject.tools.suite.GuiceToolsModule} that should 
 * be used for the injections.
 * 
 * If they are not bound the defaults are:
 * <dl>
 * <dt>{@link com.google.inject.tools.suite.JavaManager} 
 * <dd>java manager that assumes everything is on the default classpath
 * <dt>{@link com.google.inject.tools.suite.module.ModulesSource} 
 * <dd>modules source that knows about no modules
 * <dt>{@link com.google.inject.tools.suite.ProblemsHandler} 
 * <dd>problems handler that ignores problems
 * <dt>{@link com.google.inject.tools.suite.Messenger} 
 * <dd>messenger that ignores messages
 * <dt>{@link com.google.inject.tools.suite.ProgressHandler} 
 * <dd>progress handler that blocks the current thread and displays nothing
 * 
 * <p>
 * The basic objects in our tool suite are:
 * <dl>
 * <dt>{@link com.google.inject.tools.suite.module.ModuleManager}
 * <dd>manages the modules in the user's code
 * <dt>{@link com.google.inject.tools.suite.module.ModulesSource}
 * <dd>notify the manager of changes in modules
 * <dt>{@link com.google.inject.tools.suite.ProblemsHandler}
 * <dd>notifies the user of guice related problems in the code
 * <dt>{@link com.google.inject.tools.suite.code.CodeRunner}
 * <dd>runs {@link com.google.inject.tools.suite.snippets.CodeSnippet}s in user
 * space to resolve bindings
 * </dl>
 * 
 * <p>
 * There is also the snippets package which is packaged a standalone jar file
 * that needs to be on the classpath provided by the JavaManager.
 */

package com.google.inject.tools.suite;