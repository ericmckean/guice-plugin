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
import java.util.Set;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.FormDialog;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

import com.google.inject.tools.ideplugin.module.ModuleSelectionView;
import com.google.inject.tools.module.ModuleContextRepresentation;
import com.google.inject.tools.module.ModuleManager;
import com.google.inject.tools.module.ModuleContextRepresentation.ModuleInstanceRepresentation;

/**
 * Eclipse implementation of the {@link ModuleSelectionView}.
 * 
 * @author Darren Creutz <dcreutz@gmail.com>
 */
public class EclipseModuleDialog extends FormDialog {	
  private final ModuleManager moduleManager;
  private Set<ModuleContextRepresentation> moduleContexts;
  private Set<ModuleContextRepresentation> activeModuleContexts;
  private boolean activateByDefault;
  private FormToolkit toolkit;
  
  public EclipseModuleDialog(Shell parent, ModuleManager moduleManager) {
    super(parent);
    parent.setText("Module Context Configuration");
    this.moduleManager = moduleManager;
    this.moduleContexts = moduleManager.getModuleContexts();
    this.activeModuleContexts = moduleManager.getActiveModuleContexts();
    this.activateByDefault = moduleManager.activateModulesByDefault();
  }
  
  public static void display(Shell parent, ModuleManager moduleManager) {
    EclipseModuleDialog dialog = new EclipseModuleDialog(parent, moduleManager);
    dialog.create();
    dialog.setBlockOnOpen(true);
    dialog.open();
    if (dialog.getReturnCode() == Window.OK && moduleManager.getActiveModuleContexts() != null && moduleManager.getActiveModuleContexts().size() > 0) {
      dialog.updateSettings();
    }
  }
  
  @Override
  protected void createFormContent(IManagedForm form) {
    ScrolledForm scrolledForm = form.getForm();
    toolkit = form.getToolkit();
    if (moduleContexts == null) {
      toolkit.createLabel(scrolledForm.getBody(), "No project selected.");
    } else if (moduleContexts.size() == 0) {
      toolkit.createLabel(scrolledForm.getBody(), "No Module Contexts Available");
    } else {
      myStuff(scrolledForm);
    }
    makeCheckbox(scrolledForm.getBody(), activateByDefault, "Activate new modules by default")
      .addSelectionListener(activateByDefaultListener);
    scrolledForm.pack();
    scrolledForm.reflow(true);
  }
  
  private void myStuff(ScrolledForm form) {
    checkboxListeners = new HashSet<CheckboxListener>();
    form.getBody().setLayout(new GridLayout());
    
    Section section = toolkit.createSection(form.getBody(), Section.EXPANDED | Section.TITLE_BAR);
    section.setText("Module Context Configuration");
    Composite body = toolkit.createComposite(section);
    
    GridLayout layout = new GridLayout();
    layout.numColumns = 3;
    body.setLayout(layout);
    
    for (ModuleContextRepresentation moduleContext : moduleContexts) {
      CheckboxListener listener = 
        new CheckboxListener(moduleContext, activeModuleContexts.contains(moduleContext));
      makeCheckbox(body, activeModuleContexts.contains(moduleContext), moduleContext.getName())
      .addSelectionListener(listener);
      makeText(body, SWT.NONE, "     ");
      checkboxListeners.add(listener);
      StringBuilder text = new StringBuilder();
      text.append("Guice.createInjector(");
      int count=0;
      for (ModuleInstanceRepresentation module : moduleContext.getModules()) {
        text.append(module.getCreationString());
        count++;
        if (count < moduleContext.getModules().size()) text.append(", ");
      }
      text.append(");");
      makeText(body, SWT.NONE, text.toString());
    }
    
    makeText(body, SWT.NONE, "");
    makeText(body, SWT.NONE, "");
    makeText(body, SWT.NONE, "");
    
    section.setClient(body);
    body.pack();
  }
  
  private Label makeText(Composite parent, int style, String text) {
    return toolkit.createLabel(parent, text, style);
  }
  
  private Button makeCheckbox(Composite parent, boolean selected, String text) {
    Button button = toolkit.createButton(parent, text, SWT.CHECK);
    button.setSelection(selected);
    return button;
  }
  
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
  
  protected void updateSettings() {
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
