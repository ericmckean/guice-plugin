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

//TODO: make concurrent!

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.tools.ideplugin.Messenger;
import java.util.Set;

/**
 * Standard implementation of the ErrorsHandler.
 * 
 * @author Darren Creutz <dcreutz@gmail.com>
 */
@Singleton
public class ProblemsHandlerImpl implements ProblemsHandler {
	private final Messenger messenger;
	
	/**
	 * Create an ProblemsHandlerImpl.  This should be injected.
	 * 
	 * @param messenger the Messenger
	 */
	@Inject
	public ProblemsHandlerImpl(Messenger messenger) {
		this.messenger = messenger;
	}
	
	private void foundProblem(CodeProblem problem) {
		//what to do?  somehow do codeassist with problems
		messenger.display(problem.toString());
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.google.inject.tools.ideplugin.problem.ProblemsHandler#foundProblems(java.util.Set)
	 */
	public void foundProblems(Set<CodeProblem> problems) {
		for (CodeProblem problem : problems) {
			foundProblem(problem);
		}
	}
}
