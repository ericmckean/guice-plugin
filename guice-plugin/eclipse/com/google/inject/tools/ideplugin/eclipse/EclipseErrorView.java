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
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.part.ViewPart;

/**
 * Displays error output from the Guice plugin that is logged to the
 * {@link com.google.inject.tools.suite.Messenger}.
 * 
 * @author Darren Creutz <dcreutz@gmail.com>
 */
public class EclipseErrorView extends ViewPart {
  private FormToolkit toolkit;
  private ScrolledForm form;
  private Composite body;

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
    toolkit = new FormToolkit(parent.getDisplay());
    form = toolkit.createScrolledForm(parent);
    form.setExpandHorizontal(true);
    form.setExpandVertical(true);
    form.getBody().setLayout(new FillLayout());
    body = toolkit.createComposite(form.getBody());
    GridLayout layout = new GridLayout();
    layout.marginHeight = 3;
    layout.marginBottom = 0;
    layout.marginLeft = 7;
    layout.marginRight = 0;
    layout.marginTop = 0;
    layout.marginWidth = 0;
    layout.horizontalSpacing = 0;
    layout.verticalSpacing = 2;
    body.setLayout(layout);
    form.setText("Guice Error Log");
    form.pack();
    form.reflow(true);
  }

  @Override
  public void setFocus() {
    if (form != null) {
      form.setFocus();
    }
  }

  public void displayError(String message) {
    String dateString =
        new SimpleDateFormat("dd/MM HH:mm:ss").format(new Date());
    String msg = "[" + dateString + "]   " + message;
    FormText formText = toolkit.createFormText(body, true);
    formText.setText(msg, false, true);
    formText.setFocus();
    form.reflow(true);
  }
}
