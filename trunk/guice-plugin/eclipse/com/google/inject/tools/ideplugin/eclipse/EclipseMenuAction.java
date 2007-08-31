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

import com.google.inject.tools.ideplugin.GuicePlugin;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;

/**
 * General implementation of an action for the Eclipse menu bar.
 * 
 * @author dcreutz@gmail.com (Darren Creutz)
 */
public abstract class EclipseMenuAction implements IWorkbenchWindowActionDelegate {
  protected GuicePlugin guicePlugin;
  protected IWorkbenchWindow window;

  public EclipseMenuAction() {
    super();
    guicePlugin = Activator.getGuicePlugin();
  }
  
  public void dispose() {
  }

  public void init(IWorkbenchWindow window) {
    this.window = window;
  }

  public void run(IAction action) {
    IWorkbenchPart part = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePart();
    if (part instanceof IEditorPart && runMyAction((IEditorPart)part)) {
    } else {
      if (part instanceof IViewPart) {
        IStatusLineManager statusManager = ((IViewPart)part).getViewSite().getActionBars().getStatusLineManager();
        statusManager.setMessage(myStatusFailedMessage());
      } else {
        guicePlugin.getMessenger().display(myStatusFailedMessage());
      }
    }
  }

  public void selectionChanged(IAction action, ISelection selection) {
  }
  
  protected abstract boolean runMyAction(IEditorPart part);
  
  protected abstract String myStatusFailedMessage();
}