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
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.javaeditor.ClassFileEditor;
import org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitEditor;
import org.eclipse.jdt.internal.ui.javaeditor.IClassFileEditorInput;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;

/**
 * Resolve the IJavaProject from an IEditorPart.
 * 
 * @author Darren Creutz (dcreutz@gmail.com)
 */
@SuppressWarnings("restriction")
class JavaProjectResolver {
  private final IJavaProject project;
  
  public JavaProjectResolver(IEditorPart editor) {
    if (editor instanceof CompilationUnitEditor) {
      IEditorInput editorInput = ((CompilationUnitEditor)editor).getEditorInput();
      ICompilationUnit cu = JavaPlugin.getDefault()
          .getWorkingCopyManager().getWorkingCopy(editorInput);
      project = cu.getJavaProject();
    } else if (editor instanceof ClassFileEditor) {
      IClassFileEditorInput editorInput = (IClassFileEditorInput)((ClassFileEditor)editor).getEditorInput();
      project = editorInput.getClassFile().getJavaProject();
    } else {
      project = null;
    }
  }
  
  public IJavaProject getProject() {
    return project;
  }
}
