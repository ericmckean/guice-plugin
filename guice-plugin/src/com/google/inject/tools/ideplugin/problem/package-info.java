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
 * Classes for representing and handling guice related problems in the user's code.
 * 
 * <p>The {@link com.google.inject.tools.ideplugin.problem.ProblemsHandler} is responsible for notifying the user about problems in their code such as
 * creation exceptions and other guice configuration errors.  The other parts of the plugin will notify the 
 * handler when such problems are found.  The {@link com.google.inject.tools.ideplugin.problem.ProblemsListener} (more accurately its IDE specific implementations)
 * listen for changes in the user's code and check it for problems, then notifying the handler.
 * 
 * <p>Problems are represented by the {@link com.google.inject.tools.ideplugin.snippets.CodeProblem} class.
 */

package com.google.inject.tools.ideplugin.problem;