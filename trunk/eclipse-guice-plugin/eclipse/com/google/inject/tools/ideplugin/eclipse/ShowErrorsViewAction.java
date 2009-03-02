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
    super(PluginTextValues.SHOW_GUICE_ERRORS, PluginDefinitionValues.GUICE_ERRORS_ICON);
  }
  
  @Override
  protected String myTooltip() {
    return PluginTextValues.SHOW_GUICE_ERRORS;
  }
  
  @Override
  protected String myStatusFailedMessage() {
    return PluginTextValues.CANT_OPEN_ERRORS;
  }
  
  @Override
  protected boolean runMyAction(IEditorPart part) {
    try {
      IWorkbenchPage activePage = PlatformUI.getWorkbench()
          .getWorkbenchWindows()[0].getActivePage();
      activePage.showView(PluginDefinitionValues.ERROR_VIEW_ID);
      return true;
    } catch (Exception e) {
      return false;
    }
  }
}
