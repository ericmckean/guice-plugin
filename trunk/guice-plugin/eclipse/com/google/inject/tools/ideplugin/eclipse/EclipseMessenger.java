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

import com.google.inject.Singleton;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import com.google.inject.tools.ideplugin.Messenger;

/**
 * Eclipse implementation of the Messenger object.
 * 
 * @author Darren Creutz <dcreutz@gmail.com>
 */
@Singleton
public class EclipseMessenger implements Messenger {
	private void showMessage(String message) {
		MessageDialog.openInformation(
			new Shell(),
			"Guice",
			message);
	}
	
	/**
	 * (non-Javadoc)
	 * @see com.google.inject.tools.ideplugin.Messenger#display(java.lang.String)
	 */
	public void display(String message) {
		showMessage(message);
	}
}
