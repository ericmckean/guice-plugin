package com.google.inject.tools.ideplugin.intellij;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataConstants;
import com.intellij.openapi.project.Project;


public class RunContextsNow extends AnAction {
  public void actionPerformed(AnActionEvent anActionEvent) {
    Project project = (Project)anActionEvent.getDataContext().getData(DataConstants.PROJECT);
    IntellijJavaProject javaProject = new IntellijJavaProject(project);
    Plugin.getGuicePlugin().runModulesNow(javaProject, false);
  }
}
