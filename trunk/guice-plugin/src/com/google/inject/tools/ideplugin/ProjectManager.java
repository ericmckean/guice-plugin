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

package com.google.inject.tools.ideplugin;

import com.google.inject.tools.ideplugin.JavaProject;
import com.google.inject.tools.suite.JavaManager;
import com.google.inject.tools.suite.module.ModuleManager;
import com.google.inject.tools.suite.module.ModuleManager.PostUpdater;

/**
 * Manages the set of open projects in the user's code.
 * 
 * @author Darren Creutz (dcreutz@gmail.com)
 */
public interface ProjectManager {
  /**
   * Notify the ProjectManager that the given project has been opened.
   */
  public void projectOpened(JavaProject javaProject);

  /**
   * Notify the ProjectManager that the given project has been closed.
   */
  public void projectClosed(JavaProject javaProject);

  /**
   * Return the {@link ModuleManager} for the given project.
   */
  public ModuleManager getModuleManager(JavaProject javaProject);

  /**
   * Return the {@link ModuleManager} for the current project.
   */
  public ModuleManager getModuleManager();

  /**
   * Return the current (last accessed) project.
   */
  public JavaProject getCurrentProject();
  
  /**
   * Find new contexts in the given project and run the post updater afterward.
   */
  public void findNewContexts(JavaManager javaManager,
      PostUpdater postUpdater, boolean backgroundAutomatically);
  
  /**
   * Find new contexts in the given project.
   */
  public boolean findNewContexts(JavaManager javaManager);
  
  /**
   * Return the java manager for the given module manager.
   */
  public JavaManager getJavaManager(ModuleManager moduleManager);
}
