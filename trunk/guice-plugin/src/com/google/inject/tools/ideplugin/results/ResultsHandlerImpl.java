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

import com.google.inject.Singleton;
import com.google.inject.Inject;

/** 
 * Standard implementation of the ResultsHandler.
 * 
 * @author Darren Creutz <dcreutz@gmail.com>
 */
@Singleton
public class ResultsHandlerImpl implements ResultsHandler {
	private final ResultsView resultsView;
	
	/** 
	 * Create a (the) ResultsHandler.
	 */
	@Inject
	public ResultsHandlerImpl(ResultsView resultsView) {
		this.resultsView = resultsView;
	}
	
	/**
	 * (non-Javadoc)
	 * @see com.google.inject.tools.ideplugin.results.ResultsHandler#displayLocationsResults(com.google.inject.tools.ideplugin.results.CodeLocationsResults)
	 */
	public void displayLocationsResults(CodeLocationsResults results) {
		resultsView.displayResults(results);
	}
}
