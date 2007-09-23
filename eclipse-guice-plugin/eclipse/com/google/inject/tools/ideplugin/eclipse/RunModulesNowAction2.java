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

import org.eclipse.ui.IEditorPart;

import com.google.inject.tools.ideplugin.JavaProject;

/**
 * Runs the find bindings action based on the user pressing a key combination.
 * 
 * @author dcreutz@gmail.com (Darren Creutz)
 */
public class RunModulesNowAction2 extends EclipseMenuAction {
  public RunModulesNowAction2() {
    super("Run Contexts Now", "runnow.gif");
  }
  
  @Override
  protected boolean runMyAction(IEditorPart part) {
    JavaProject project = new EclipseJavaProject(new JavaProjectResolver(part).getProject());
    if (project != null) {
      Activator.getGuicePlugin().runModulesNow(project, false);
    }
    return true;
  }
  
  @Override
  protected String myTooltip() {
    return "Run Contexts Now";
  }
  
  @Override
  protected String myStatusFailedMessage() {
    return "Guice: Cannot resolve project.";
  }
}