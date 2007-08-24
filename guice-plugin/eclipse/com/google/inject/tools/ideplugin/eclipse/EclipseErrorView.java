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

import java.text.SimpleDateFormat;
import java.util.Date;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.part.ViewPart;

/**
 * Displays error output from the Guice plugin that is logged to the
 * {@link com.google.inject.tools.Messenger}.
 * 
 * @author Darren Creutz <dcreutz@gmail.com>
 */
public class EclipseErrorView extends ViewPart {
  private Action clearAction;
  private FormToolkit toolkit;
  private ScrolledForm form;
  private Composite parent;

  /**
   * The constructor. This will be called by Eclipse internally.
   */
  public EclipseErrorView() {
  }

  /**
   * This is a callback that will allow us to create the viewer and initialize
   * it.
   */
  @Override
  public void createPartControl(Composite parent) {
    this.parent = parent;
    toolkit = new FormToolkit(parent.getDisplay());
    clear();
  }

  private void showMessage(String message) {
    MessageDialog.openInformation(new Shell(), "Guice Errors", message);
  }

  /**
   * (non-Javadoc)
   * 
   * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
   */
  @Override
  public void setFocus() {
    if (form != null) {
      form.setFocus();
    }
  }

  /**
   * (non-Javadoc)
   * 
   * @see com.google.inject.tools.ideplugin.results.ResultsView#displayResults(com.google.inject.tools.ideplugin.results.Results)
   */
  public void displayError(String message) {
    String dateString =
        new SimpleDateFormat("dd/MM HH:mm:ss").format(new Date());
    String msg = "[" + dateString + "]   " + message;
    toolkit.createFormText(form.getBody(), true).setText(msg, false, true);
    form.reflow(true);
    try {
      this.getViewSite().getWorkbenchWindow().getActivePage().showView(
          "com.google.inject.tools.ideplugin.eclipse.EclipseErrorView");
    } catch (Exception e) {
      this.showMessage(e.toString());
    }
  }

  private void createMenu() {
    clearAction = new Action("Clear...") {
      @Override
      public void run() {
        clear();
      }
    };
    clearAction.setEnabled(true);
    MenuManager mgr = new MenuManager();
    mgr.addMenuListener(new IMenuListener() {
      public void menuAboutToShow(IMenuManager mgr) {
        mgr.add(clearAction);
      }
    });
    Menu menu = mgr.createContextMenu(form);
  }

  protected void clear() {
    if (form != null) {
      form.dispose();
      form = null;
    }
    form = toolkit.createScrolledForm(parent);
    form.setExpandHorizontal(true);
    form.setExpandVertical(true);
    GridLayout layout = new GridLayout();
    layout.marginHeight = 3;
    layout.marginBottom = 0;
    layout.marginLeft = 7;
    layout.marginRight = 0;
    layout.marginTop = 0;
    layout.marginWidth = 0;
    layout.horizontalSpacing = 0;
    layout.verticalSpacing = 2;
    form.getBody().setLayout(layout);
    form.setText("Guice Error Log");
    createMenu();
    form.reflow(true);
  }
}
