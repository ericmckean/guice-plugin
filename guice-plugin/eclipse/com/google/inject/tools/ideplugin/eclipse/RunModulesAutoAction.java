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

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import com.google.inject.tools.ideplugin.ProjectManager;

/**
 * Menu action to run modules automatically or not.
 * 
 * @author Darren Creutz <dcreutz@gmail.com>
 */
public class RunModulesAutoAction implements IEditorActionDelegate,
    IObjectActionDelegate {
  private boolean state;
  private final ProjectManager projectManager;

  public RunModulesAutoAction() {
    this.state = false;
    this.projectManager = Activator.getGuicePlugin().getProjectManager();
  }

  public void setActiveEditor(IAction action, IEditorPart targetEditor) {
  }

  public void setActivePart(IAction action, IWorkbenchPart targetPart) {
  }

  public void selectionChanged(IAction action, ISelection selection) {
  }

  public void run(IAction action) {
    state = !state;
    projectManager.getModuleManager().setRunAutomatically(state);
  }
}
