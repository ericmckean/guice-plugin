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

import java.net.URL;

import com.google.inject.tools.ideplugin.GuicePlugin;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.Bundle;

/**
 * General implementation of an action for the Eclipse menu bar.
 * 
 * @author dcreutz@gmail.com (Darren Creutz)
 */
public abstract class EclipseMenuAction extends Action 
    implements IWorkbenchWindowActionDelegate {
  
  protected GuicePlugin guicePlugin;
  protected IWorkbenchWindow window;
  
  public EclipseMenuAction(String name) {
    this(name, (ImageDescriptor)null);
  }
  
  public EclipseMenuAction(String name, String imagefile) {
    this(name, getImage(imagefile));
  }
  
  public EclipseMenuAction(String name, ImageDescriptor image) {
    super(name, image);
    this.setToolTipText(myTooltip());
    guicePlugin = Activator.getGuicePlugin();
  }
  
  protected String myTooltip() {
    return null;
  }
  
  protected static ImageDescriptor getImage(String imagefile) {
    return makeImage(imagefile);
  }
  
  private static URL getIconURL(String imagefile) {
    Bundle bundle = Platform.getBundle(PluginDefinitionValues.BUNDLE_ID);
    URL url = bundle.getEntry(imagefile);
    return url;
  }
  
  private static ImageDescriptor makeImage(String imagefile) {
    return ImageDescriptor.createFromURL(getIconURL(imagefile));
  }
  
  public void dispose() {
  }

  public void init(IWorkbenchWindow window) {
    this.window = window;
  }

  @Override
  public void run() {
    IEditorPart editor = 
      PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
    if (editor == null) {
      IWorkbenchPart part =
        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePart();
      displayEditorFailed(part);
    } else if (!runMyAction(editor)) {
      displayFailed(editor);
    }
  }
  
  protected void displayFailed(IWorkbenchPart part) {
    displayError(part, myStatusFailedMessage());
  }
  
  protected void displayEditorFailed(IWorkbenchPart part) {
    displayError(part, PluginTextValues.CANNOT_RESOLVE_EDITOR);
  }
  
  protected void displayError(IWorkbenchPart part, String message) {
    if (part instanceof IViewPart) {
      IStatusLineManager statusManager = ((IViewPart)part).getViewSite().getActionBars().getStatusLineManager();
      statusManager.setMessage(PluginTextValues.GUICE_PLUGIN_NAME + ": " + message);
    } else {
      guicePlugin.getMessenger().display(message);
    }
  }
  
  @Override
  public void runWithEvent(Event event) {
    run();
  }
  
  public void run(IAction action) {
    run();
  }

  public void selectionChanged(IAction action, ISelection selection) {
  }
  
  protected abstract boolean runMyAction(IEditorPart part);
  
  protected abstract String myStatusFailedMessage();
}