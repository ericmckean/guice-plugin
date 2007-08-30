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
import org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitEditor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;

/**
 * Runs the find bindings action based on the user pressing a key combination.
 * 
 * @author dcreutz@gmail.com (Darren Creutz)
 */
@SuppressWarnings("restriction")
public class FindBindingsAction implements IWorkbenchWindowActionDelegate {
  private GuicePlugin guicePlugin;

  public FindBindingsAction() {
    super();
    guicePlugin = Activator.getGuicePlugin();
  }
  
  public void dispose() {
  }

  public void init(IWorkbenchWindow window) {
  }

  public void run(IAction action) {
    IJavaElement element = null;
    IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
    ISelection sel = window.getSelectionService().getSelection();
    ITextSelection selection = (ITextSelection)sel;
    IWorkbenchPart part = window.getPartService().getActivePart();
    if (part instanceof IEditorPart) {
      IEditorInput editorInput = ((CompilationUnitEditor)part).getEditorInput();
      ICompilationUnit cu = JavaPlugin.getDefault()
        .getWorkingCopyManager().getWorkingCopy(editorInput);
      try {
        IJavaElement[] elements =
          cu.codeSelect(selection.getOffset(), selection.getLength());
        if (elements.length > 0) {
          element = elements[0];
        }
      } catch (JavaModelException exception) {
        element = null;
      }
    }
    EclipseJavaElement javaElement = null;
    if (element != null) {
      ICompilationUnit cu = 
        (ICompilationUnit)element.getAncestor(IJavaElement.COMPILATION_UNIT);
      javaElement = new EclipseJavaElement(element, cu);
    }
    if (javaElement != null && javaElement.getType() != null) {
      guicePlugin.getBindingsEngine(javaElement,
          new EclipseJavaProject(element.getJavaProject()));
    } else {
      if (part instanceof IEditorPart) {
        IEditorPart editor = (IEditorPart)part;
        IStatusLineManager statusManager = editor.getEditorSite().getActionBars().getStatusLineManager();
        if (selection != null) {
          statusManager.setMessage(
              "Selection is not a Java element: " + selection.getText());
        } else {
          statusManager.setMessage("Selection is not a Java element.");
        }
      } else {
        guicePlugin.getMessenger().display("Selection does not have bindings.");
      }
    }
  }

  public void selectionChanged(IAction action, ISelection selection) {
  }
}