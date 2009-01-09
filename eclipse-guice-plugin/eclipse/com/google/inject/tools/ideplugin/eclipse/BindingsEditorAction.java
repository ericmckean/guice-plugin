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
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;

/**
 * The action to take when the user selects some text and chooses "Find
 * Bindings" from the right click menu.
 * 
 * @author Darren Creutz (dcreutz@gmail.com)
 */
public class BindingsEditorAction implements IEditorActionDelegate {
  private IEditorPart editor;
  private GuicePlugin guicePlugin;

  public BindingsEditorAction() {
    super();
    guicePlugin = Activator.getDefault().getGuicePlugin();
  }

  public void setActiveEditor(IAction action, IEditorPart targetEditor) {
    this.editor = targetEditor;
  }

  /**
   * Eclipse callback to have us run the bindings engine.
   */
  public void run(IAction action) {
    JavaElementResolver resolver = new JavaElementResolver(editor);
    EclipseJavaElement javaElement = resolver.getJavaElement()==null ? null :
        new EclipseJavaElement(resolver.getJavaElement());
    if (javaElement != null && javaElement.getType() != null) {
      guicePlugin.getBindingsEngine(javaElement,
          new EclipseJavaProject(resolver.getJavaElement().getJavaProject()));
    } else {
      IStatusLineManager statusManager = editor.getEditorSite().getActionBars().getStatusLineManager();
      if (resolver.getSelection() != null) {
        statusManager.setMessage(PluginTextValues.GUICE_PLUGIN_NAME + ": " +
            PluginTextValues.SELECTION_NOT_JAVA_ELEMENT + " (" + resolver.getSelection() + ")");
      } else {
        statusManager.setMessage(PluginTextValues.GUICE_PLUGIN_NAME + ": " + PluginTextValues.SELECTION_NOT_JAVA_ELEMENT);
      }
    }
  }

  public void selectionChanged(IAction action, ISelection selection) {
  }
}
