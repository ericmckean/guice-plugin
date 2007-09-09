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

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.javaeditor.ClassFileEditor;
import org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitEditor;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;

/**
 * Runs the find bindings action based on the user pressing a key combination.
 * 
 * @author dcreutz@gmail.com (Darren Creutz)
 */
@SuppressWarnings("restriction")
public class FindBindingsAction extends EclipseMenuAction {
  @Override
  public boolean runMyAction(IEditorPart part) {
    IJavaElement element = null;
    ISelection sel = window.getSelectionService().getSelection();
    ITextSelection selection = (ITextSelection)sel;
    IEditorInput editorInput = null;
    if (part instanceof CompilationUnitEditor) {
      editorInput = ((CompilationUnitEditor)part).getEditorInput();
    } else if (part instanceof ClassFileEditor) {
      editorInput = ((ClassFileEditor)part).getEditorInput();
    }
    if (editorInput != null) {
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
      EclipseJavaElement javaElement = null;
      if (element != null) {
        ICompilationUnit cu2 = 
          (ICompilationUnit)element.getAncestor(IJavaElement.COMPILATION_UNIT);
        javaElement = new EclipseJavaElement(element, cu2);
      }
      if (javaElement != null && javaElement.getType() != null) {
        guicePlugin.getBindingsEngine(javaElement,
            new EclipseJavaProject(element.getJavaProject()));
        return true;
      }
    } else {
      guicePlugin.getMessenger().display("Find Bindings not available in this context");
    }
    return false;
  }

  @Override
  protected String myStatusFailedMessage() {
    return "Guice: Cannot resolve Java Element.";
  }
}