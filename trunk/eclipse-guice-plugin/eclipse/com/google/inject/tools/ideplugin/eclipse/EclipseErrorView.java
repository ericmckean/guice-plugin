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
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.console.actions.ClearOutputAction;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.texteditor.IUpdate;

/**
 * Displays error output from the Guice plugin that is logged to the
 * {@link com.google.inject.tools.suite.Messenger}.
 * 
 * @author Darren Creutz (dcreutz@gmail.com)
 */
public class EclipseErrorView extends ViewPart {
  private IDocument document;
  private ITextViewer viewer;

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
    viewer = new TextViewer(parent, SWT.H_SCROLL | SWT.V_SCROLL);
    
    viewer.setEditable(false);
    document = new Document();
    viewer.setDocument(document);
    document.set("Guice Error Log\n");
    viewer.activatePlugins();
    
    createActions();
    initializeToolBar();

    MenuManager menuMgr = new MenuManager("#PopUp"); //$NON-NLS-1$
    menuMgr.setRemoveAllWhenShown(true);
    menuMgr.addMenuListener(new IMenuListener() {
      public void menuAboutToShow(IMenuManager mgr) {
        fillContextMenu(mgr);
      }
    });
    Menu menu = menuMgr.createContextMenu(viewer.getTextWidget());
    viewer.getTextWidget().setMenu(menu);
    getSite().registerContextMenu(menuMgr, viewer.getSelectionProvider());
  }

  private Map<String, IAction> fGlobalActions = new HashMap<String, IAction>();
  private IAction fClearDisplayAction;

  protected void fillContextMenu(IMenuManager menu) {
    if (viewer.getDocument() == null) {
      return;
    }
    menu.add(new Separator());
    menu.add(fGlobalActions.get(ActionFactory.CUT.getId()));
    menu.add(fGlobalActions.get(ActionFactory.COPY.getId()));
    menu.add(fGlobalActions.get(ActionFactory.SELECT_ALL.getId()));
    menu.add(fClearDisplayAction);
  }

  protected void createActions() {

    fClearDisplayAction= new ClearOutputAction(viewer);

    IActionBars actionBars = getViewSite().getActionBars();

    IAction action= new DisplayViewAction(this, ITextOperationTarget.CUT);
    action.setText(PluginTextValues.CUT);
    setGlobalAction(actionBars, ActionFactory.CUT.getId(), action);

    action= new DisplayViewAction(this, ITextOperationTarget.COPY);
    action.setText(PluginTextValues.COPY);
    setGlobalAction(actionBars, ActionFactory.COPY.getId(), action);


    action= new DisplayViewAction(this, ITextOperationTarget.SELECT_ALL);
    action.setText(PluginTextValues.SELECT_ALL);
    setGlobalAction(actionBars, ActionFactory.SELECT_ALL.getId(), action);

  }

  protected void setGlobalAction(IActionBars actionBars, String actionID, IAction action) {
    fGlobalActions.put(actionID, action);
    actionBars.setGlobalActionHandler(actionID, action);
  }

  private void initializeToolBar() {
    IToolBarManager tbm = getViewSite().getActionBars().getToolBarManager();
    tbm.add(fClearDisplayAction);
    createToolbarActions();
    getViewSite().getActionBars().updateActionBars();
  }

  public class DisplayViewAction extends Action implements IUpdate {
    private int fOperationCode= -1;
    private ITextOperationTarget fOperationTarget;
    private IAdaptable fTargetProvider;


    public DisplayViewAction(ITextOperationTarget target, int operationCode) {
      super();
      fOperationTarget= target;
      fOperationCode= operationCode;
      update();
    }

    public DisplayViewAction(IAdaptable targetProvider, int operationCode) {
      super();
      fTargetProvider= targetProvider;
      fOperationCode= operationCode;
      update();
    }

    @Override
    public void run() {
      if (fOperationCode != -1 && fOperationTarget != null)
        fOperationTarget.doOperation(fOperationCode);
    }


    public void update() {
      if (fOperationTarget == null && fTargetProvider != null && fOperationCode != -1){
        fOperationTarget= (ITextOperationTarget) fTargetProvider.getAdapter(ITextOperationTarget.class);
      }

      boolean isEnabled= (fOperationTarget != null && fOperationTarget.canDoOperation(fOperationCode));
      setEnabled(isEnabled);
    }
  }

  @Override
  public void setFocus() {
  }

  public void displayError(String message) {
    String dateString =
      new SimpleDateFormat("dd/MM HH:mm:ss").format(new Date());
    String msg = "[" + dateString + "]   " + message;
    document.set(document.get() + msg);
  }
  
  protected void createToolbarActions() {
    IToolBarManager toolbar = getViewSite().getActionBars().getToolBarManager();
    toolbar.add(new ShowBindingsViewAction());
    toolbar.add(new FindBindingsAction());
    toolbar.add(new RunModulesNowAction2());
    toolbar.add(new GuicePluginConfigureAction2());
  }
}
