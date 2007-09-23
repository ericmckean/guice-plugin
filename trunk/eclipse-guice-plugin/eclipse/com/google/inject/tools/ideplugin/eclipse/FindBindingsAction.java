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

/**
 * Runs the find bindings action based on the user pressing a key combination.
 * 
 * @author dcreutz@gmail.com (Darren Creutz)
 */
public class FindBindingsAction extends EclipseMenuAction {
  public FindBindingsAction() {
    super("Find Bindings", "findbindings.gif");
  }
  
  @Override
  public boolean runMyAction(IEditorPart part) {
    JavaElementResolver resolver = new JavaElementResolver(part);
    EclipseJavaElement javaElement = new EclipseJavaElement(resolver.getJavaElement());
    if (javaElement != null && javaElement.getType() != null) {
      guicePlugin.getBindingsEngine(javaElement,
         new EclipseJavaProject(resolver.getJavaElement().getJavaProject()));
      return true;
    } else {
      guicePlugin.getMessenger().display("Find Bindings not available in this context");
    }
    return false;
  }
  
  @Override
  protected String myTooltip() {
    return "Find Bindings";
  }

  @Override
  protected String myStatusFailedMessage() {
    return "Guice: Cannot resolve Java Element.";
  }
}