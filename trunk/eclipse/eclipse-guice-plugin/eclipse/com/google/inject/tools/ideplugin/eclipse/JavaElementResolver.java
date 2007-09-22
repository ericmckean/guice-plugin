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

import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.javaeditor.ClassFileEditor;
import org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitEditor;
import org.eclipse.jdt.internal.ui.javaeditor.IClassFileEditorInput;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;

/**
 * Resolve the selected JavaElement from the EditorPart.
 * 
 * @author Darren Creutz (dcreutz@gmail.com)
 */
@SuppressWarnings("restriction")
class JavaElementResolver {
  private IJavaElement element;
  private ITextSelection selection;
  
  public JavaElementResolver(IEditorPart editor) {
    element = null;
    selection = null;
    ISelectionProvider selectionProvider = editor.getSite().getSelectionProvider();
    ISelection theselection = selectionProvider.getSelection();
    if (editor instanceof CompilationUnitEditor) {
      IEditorInput editorInput = ((CompilationUnitEditor)editor).getEditorInput();
      ICompilationUnit compilationUnit = JavaPlugin.getDefault()
          .getWorkingCopyManager().getWorkingCopy(editorInput);
      selection = (ITextSelection) theselection;
      try {
        IJavaElement[] elements =
          compilationUnit.codeSelect(selection.getOffset(), selection.getLength());
        if (elements.length > 0) {
          element = elements[0];
        }
      } catch (JavaModelException exception) {
        element = null;
      }
    } else if (editor instanceof ClassFileEditor) {
      IClassFileEditorInput editorInput = (IClassFileEditorInput)((ClassFileEditor)editor).getEditorInput();
      IClassFile cf = editorInput.getClassFile();
      if (theselection instanceof IStructuredSelection) {
        element = (IJavaElement) ((IStructuredSelection)theselection).getFirstElement();
      } else if (theselection instanceof ITextSelection) {
        selection = (ITextSelection) theselection;
        try {
          element = cf.getElementAt(selection.getOffset());
        } catch (JavaModelException e) {}
      }
    }
  }
  
  public String getSelection() {
    if (selection != null) {
      return selection.getText();
    } else {
      return null;
    }
  }
  
  public IJavaElement getJavaElement() {
    return element;
  }
}
