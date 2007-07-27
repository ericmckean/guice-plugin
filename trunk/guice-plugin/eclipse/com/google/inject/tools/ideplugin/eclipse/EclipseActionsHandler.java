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

package com.google.inject.tools.ideplugin.eclipse;

import com.google.inject.tools.ideplugin.ActionsHandler;
import com.google.inject.Singleton;
import com.google.inject.Inject;

/**
 * Eclipse implementation of the {@link ActionsHandler}.
 * 
 * @author Darren Creutz <dcreutz@gmail.com>
 */
@Singleton
public class EclipseActionsHandler implements ActionsHandler {
	/**
	 * Create the ActionsHandler.  This should be injected as a singleton.
	 */
	@Inject
	public EclipseActionsHandler() {
		
	}
	
	/**
	 * (non-Javadoc)
	 * @see com.google.inject.tools.ideplugin.ActionsHandler#run(com.google.inject.tools.ideplugin.ActionsHandler.GotoCodeLocation)
	 */
	public void run(GotoCodeLocation action) {
		
	}
	
	/**
	 * (non-Javadoc)
	 * @see com.google.inject.tools.ideplugin.ActionsHandler#run(com.google.inject.tools.ideplugin.ActionsHandler.Action)
	 */
	public void run(Action action) {
		
	}
}
