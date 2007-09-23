package com.google.inject.tools.ideplugin.intellij;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataConstants;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;

/**
 * Created by IntelliJ IDEA.
 * User: d
 * Date: Sep 21, 2007
 * Time: 8:39:29 PM
 * To change this template use File | Settings | File Templates.
 */
public class FindBindings extends AnAction {
  public void actionPerformed(AnActionEvent anActionEvent) {
    Project project = (Project)anActionEvent.getDataContext().getData(DataConstants.PROJECT);
    IntellijJavaProject javaProject = new IntellijJavaProject(project);

    Editor editor = (Editor)anActionEvent.getDataContext().getData(DataConstants.EDITOR);
    String selection = editor.getSelectionModel().getSelectedText();

    PsiElement element = (PsiElement)anActionEvent.getDataContext().getData(DataConstants.PSI_ELEMENT);
    IntellijJavaElement javaElement = new IntellijJavaElement(element);

    if (javaElement != null && javaElement.getType() != null) {
      Plugin.getGuicePlugin().getBindingsEngine(javaElement, javaProject);
    } else {
      if (selection != null) {
        Plugin.getGuicePlugin().getMessenger().display("Selection '" + selection + "' is not a Java Element.");
      } else {
        Plugin.getGuicePlugin().getMessenger().display("Selection is not a Java Element.");
      }
    }
  }
}
