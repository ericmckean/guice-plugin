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

package com.google.inject.tools.ideplugin.results;

import java.util.Set;

import com.google.inject.tools.ideplugin.problem.CodeProblem;

/**
 * Represents a location in the user's code space.
 * 
 * @author Darren Creutz <dcreutz@gmail.com>
 */
public abstract class CodeLocation {
	private final String file;
	private final int location;
	private final Set<CodeProblem> problems;
	
	/**
	 * Create a new CodeLocation.
	 */
	public CodeLocation(String file,int location,Set<CodeProblem> problems) {
		this.file = file;
		this.location = location;
		this.problems = problems;
	}
	
	/**
	 * Return the test to display when naming the code location for the user.
	 * 
	 * @return the display name
	 */
	public abstract String getDisplayName();
	
	/**
	 * Return the file where the code lives.
	 * 
	 * @return the file name
	 */
	public String file() {
		return file;
	}
	
	/**
	 * Return the location in the file of the code snippet.
	 * 
	 * @return the location in the file
	 */
	public int location() {
		return location;
	}
	
	/**
	 * Return the {@link CodeProblem}s associated with this CodeLocation.
	 * 
	 * @return the problems
	 */
	public Set<CodeProblem> getProblems() {
		return problems;
	}
}
