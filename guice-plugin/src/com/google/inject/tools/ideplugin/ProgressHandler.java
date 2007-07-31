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

package com.google.inject.tools.ideplugin;

/**
 * Manages a progress bar display of operations.  IDE specific implementations are required.
 * 
 * @author Darren Creutz <dcreutz@gmail.com>
 */
public interface ProgressHandler {
	/**
	 * Reset the progress handler to its starting state and prepare to do the given number of steps.
	 * 
	 * @param totalsteps the number of steps
	 */
	public void initialize(int totalsteps);
	
	/**
	 * Notify the progress handler that the next step is beginning and give it a label to display for this step.
	 * 
	 * @param label the label to display for this step
	 * @return false if the user cancelled the operation
	 */
	public boolean step(String label);
}
