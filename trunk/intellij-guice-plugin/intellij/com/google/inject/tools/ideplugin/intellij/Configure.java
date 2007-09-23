package com.google.inject.tools.ideplugin.intellij;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataConstants;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.project.Project;

/**
 * Created by IntelliJ IDEA.
 * User: d
 * Date: Sep 21, 2007
 * Time: 8:39:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class Configure extends AnAction {
  public void actionPerformed(AnActionEvent anActionEvent) {
    Project project = (Project)anActionEvent.getDataContext().getData(DataConstants.PROJECT);
    IntellijJavaProject javaProject = new IntellijJavaProject(project);
    Plugin.getGuicePlugin().configurePlugin(javaProject, true);
  }
}
