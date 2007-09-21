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

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.google.inject.tools.ideplugin.JavaProject;
import com.google.inject.tools.ideplugin.ProjectSettings;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jface.preference.IPreferenceStore;
import org.osgi.framework.Bundle;

/**
 * Eclipse specific implementation of the {@link com.google.inject.tools.ideplugin.JavaProject}.
 * 
 * {@inheritDoc JavaProject}
 * 
 * @author Darren Creutz (dcreutz@gmail.com)
 */
class EclipseJavaProject extends JavaProject {
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
    IVMInstall vm = JavaRuntime.getVMInstall(project);
    File jreHome = vm.getInstallLocation();
    String javacommand = getJavaCommand(jreHome);
    if (javacommand == null) {
      javacommand = "java";
    }
    return javacommand;
  }
  
  private String getJavaCommand(File jreHome) {
    for (File file : jreHome.getAbsoluteFile().listFiles()) {
      if (file.isDirectory()) {
        for (File file2 : file.listFiles()) {
          if (file2.getName().equals("java") || file2.getName().equals("java.exe")) {
            return file2.getAbsolutePath();
          }
        }
      }
    }
    return null;
  }
  
  public List<String> getJavaFlags() throws Exception {
    IVMInstall vm = JavaRuntime.getVMInstall(project);
    String[] args = vm.getVMArguments();
    List<String> flags = new ArrayList<String>();
    if (args != null && args.length > 0) {
      for (String arg : args) {
        flags.add(arg);
      }
    }
    return flags;
  }

  public String getProjectClasspath() throws Exception {
    final List<String> args = new ArrayList<String>();
    IClasspathEntry[] cp = new IClasspathEntry[0];
    try {
      cp = project.getResolvedClasspath(false);
    } catch (Throwable e) {}
    args.add(getProjectOutputLocation(project));
    args.addAll(expandClasspath(cp, getProjectName(project),
        getProjectLocation(project)));
    final StringBuilder args2 = new StringBuilder();
    for (int i = 0; i < args.size() - 1; i++) {
      args2.append(args.get(i));
      args2.append(":");
    }
    args2.append(args.get(args.size() - 1));
    return args2.toString();
  }

  private String getProjectOutputLocation(IJavaProject project)
      throws JavaModelException {
    IResource resource = project.getResource();
    String resourceLocation = resource.getLocation().toOSString();
    String projectLocation =
        project.getOutputLocation().makeRelative().toOSString();
    return projectLocation.replaceFirst(project.getProject().getName(),
        resourceLocation);
  }
  
  private String getProjectLocation(IJavaProject project) {
    IResource resource = project.getResource();
    String resourceLocation = resource.getLocation().toOSString();
    return resourceLocation;
  }
  
  private String getProjectName(IJavaProject project) {
    return project.getProject().getName();
  }

  private List<String> expandClasspath(IClasspathEntry[] entries, 
      String projectName, String projectLocation)
      throws Exception {
    final List<String> args = new ArrayList<String>();
    IResource presource;
    String resourceLocation;
    String path;
    for (IClasspathEntry entry : entries) {
      switch (entry.getEntryKind()) {
        case IClasspathEntry.CPE_CONTAINER:
          IClasspathContainer container =
              JavaCore.getClasspathContainer(entry.getPath(), project);
          args.addAll(expandClasspath(container.getClasspathEntries(),
              projectName, projectLocation));
          break;
        case IClasspathEntry.CPE_SOURCE:
          IResource resource =
              ResourcesPlugin.getWorkspace().getRoot().findMember(
                  entry.getPath());
          path = resource.getLocation().makeAbsolute().toOSString();
          if (path.startsWith("/"+projectName)) {
            args.add(path.replaceFirst("/"+projectName, projectLocation));
          } else {
            args.add(path);
          }
          break;
        case IClasspathEntry.CPE_LIBRARY:
          path = entry.getPath().makeAbsolute().toOSString();
          if (path.startsWith("/"+projectName)) {
            args.add(path.replaceFirst("/"+projectName, projectLocation));
          } else {
            args.add(path);
          }
          break;
        case IClasspathEntry.CPE_PROJECT:
          presource =
              ResourcesPlugin.getWorkspace().getRoot().findMember(
                  entry.getPath());
          resourceLocation = presource.getLocation().makeAbsolute().toOSString();
          String outputLocation = resourceLocation;
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
  
  public String getGuiceClasspath() throws Exception {
    String guiceJar = getJarClasspath("lib/Guice/guice-r364.jar");
    String aoPath = getJarClasspath("lib/Guice/aopalliance.jar");
    String asmPath = getJarClasspath("lib/Guice/asm-2.2.3.jar");
    String cglibPath = getJarClasspath("lib/Guice/cglib-2.2_beta1.jar");
    return guiceJar + getClasspathDelimiter() + aoPath + getClasspathDelimiter()
        + asmPath + getClasspathDelimiter() + cglibPath;
  }
  
  private String getJarClasspath(String jarFile) throws Exception {
    Bundle bundle = Platform.getBundle("GuicePlugin");
    URL url = bundle.getEntry(jarFile);
    url = FileLocator.toFileURL(url);
    return url.getFile();
  }
  
  @Override
  public String getName() {
    return project.getProject().getName();
  }
  
  @Override
  public void saveSettings(ProjectSettings settings) {
    //handled in preference page OK button
  }
  
  @Override
  public ProjectSettings loadSettings() {
    IPreferenceStore store = Activator.getDefault().getPreferenceStore();
    ProjectSettings settings = new ProjectSettings(store.getString("com.google.inject.tools.ideplugin.eclipse.preferences"));
    return settings;
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
