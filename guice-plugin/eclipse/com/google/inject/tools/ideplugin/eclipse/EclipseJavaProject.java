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

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.google.inject.tools.suite.JavaManager;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.osgi.framework.Bundle;

/**
 * Eclipse specific implementation of the {@link JavaManager}.
 * 
 * {@inheritDoc JavaManager}
 * 
 * @author Darren Creutz <dcreutz@gmail.com>
 */
class EclipseJavaProject implements JavaManager {
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

  public String getJavaCommand() throws Exception {
    // TODO: get java command from project
    return "java";
  }

  public String getProjectClasspath() throws Exception {
    final List<String> args = new ArrayList<String>();
    IClasspathEntry[] cp = new IClasspathEntry[0];

    try {
      cp = project.getResolvedClasspath(false);
    } catch (Exception e) {
      e.printStackTrace();
    }
    args.add(getProjectLocation(project));
    args.addAll(expandClasspath(cp));
    final StringBuilder args2 = new StringBuilder();
    for (int i = 0; i < args.size() - 1; i++) {
      args2.append(args.get(i));
      args2.append(":");
    }
    args2.append(args.get(args.size() - 1));
    return args2.toString();
  }
  
  public String getClasspathDelimiter() {
    return System.getProperty("path.separator");
  }

  private String getProjectLocation(IJavaProject project)
      throws JavaModelException {
    IResource resource = project.getResource();
    String resourceLocation = resource.getLocation().toOSString();
    String projectLocation =
        project.getOutputLocation().makeRelative().toOSString();
    return projectLocation.replaceFirst(project.getProject().getName(),
        resourceLocation);
  }

  private List<String> expandClasspath(IClasspathEntry[] entries)
      throws Exception {
    final List<String> args = new ArrayList<String>();
    for (IClasspathEntry entry : entries) {
      switch (entry.getEntryKind()) {
        case IClasspathEntry.CPE_CONTAINER:
          IClasspathContainer container =
              JavaCore.getClasspathContainer(entry.getPath(), project);
          args.addAll(expandClasspath(container.getClasspathEntries()));
          break;
        case IClasspathEntry.CPE_SOURCE:
          IResource resource =
              ResourcesPlugin.getWorkspace().getRoot().findMember(
                  entry.getPath());
          args.add(resource.getLocation().toOSString());
          break;
        case IClasspathEntry.CPE_LIBRARY:
          args.add(entry.getPath().makeAbsolute().toOSString());
          break;
        case IClasspathEntry.CPE_PROJECT:
          IResource presource =
              ResourcesPlugin.getWorkspace().getRoot().findMember(
                  entry.getPath());
          String resourceLocation = presource.getLocation().toOSString();
          String outputLocation =
              entry.getOutputLocation().makeRelative().toOSString();
          args.add(outputLocation.replaceFirst(presource.getName(),
              resourceLocation));
          break;
        case IClasspathEntry.CPE_VARIABLE:
          break;
        default:
          //never happens
      }
    }
    return args;
  }

  public String getSnippetsClasspath() throws Exception {
    Bundle bundle = Platform.getBundle("GuicePlugin");
    URL url = bundle.getResource("/");
    url = FileLocator.toFileURL(url);
    return url.getFile();
  }

  @Override
  public boolean equals(Object object) {
    if (object instanceof EclipseJavaProject) {
      EclipseJavaProject project = (EclipseJavaProject) object;
      return project.getIJavaProject().equals(getIJavaProject());
    }
    return false;
  }

  @Override
  public int hashCode() {
    return getIJavaProject().hashCode();
  }
}
