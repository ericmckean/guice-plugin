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

import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.ui.JavaUI;
import com.google.inject.tools.ideplugin.GotoFileHandler;
import com.google.inject.tools.ideplugin.ProjectManager;
import com.google.inject.tools.ideplugin.ActionsHandler.GotoFile;
import com.google.inject.tools.suite.Messenger;
import com.google.inject.Singleton;
import com.google.inject.Inject;

@Singleton
public class EclipseGotoFileHandler implements GotoFileHandler {
  private final ProjectManager projectManager;
  private final Messenger messenger;

  @Inject
  public EclipseGotoFileHandler(ProjectManager projectManager,
      Messenger messenger) {
    this.projectManager = projectManager;
    this.messenger = messenger;
  }

  public void run(GotoFile action) {
    try {
      String fixedName = action.getClassname().replace('$', '.');
      IType type =
          ((EclipseJavaProject) projectManager.getCurrentProject())
              .getIJavaProject().findType(fixedName);
      JavaUI.openInEditor(type);
    } catch (Exception exception) {
      messenger.logException("GotoFile Action Exception", exception);
    }
  }
}
