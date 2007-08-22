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
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
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
    IClasspathEntry[] cp = new IClasspathEntry[0];
    try {
     cp = project.getResolvedClasspath(false);
    } catch (Exception e) {
      e.printStackTrace();
    }
    final String workspacePath = project.getProject().getWorkspace().getRoot().getLocation().toOSString();
    final String projectPath = workspacePath + project.getOutputLocation().toOSString();
    args.add(projectPath);
    args.addAll(expandClasspath(cp, workspacePath));
    final StringBuilder args2 = new StringBuilder();
    for (int i=0;i<args.size()-1;i++) {
      args2.append(args.get(i));
      args2.append(":");
    }
    args2.append(args.get(args.size()-1));
    if (!hasprinted) {
      for (String arg : args) {
        System.out.println(arg);
      }
      hasprinted = true;
    }
    return args2.toString();
  }
  
  private static boolean hasprinted = false;
  
  private List<String> expandClasspath(IClasspathEntry[] entries, String workspacePath) throws Exception {
    final List<String> args = new ArrayList<String>();
    for (IClasspathEntry entry : entries ) {
      switch (entry.getEntryKind()) {
        case IClasspathEntry.CPE_CONTAINER:
          IClasspathContainer container = JavaCore.getClasspathContainer(entry.getPath(), project);
          args.addAll(expandClasspath(container.getClasspathEntries(), workspacePath));
          break;
        case IClasspathEntry.CPE_SOURCE:
          if (entry.getOutputLocation() != null) {
            args.add(entry.getOutputLocation().toOSString());
            args.add(workspacePath + entry.getOutputLocation().toOSString());
          } else {
            args.add(entry.getPath().toFile().getAbsolutePath());
            if (!hasprinted) System.out.println("no output loc     " + entry.getPath().toFile().getAbsolutePath());
          }
          break;
        case IClasspathEntry.CPE_LIBRARY:
          args.add(entry.getPath().makeAbsolute().toOSString());
          break;
        case IClasspathEntry.CPE_PROJECT:
          
        case IClasspathEntry.CPE_VARIABLE:
          
      }
    }
    return args;
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
  
  @Override
  public int hashCode() {
    return getIJavaProject().hashCode();
  }
}
