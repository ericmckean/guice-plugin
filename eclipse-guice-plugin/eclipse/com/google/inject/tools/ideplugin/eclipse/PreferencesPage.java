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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.google.inject.tools.ideplugin.IDEPluginSettings;
import com.google.inject.tools.ideplugin.IDEPluginSettings.ProjectSettingsSaver;
import com.google.inject.tools.ideplugin.IDEPluginSettings.ProjectSettingsVisitor;

/**
 * The eclipse preference page dialog.
 * 
 * @author Darren Creutz (dcreutz@gmail.com)
 */
public class PreferencesPage extends PreferencePage implements IWorkbenchPreferencePage {
  public static class PreferenceDialogArea {
    private final Map<String, Button> buttons;
    private final PreferencesPage preferencesPage;
    
    public PreferenceDialogArea(PreferencesPage preferencesPage) {
      buttons = new HashMap<String, Button>();
      this.preferencesPage = preferencesPage;
    }
    
    public PreferenceDialogArea() {
      this(null);
    }
    
    public void buildComposite(Composite composite) {
      CompositeBuilder builder = new CompositeBuilder(composite);
      new IDEPluginSettings().accept(builder);
      initializeValues();
    }
    
    class CompositeBuilder implements ProjectSettingsVisitor {
      private final Composite composite;
      
      public CompositeBuilder(Composite composite) {
        this.composite = composite;
      }
      
      public void visit(String name, boolean value) {
        buttons.put(name, createCheckBox(composite, name));
      }
      
      private Button createCheckBox(Composite group, String label) {
        Button button = new Button(group, SWT.CHECK | SWT.LEFT);
        button.setText(label);
        GridData data = new GridData();
        button.setLayoutData(data);
        return button;
      }
      
      public Composite composite() {
        return composite;
      }
    }
    
    class ValuesSetter implements ProjectSettingsVisitor {
      public void visit(String name, boolean value) {
        buttons.get(name).setSelection(value);
      }
    }
    
    public void saveSettings() {
      storeValues();
      Activator.getDefault().savePluginPreferences();
    }
    
    class ValuesSaver implements ProjectSettingsSaver {
      public boolean getBoolean(String name) {
        return buttons.get(name).getSelection();
      }
    }
    
    private void setValues(String serialized) {
      IDEPluginSettings settings = new IDEPluginSettings(serialized);
      settings.accept(new ValuesSetter());
    }
    
    private void storeValues() {
      IPreferenceStore store = getPreferenceStore();
      IDEPluginSettings settings = new IDEPluginSettings(new ValuesSaver());
      store.setValue("com.google.inject.tools.ideplugin.eclipse.preferences", settings.serialize());
      Activator.getGuicePlugin().getProjectManager().settingsChanged(null, settings);
    }
    
    private void initializeDefaults() {
      IPreferenceStore store = getPreferenceStore();
      setValues(store.getDefaultString("com.google.inject.tools.ideplugin.eclipse.preferences"));
    }
    
    private void initializeValues() {
      IPreferenceStore store = getPreferenceStore();
      setValues(store.getString("com.google.inject.tools.ideplugin.eclipse.preferences"));
    }
    
    private IPreferenceStore getPreferenceStore() {
      if (preferencesPage != null) {
        return preferencesPage.getPreferenceStore();
      } else {
        return Activator.getDefault().getPreferenceStore();
      }
    }
  }
  
  private final PreferenceDialogArea dialogArea;
  
  public PreferencesPage() {
    dialogArea = new PreferenceDialogArea(this);
  }

  public void init(IWorkbench workbench) {
    // do nothing
  }
  
  private Label createLabel(Composite parent, String text) {
    Label label = new Label(parent, SWT.LEFT);
    label.setText(text);
    GridData data = new GridData();
    data.horizontalSpan = 1;
    data.horizontalAlignment = GridData.FILL;
    label.setLayoutData(data);
    return label;
  }
  
  private Composite createComposite(Composite parent, int numColumns) {
    Composite composite = new Composite(parent, SWT.NULL);
    
    //GridLayout
    GridLayout layout = new GridLayout();
    layout.numColumns = numColumns;
    composite.setLayout(layout);
    
    //GridData
    GridData data = new GridData();
    data.verticalAlignment = GridData.FILL;
    data.horizontalAlignment = GridData.FILL;
    composite.setLayoutData(data);
    return composite;
  }
  
  @Override
  protected Control createContents(Composite parent) {
    Composite composite = createComposite(parent, 1);
    createLabel(composite, "Guice Plugin Global Options");
    dialogArea.buildComposite(composite);
    return new Composite(parent, SWT.NULL);
  }

  @Override
  protected IPreferenceStore doGetPreferenceStore() {
    return Activator.getDefault().getPreferenceStore();
  }
  
  @Override
  protected void performDefaults() {
    super.performDefaults();
    dialogArea.initializeDefaults();
  }
  
  @Override
  public boolean performOk() {
    dialogArea.saveSettings();
    return true;
  }
}
