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
 * The ActionsHandler responds to {@link Action} requests by taking the appropriate action,
 * such as going to a given code location.
 * 
 * IDE specific implementations implement the Actions defined in this interface.
 * 
 * @author Darren Creutz <dcreutz@gmail.com>
 */
public interface ActionsHandler {
	/**
	 * An Action is anything the IDE can do in response to a trigger.
	 * For example, going to a code location.
	 */
	public static interface Action {}
	
	/**
	 * Represents the IDE action of going to a location in the code, i.e. opening
	 * the file and moving to the line number.
	 */
	public static class GotoCodeLocation implements Action {
		private final String file;
		private final int location;
		
		/**
		 * Create a GotoCodeLocation Action.
		 * 
		 * @param file the file to go to
		 * @param location the line number
		 */
		public GotoCodeLocation(String file,int location) {
			this.file = file;
			this.location = location;
		}
		
		/**
		 * Return the file name.
		 * 
		 * @return the file name
		 */
		public String file() {
			return file;
		}
		
		/**
		 * Return the location (line number) to go to.
		 * 
		 * @return the line number
		 */
		public int location() {
			return location;
		}
	}
	
	/**
	 * Perform a GotoCodeLocation Action.
	 * 
	 * @param action the GotoCodeLocation Action
	 */
	public void run(GotoCodeLocation action);
	
	/**
	 * Perform a nonspecific Action (does not satisfy any of the above types).
	 * Likely this causes an exception.
	 * 
	 * @param action the Action to perform
	 */
	public void run(Action action);
}
