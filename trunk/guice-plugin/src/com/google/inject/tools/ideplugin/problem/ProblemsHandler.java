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

package com.google.inject.tools.ideplugin.problem;

import java.util.Set;

import com.google.inject.tools.ideplugin.snippets.CodeProblem;

/**
 * Notify the user in realtime of problems with their guice code by code assist or other (nonblocking) means.
 * These should respond concurrently to the existing flow, i.e. be nonblocking methods.
 * 
 * @author Darren Creutz <dcreutz@gmail.com>
 */
public interface ProblemsHandler {
	/**
	 * Handle a set of problems found with user's code.
	 * 
	 * @param problem
	 */
	public void foundProblems(Set<? extends CodeProblem> problem);
}
