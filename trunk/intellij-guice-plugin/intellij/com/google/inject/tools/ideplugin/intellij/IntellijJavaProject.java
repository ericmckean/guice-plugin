package com.google.inject.tools.ideplugin.intellij;

import com.google.inject.tools.ideplugin.JavaProject;
import com.google.inject.tools.ideplugin.IDEPluginSettings;
import com.intellij.openapi.project.Project;

import java.util.List;
import java.util.Collections;

/**
 * Created by IntelliJ IDEA.
 * User: d
 * Date: Sep 21, 2007
 * Time: 9:44:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class IntellijJavaProject extends JavaProject {
  private final Project project;

  public IntellijJavaProject(Project project) {
    this.project = project;
  }

  public void saveSettings(IDEPluginSettings projectSettings) {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  public IDEPluginSettings loadSettings() {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public String getName() {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public String getJavaCommand() throws Exception {
    return "java";
  }

  public List<String> getJavaFlags() throws Exception {
    return Collections.<String>emptyList();
  }

  public String getSnippetsClasspath() throws Exception {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public String getGuiceClasspath() throws Exception {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  public String getProjectClasspath() throws Exception {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }
}
