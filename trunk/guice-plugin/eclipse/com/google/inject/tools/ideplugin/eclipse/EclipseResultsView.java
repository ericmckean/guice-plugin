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

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.SWT;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import com.google.inject.tools.ideplugin.results.Results;
import com.google.inject.tools.ideplugin.results.ResultsView;
import com.google.inject.tools.ideplugin.results.ActionStringBuilder.ActionString;
import com.google.inject.tools.ideplugin.results.ActionStringBuilder.ActionStringElement;
import com.google.inject.tools.ideplugin.ActionsHandler;
import com.google.inject.tools.suite.Messenger;

/**
 * The Eclipse implementation of the {@link ResultsView}, a view for displaying
 * results and error messages (a view is a tab in the lower panel).
 * 
 * {@inheritDoc ResultsView}
 * 
 * @author Darren Creutz <dcreutz@gmail.com>
 */
class EclipseResultsView extends ViewPart implements ResultsView {
  private FormToolkit toolkit;
  private ScrolledForm form;
  private Composite parent;
  private Messenger messenger;

  /**
   * The constructor. This will be called by Eclipse internally.
   */
  public EclipseResultsView() {
    messenger = Activator.getGuicePlugin().getMessenger();
  }

  private ActionsHandler getActionsHandler() {
    return Activator.getGuicePlugin() != null ? Activator.getGuicePlugin()
        .getActionsHandler() : null;
  }

  /**
   * This is a callback that will allow us to create the viewer and initialize
   * it.
   */
  @Override
  public void createPartControl(Composite parent) {
    this.parent = parent;
    toolkit = new FormToolkit(parent.getDisplay());
  }

  private class HyperlinkListener implements IHyperlinkListener {
    private final ActionsHandler.Action action;

    public HyperlinkListener(ActionsHandler.Action action) {
      this.action = action;
    }

    public void linkActivated(HyperlinkEvent e) {
      getActionsHandler().run(action);
    }

    public void linkEntered(HyperlinkEvent e) {
    }

    public void linkExited(HyperlinkEvent e) {
    }
  }

  private void makeFormFromNode(Composite body, int depth, Results.Node node) {
    Form nodeForm = toolkit.createForm(body);
    Composite nodeFormBody = nodeForm.getBody();
    GridLayout layout = new GridLayout();
    layout.marginHeight = 0;
    layout.marginBottom = 0;
    layout.marginLeft = 10 + 10 * depth;
    layout.marginRight = 0;
    layout.marginTop = 0;
    layout.marginWidth = 0;
    layout.numColumns = node.getText().elements().size();
    layout.horizontalSpacing = 0;
    layout.verticalSpacing = 0;
    nodeFormBody.setLayout(layout);
    ActionString text = node.getText();
    for (ActionStringElement element : text.elements()) {
      if (element.action() == null
          || element.action() instanceof ActionsHandler.NullAction) {
        Label label = toolkit.createLabel(nodeFormBody, element.label());
        label.setToolTipText(element.tooltip());
        label.setEnabled(true);
      } else {
        Hyperlink link =
            toolkit.createHyperlink(nodeFormBody, element.label(), SWT.WRAP);
        link.addHyperlinkListener(new HyperlinkListener(element.action()));
        link.setToolTipText(element.tooltip());
        link.setEnabled(true);
      }
    }
    for (Results.Node child : node.children()) {
      makeFormFromNode(body, depth + 1, child);
    }
    nodeForm.pack();
  }

  public void useResults(Results results) {
    if (form != null) {
      form.dispose();
      form = null;
    }
    form = toolkit.createScrolledForm(parent);
    form.setExpandHorizontal(true);
    form.setExpandVertical(true);
    form.getBody().setLayout(new FillLayout());
    Composite body = toolkit.createComposite(form.getBody());
    GridLayout layout = new GridLayout();
    layout.marginHeight = 0;
    layout.marginBottom = 0;
    layout.marginLeft = 0;
    layout.marginRight = 0;
    layout.marginTop = 0;
    layout.marginWidth = 0;
    layout.horizontalSpacing = 0;
    layout.verticalSpacing = 0;
    body.setLayout(layout);
    Results.Node root = results.getRoot();
    form.setText(root.getTextString());
    this.setTitleToolTip(root.getTextString());
    for (Results.Node child : root.children()) {
      makeFormFromNode(body, 1, child);
    }
    body.pack();
    form.pack();
    form.reflow(true);
  }

  @Override
  public void setFocus() {
    if (form != null) {
      form.setFocus();
    }
  }

  public void displayResults(Results results) {
    this.useResults(results);
    try {
      this.getViewSite().getWorkbenchWindow().getActivePage().showView(
          "com.google.inject.tools.ideplugin.eclipse.EclipseResultsView");
    } catch (Exception e) {
      messenger.logException("Error with results view", e);
    }
  }
}
