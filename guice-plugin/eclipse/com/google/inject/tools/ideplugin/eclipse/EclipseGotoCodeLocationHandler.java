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
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.ui.texteditor.ITextEditor;

import com.google.inject.tools.ideplugin.GotoCodeLocationHandler;
import com.google.inject.tools.ideplugin.ProjectManager;
import com.google.inject.tools.ideplugin.ActionsHandler.GotoCodeLocation;
import com.google.inject.tools.suite.Messenger;
import com.google.inject.Singleton;
import com.google.inject.Inject;

@Singleton
public class EclipseGotoCodeLocationHandler implements GotoCodeLocationHandler {
  private final ProjectManager projectManager;
  private final Messenger messenger;

  @Inject
  public EclipseGotoCodeLocationHandler(ProjectManager projectManager,
      Messenger messenger) {
    this.projectManager = projectManager;
    this.messenger = messenger;
  }

  public void run(GotoCodeLocation action) {
    try {
      EclipseJavaProject project =
          (EclipseJavaProject) projectManager.getCurrentProject();
      String fixedName = action.getStackTraceElement().getClassName().replace('$', '.');
      IType type =
        project.getIJavaProject().findType(fixedName);
      ICompilationUnit cu = type.getCompilationUnit();
      if (cu == null) {
        JavaUI.openInEditor(type);
      } else {
        ITextEditor editor = ((ITextEditor) JavaUI.openInEditor(cu));
        int offset =
          editor.getDocumentProvider().getDocument(editor.getEditorInput())
          .getLineOffset(action.location() - 1);
        int length =
          editor.getDocumentProvider().getDocument(editor.getEditorInput())
          .getLineLength(action.location() - 1);
        editor.selectAndReveal(offset, length);
      }
    } catch (Exception exception) {
      messenger.logException("GotoCodeLocation Action Exception", exception);
    }
  }
}
