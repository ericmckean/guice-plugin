/**
 * Copyright (C) 2007 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.inject.tools.ideplugin.eclipse;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitEditor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import com.google.inject.tools.ideplugin.ProjectManager;
import com.google.inject.tools.ideplugin.module.ModuleSelectionView;

/**
 * Responds to the user choosing configure from the Guice menu by opening the configure
 * dialog.
 * 
 * @author Darren Creutz <dcreutz@gmail.com>
 */
@SuppressWarnings("restriction")
//TODO: remove internal class use if possible
public class GuicePluginConfigureAction implements IEditorActionDelegate, IObjectActionDelegate {
  private final ModuleSelectionView moduleSelectionView;
  private final ProjectManager projectManager;
  private IEditorPart editor;
  
  public GuicePluginConfigureAction() {
    this.moduleSelectionView = Activator.getGuicePlugin().getModuleSelectionView();
    this.projectManager = Activator.getGuicePlugin().getProjectManager();
  }
  
  public void setActiveEditor(IAction action, IEditorPart targetEditor) {
    this.editor = targetEditor;
  }
  public void setActivePart(IAction action, IWorkbenchPart targetPart) {}
  public void selectionChanged(IAction action, ISelection selection) {}

  private class NonUIThread extends Thread {
    private final ICompilationUnit cu;
    public NonUIThread(ICompilationUnit cu) {
      this.cu = cu;
    }
    @Override
    public void run() {
      projectManager.getModuleManager(new EclipseJavaProject(cu.getJavaProject())).updateModules(true);
      moduleSelectionView.show();
    }
  }
  
  public void run(IAction action) {
    ICompilationUnit cu = JavaPlugin.getDefault().getWorkingCopyManager().getWorkingCopy(((CompilationUnitEditor)editor).getEditorInput());
    Thread nonUIThread = new NonUIThread(cu);
    nonUIThread.start();
  }
}
