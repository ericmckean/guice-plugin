/**
 * Copyright (C) 2007 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.inject.tools.ideplugin.eclipse;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import com.google.inject.tools.ideplugin.module.ModuleSelectionView;
import com.google.inject.tools.module.ModuleContextRepresentation;
import com.google.inject.tools.module.ModuleManager;
import com.google.inject.tools.module.ModuleContextRepresentation.ModuleInstanceRepresentation;

//TODO: write this

/**
 * Eclipse implementation of the {@link ModuleSelectionView}.
 * 
 * @author Darren Creutz <dcreutz@gmail.com>
 */
public class EclipseModuleDialog extends Dialog {	
  private final ModuleManager moduleManager;
  private Set<ModuleContextRepresentation> moduleContexts;
  private Set<ModuleContextRepresentation> activeModuleContexts;
  private boolean activateByDefault;
  private Shell parent;
  private Shell shell;
  private ScrolledForm form;
  
  public EclipseModuleDialog(Shell parent, ModuleManager moduleManager) {
    super(parent);
    this.parent = parent;
    shell = new Shell(parent, SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM);
    parent.setText("Guice Module Context Configuration");
    this.moduleManager = moduleManager;
  }
  
  public void display(Set<ModuleContextRepresentation> moduleContexts, Set<ModuleContextRepresentation> activeContexts, boolean activateByDefault) {
    this.moduleContexts = moduleContexts;
    this.activeModuleContexts = activeContexts;
    this.activateByDefault = activateByDefault;
    createContents(shell);
    form.reflow(true);
    shell.pack();
    shell.open();
    Display display = shell.getDisplay();
    while (!shell.isDisposed()) {
      if (!display.readAndDispatch()) {
        display.sleep();
      }
    }
    updateSettings();
  }
  
  protected void createContents(Shell shell) {
    checkboxListeners = new HashSet<CheckboxListener>();
    save = false;
    shell.setLayout(new GridLayout());
    FormToolkit toolkit = new FormToolkit(shell.getDisplay());
    form = toolkit.createScrolledForm(shell);
    form.setText("Available Module Contexts");
    GridLayout layout = new GridLayout();
    layout.marginHeight = 0;
    layout.marginBottom = 0;
    layout.marginLeft = 10;
    layout.marginRight = 0;
    layout.marginTop = 0;
    layout.marginWidth = 0;
    layout.horizontalSpacing = 0;
    layout.verticalSpacing = 0;
    if (moduleContexts == null) {
      System.out.println("null contexts");
      layout.numColumns = 1;
      form.setLayout(layout);
      toolkit.createLabel(form.getBody(), "No project is currently selected.");
      toolkit.createButton(form.getBody(), "Close", SWT.PUSH).addSelectionListener(cancelListener);
    } else if (moduleContexts.size() == 0) {
      System.out.println("zero contexts");
      layout.numColumns = 1;
      form.setLayout(layout);
      toolkit.createLabel(form.getBody(), "There are no module contexts available.");
      toolkit.createButton(form.getBody(), "Close", SWT.PUSH).addSelectionListener(cancelListener);
    } else {
      System.out.println("1+ contexts");
      layout.numColumns = 3;
      form.setLayout(layout);
      toolkit.createLabel(form.getBody(), "Active?", SWT.BOLD);
      toolkit.createLabel(form.getBody(), "Module Context", SWT.BOLD);
      toolkit.createLabel(form.getBody(), "Injector creation method", SWT.BOLD);
      for (ModuleContextRepresentation moduleContext : moduleContexts) {
        System.out.println("  " + moduleContext.getName());
        int style = activeModuleContexts.contains(moduleContext) ?
            SWT.CHECK | SWT.ON : SWT.CHECK | SWT.OFF;
        CheckboxListener listener = new CheckboxListener(moduleContext, activeModuleContexts.contains(moduleContext));
        toolkit.createButton(form.getBody(), "", style).addSelectionListener(listener);
        checkboxListeners.add(listener);
        toolkit.createLabel(form.getBody(), moduleContext.getName());
        StringBuilder text = new StringBuilder();
        text.append("Guice.createInjector(");
        Iterator<ModuleInstanceRepresentation> moduleInstances = moduleContext.getModules().iterator();
        for (ModuleInstanceRepresentation module = moduleInstances.next(); moduleInstances.hasNext(); module = moduleInstances.next()) {
          text.append(module.getClassName());
          if (moduleInstances.hasNext()) text.append(", ");
        }
        text.append(");");
        toolkit.createLabel(form.getBody(), text.toString());
      }
      toolkit.createLabel(form.getBody(), "");
      toolkit.createLabel(form.getBody(), "");
      toolkit.createLabel(form.getBody(), "");
      int style = SWT.CHECK | (activateByDefault ? SWT.ON : SWT.OFF);
      toolkit.createButton(form.getBody(), "", style).addSelectionListener(activateByDefaultListener);
      toolkit.createLabel(form.getBody(), "Activate new modules by default");
      toolkit.createLabel(form.getBody(), "");
      toolkit.createButton(form.getBody(), "Save", SWT.PUSH).addSelectionListener(saveListener);
      toolkit.createLabel(form.getBody(), "");
      toolkit.createButton(form.getBody(), "Cancel", SWT.PUSH).addSelectionListener(cancelListener);
    }
    form.pack();
    form.reflow(true);
  }
  
  private SelectionAdapter cancelListener = new SelectionAdapter() {
    @Override
    public void widgetSelected(SelectionEvent event) {
      save = false;
      shell.close();
    }
  };
  
  private SelectionAdapter saveListener = new SelectionAdapter() {
    @Override
    public void widgetSelected(SelectionEvent event) {
      save = true;
      shell.close();
    }
  };
  
  private SelectionAdapter activateByDefaultListener = new SelectionAdapter() {
    @Override
    public void widgetSelected(SelectionEvent event) {
      activateByDefault = !activateByDefault;
    }
  };
  
  private class CheckboxListener extends SelectionAdapter {
    private final ModuleContextRepresentation moduleContext;
    private boolean state;
    public CheckboxListener(ModuleContextRepresentation moduleContext, boolean state) {
      this.moduleContext = moduleContext;
      this.state = state;
    }
    @Override
    public void widgetSelected(SelectionEvent event) {
      state = !state;
    }
    public ModuleContextRepresentation getModuleContext() {
      return moduleContext;
    }
    public boolean getState() {
      return state;
    }
  }
  
  private Set<CheckboxListener> checkboxListeners;
  
  protected boolean save;
  
  protected void updateSettings() {
    if (save) {
      moduleManager.setActivateModulesByDefault(activateByDefault);
      for (CheckboxListener checkbox : checkboxListeners) {
        if (checkbox.getState()) {
          moduleManager.activateModuleContext(checkbox.getModuleContext());
        } else {
          moduleManager.deactivateModuleContext(checkbox.getModuleContext());
        }
      }
    }
  }
}
