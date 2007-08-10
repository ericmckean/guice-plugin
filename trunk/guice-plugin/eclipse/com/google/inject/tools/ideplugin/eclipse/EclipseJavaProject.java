package com.google.inject.tools.ideplugin.eclipse;

import java.util.ArrayList;
import java.util.List;

import com.google.inject.tools.ideplugin.JavaProject;

import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;

public class EclipseJavaProject implements JavaProject {
	private final IJavaProject project;
	
	public EclipseJavaProject(IJavaProject project) {
		this.project = project;
	}
	
	public IJavaProject getIJavaProject() {
		return project;
	}
  
  public String getJavaCommand() throws Exception {
    //TODO: fix this
    return "java";
  }
  
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
  
  public String getSnippetsClasspath() throws Exception {
    //TODO: do this
    return "";
  }
}
