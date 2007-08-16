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

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.google.inject.tools.JavaManager;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.osgi.framework.Bundle;

/**
 * Eclipse specific implementation of the {@link JavaManager}.
 * 
 * @author Darren Creutz <dcreutz@gmail.com>
 */
public class EclipseJavaProject implements JavaManager {
  private final IJavaProject project;
  
  public EclipseJavaProject(IJavaProject project) {
    this.project = project;
  }
  
  /**
   * Return the underlying eclipse project.
   */
  public IJavaProject getIJavaProject() {
    return project;
  }
  
  /**
   * (non-Javadoc)
   * @see com.google.inject.tools.JavaManager#getJavaCommand()
   */
  public String getJavaCommand() throws Exception {
    //TODO: fix this
    return "java";
  }
  
  /**
   * (non-Javadoc)
   * @see com.google.inject.tools.JavaManager#getProjectClasspath()
   */
  public String getProjectClasspath() throws Exception {
    final List<String> args = new ArrayList<String>();
    final IClasspathEntry[] cp = project.getResolvedClasspath(true);
    final String workspacePath = project.getProject().getWorkspace().getRoot().getLocation().toOSString();
    final String projectPath = workspacePath + project.getOutputLocation().toOSString();
    args.add(projectPath);
    for (IClasspathEntry entry : cp ) {
      if (entry.getOutputLocation() != null) {
        args.add(entry.getOutputLocation().toOSString());
      }
      args.add(entry.getPath().toOSString());
    }
    final StringBuilder args2 = new StringBuilder();
    for (int i=0;i<args.size()-1;i++) {
      args2.append(args.get(i));
      args2.append(":");
    }
    args2.append(args.get(args.size()-1));
    return args2.toString();
  }
  
  /**
   * (non-Javadoc)
   * @see com.google.inject.tools.JavaManager#getSnippetsClasspath()
   */
  public String getSnippetsClasspath() throws Exception {
    Bundle bundle = Platform.getBundle("GuicePlugin");
    URL url = bundle.getResource("/");
    url = FileLocator.toFileURL(url);
    return url.getFile();
  }
  
  @Override
  public boolean equals(Object object) {
    if (object instanceof EclipseJavaProject) {
      EclipseJavaProject project = (EclipseJavaProject)object;
      return project.getIJavaProject().equals(getIJavaProject());
    }
    return false;
  }
}
