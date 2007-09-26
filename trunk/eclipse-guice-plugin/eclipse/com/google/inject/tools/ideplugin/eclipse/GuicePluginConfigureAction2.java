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
 * Run the Module Contexts Configure dialog.
 * 
 * @author Darren Creutz (dcreutz@gmail.com)
 */
public class GuicePluginConfigureAction2 extends EclipseMenuAction {
  public GuicePluginConfigureAction2() {
    super(PluginTextValues.CONFIGURE, PluginDefinitionValues.CONFIGURE_ICON);
  }
  
  @Override
  protected boolean runMyAction(IEditorPart editor) {
    JavaProject project = new EclipseJavaProject(new JavaProjectResolver(editor).getProject());
    if (project != null) {
      Activator.getGuicePlugin().configurePlugin(project, true);
      return true;
    }
    return false;
  }
  
  @Override
  protected String myTooltip() {
    return PluginTextValues.CONFIGURE;
  }
  
  @Override
  protected String myStatusFailedMessage() {
    return PluginTextValues.CANNOT_RESOLVE_JAVA_PROJECT;
  }
}
