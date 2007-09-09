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

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.javaeditor.ClassFileEditor;
import org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitEditor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;

/**
 * The action to take when the user selects some text and chooses "Find
 * Bindings" from the right click menu.
 * 
 * @author Darren Creutz (dcreutz@gmail.com)
 */
@SuppressWarnings("restriction")
public class BindingsEditorAction implements IEditorActionDelegate {
  private IEditorPart editor;
  private GuicePlugin guicePlugin;

  public BindingsEditorAction() {
    super();
    guicePlugin = Activator.getGuicePlugin();
  }

  public void setActiveEditor(IAction action, IEditorPart targetEditor) {
    this.editor = targetEditor;
  }

  /**
   * Eclipse callback to have us run the bindings engine.
   */
  public void run(IAction action) {
    IEditorInput editorInput = null;
    if (editor instanceof CompilationUnitEditor) {
      editorInput = ((CompilationUnitEditor)editor).getEditorInput();
    } else if (editor instanceof ClassFileEditor) {
      editorInput = ((ClassFileEditor)editor).getEditorInput();
    }
    if (editorInput != null) {
      ICompilationUnit cu = JavaPlugin.getDefault()
      .getWorkingCopyManager().getWorkingCopy(editorInput);
      ITextSelection selection =
        (ITextSelection) editor.getSite().getSelectionProvider().getSelection();
      IJavaElement element = null;
      try {
        IJavaElement[] elements =
          cu.codeSelect(selection.getOffset(), selection.getLength());
        if (elements.length > 0) {
          element = elements[0];
        }
      } catch (JavaModelException exception) {
        element = null;
      }
      EclipseJavaElement javaElement = null;
      if (element != null) {
        javaElement = new EclipseJavaElement(element, cu);
      }
      if (javaElement != null && javaElement.getType() != null) {
        guicePlugin.getBindingsEngine(javaElement,
            new EclipseJavaProject(element.getJavaProject()));
      } else {
        IStatusLineManager statusManager = editor.getEditorSite().getActionBars().getStatusLineManager();
        if (selection != null) {
          statusManager.setMessage(
              "Selection is not a Java element: " + selection.getText());
        } else {
          statusManager.setMessage("Selection is not a Java element.");
        }
      }
    } else {
      guicePlugin.getMessenger().display("Find Bindings not available in this context");
    }
  }

  public void selectionChanged(IAction action, ISelection selection) {
  }
}
