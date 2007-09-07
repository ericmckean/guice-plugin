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
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitEditor;
import org.eclipse.ui.IEditorPart;

/**
 * Runs the find bindings action based on the user pressing a key combination.
 * 
 * @author dcreutz@gmail.com (Darren Creutz)
 */
@SuppressWarnings("restriction")
public class RunModulesNowAction2 extends EclipseMenuAction {
  @Override
  protected boolean runMyAction(IEditorPart part) {
    ICompilationUnit cu = JavaPlugin.getDefault()
        .getWorkingCopyManager().getWorkingCopy(((CompilationUnitEditor)part).getEditorInput());
    guicePlugin.runModulesNow(new EclipseJavaProject(cu.getJavaProject()), false);
    return true;
  }
  
  @Override
  protected String myStatusFailedMessage() {
    return "Guice: Cannot resolve project.";
  }
}