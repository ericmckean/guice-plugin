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

package com.google.inject.tools.ideplugin.eclipse;

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

/**
 * Show the Guice Errors View.
 * 
 * @author Darren Creutz (dcreutz@gmail.com)
 */
public class ShowErrorsViewAction extends EclipseMenuAction {
  public ShowErrorsViewAction() {
    super("Show Guice Errors View", "guiceerrors.gif");
  }
  
  @Override
  protected String myTooltip() {
    return "Show Guice Errors View";
  }
  
  @Override
  protected String myStatusFailedMessage() {
    return "Could not open Guice Errors View";
  }
  
  @Override
  protected boolean runMyAction(IEditorPart part) {
    try {
      IWorkbenchPage activePage = PlatformUI.getWorkbench()
          .getWorkbenchWindows()[0].getActivePage();
      activePage.showView(
          "com.google.inject.tools.ideplugin.eclipse.EclipseErrorView");
      return true;
    } catch (Exception e) {
      return false;
    }
  }
}
