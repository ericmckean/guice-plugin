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
import java.util.Set;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.FormDialog;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

import com.google.inject.tools.ideplugin.ProjectManager;
import com.google.inject.tools.ideplugin.eclipse.PreferencesPage.PreferenceDialogArea;
import com.google.inject.tools.suite.Messenger;
import com.google.inject.tools.suite.module.ApplicationModuleContextRepresentation;
import com.google.inject.tools.suite.module.ClassNameUtility;
import com.google.inject.tools.suite.module.CustomModuleContextRepresentation;
import com.google.inject.tools.suite.module.ModuleContextRepresentation;
import com.google.inject.tools.suite.module.ModuleManager;
import com.google.inject.tools.suite.module.ModuleContextRepresentation.ModuleInstanceRepresentation;

/**
 * Eclipse dialog allowing selection of module contexts.
 * 
 * @author Darren Creutz (dcreutz@gmail.com)
 */
class EclipseModuleDialog extends FormDialog {
  private final Messenger messenger;
  private final ModuleManager moduleManager;
  private final ProjectManager projectManager;
  private Set<ModuleContextRepresentation> moduleContexts;
  private Set<ModuleContextRepresentation> activeModuleContexts;
  private FormToolkit toolkit;
  private final Shell shell;
  private final PreferenceDialogArea preferencesPage;
  private final Map<String, Button> checkboxes;

  public EclipseModuleDialog(Shell parent, Messenger messenger, ProjectManager projectManager, ModuleManager moduleManager) {
    super(parent);
    this.shell = parent;
    this.messenger = messenger;
    this.moduleManager = moduleManager;
    this.projectManager = projectManager;
    moduleContexts = moduleManager.getModuleContexts();
    activeModuleContexts = moduleManager.getActiveModuleContexts();
    preferencesPage = new PreferenceDialogArea();
    checkboxes = new HashMap<String, Button>();
  }

  public static boolean display(Shell parent, Messenger messenger, ProjectManager projectManager, ModuleManager moduleManager) {
    EclipseModuleDialog dialog = new EclipseModuleDialog(parent, messenger, projectManager, moduleManager);
    dialog.create();
    dialog.getShell().setBounds(200, 200, 500, 400);
    dialog.getShell().setText("Guice Context Configuration");
    dialog.setBlockOnOpen(true);
    dialog.open();
    return dialog.getReturnCode() == Window.OK;
  }

  @Override
  protected void createFormContent(IManagedForm form) {
    ScrolledForm scrolledForm = form.getForm();
    toolkit = form.getToolkit();
    Composite parent = scrolledForm.getBody();
    parent.setLayout(new GridLayout());
    if (moduleContexts == null) {
      toolkit.createLabel(parent, "No project selected.");
    } else {
      createScanContent(scrolledForm.getBody());
      createUserContexts(scrolledForm.getBody());
      createPremadeContexts(scrolledForm.getBody());
      createOptionsContent(scrolledForm.getBody());
    }
    scrolledForm.pack();
    scrolledForm.reflow(true);
  }
  
  private void createScanContent(Composite parent) {
    Section section =
      toolkit.createSection(parent, Section.EXPANDED | Section.TITLE_BAR);
    section.setText("Find New Contexts");
    
    ScrolledForm insideScrolledForm = toolkit.createScrolledForm(section);
    insideScrolledForm.setExpandHorizontal(true);
    insideScrolledForm.setExpandVertical(true);
    Composite body = insideScrolledForm.getBody();
    
    GridLayout layout = new GridLayout();
    layout.numColumns = 1;
    body.setLayout(layout);
    
    makeHyperlink(body, "Scan for new contexts", new IHyperlinkListener() {
      public void linkActivated(HyperlinkEvent e) {
        EclipseModuleDialog.this.close();
        projectManager.findNewContexts(projectManager.getJavaManager(moduleManager),
            new ModuleManager.PostUpdater() {
          public void execute(boolean success) {
            Display.getDefault().asyncExec(new Runnable() {
              public void run() {
                EclipseModuleDialog.display(shell, messenger, projectManager, moduleManager);
              }
            });
          }
        }, false);
      }
      
      public void linkEntered(HyperlinkEvent e) {
      }
      
      public void linkExited(HyperlinkEvent e) {
      }
    });
    
    body.pack();
    insideScrolledForm.pack();
    insideScrolledForm.reflow(true);
    section.setClient(insideScrolledForm);
    section.pack();
  }
  
  private void createOptionsContent(Composite parent) {
    Section section =
      toolkit.createSection(parent, Section.EXPANDED | Section.TITLE_BAR);
    section.setText("Global Options");
    
    ScrolledForm insideScrolledForm = toolkit.createScrolledForm(section);
    insideScrolledForm.setExpandHorizontal(true);
    insideScrolledForm.setExpandVertical(true);
    Composite body = insideScrolledForm.getBody();
    
    GridLayout layout = new GridLayout();
    layout.numColumns = 1;
    body.setLayout(layout);
    
    preferencesPage.buildComposite(body);
    
    body.pack();
    insideScrolledForm.pack();
    insideScrolledForm.reflow(true);
    section.setClient(insideScrolledForm);
    section.pack();
  }

  private static class NewContextDialog extends FormDialog {
    private final ModuleManager moduleManager;
    private Text classNameText = null;
    private Text methodNameText = null;
    private Text titleText = null;
    private String classNameTextValue;
    private String methodNameTextValue;
    private String titleTextValue;

    public NewContextDialog(Shell parent, ModuleManager moduleManager) {
      super(parent);
      this.moduleManager = moduleManager;
    }

    @Override
    public void createFormContent(IManagedForm form) {
      FormToolkit toolkit = form.getToolkit();
      ScrolledForm scrolledForm = form.getForm();
      Composite body = scrolledForm.getBody();
      GridLayout layout = new GridLayout();
      layout.numColumns = 2;
      body.setLayout(layout);
      toolkit.createLabel(body, "Context Name");
      titleText = toolkit.createText(body, "", SWT.BORDER);
      titleText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1,
          1));
      titleText.addModifyListener(new ModifyListener() {
        public void modifyText(ModifyEvent e) {
          titleTextValue = titleText.getText();
        }
      });
      toolkit.createLabel(body, "Fully Qualified Class Name");
      classNameText = toolkit.createText(body, "", SWT.BORDER);
      classNameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
          true, 1, 1));
      classNameText.addModifyListener(new ModifyListener() {
        public void modifyText(ModifyEvent e) {
          classNameTextValue = classNameText.getText();
        }
      });
      toolkit.createLabel(body, "Method Name");
      methodNameText = toolkit.createText(body, "", SWT.BORDER);
      methodNameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
          true, 1, 1));
      methodNameText.addModifyListener(new ModifyListener() {
        public void modifyText(ModifyEvent e) {
          methodNameTextValue = methodNameText.getText();
        }
      });
    }

    public void saveSettings() {
      String title = titleTextValue;
      String classToUse = classNameTextValue;
      String methodToUse = methodNameTextValue;
      moduleManager.addCustomContext(title, classToUse, methodToUse);
    }

    public static void display(Shell parent, Messenger messenger, ProjectManager projectManager, ModuleManager moduleManager) {
      NewContextDialog dialog = new NewContextDialog(parent, moduleManager);
      dialog.create();
      dialog.getShell().setBounds(200, 200, 500, 200);
      dialog.getShell().setText("Create a Guice Context");
      dialog.setBlockOnOpen(true);
      dialog.open();
      if (dialog.getReturnCode() == Window.OK) {
        dialog.saveSettings();
      }
      EclipseModuleDialog.display(parent, messenger, projectManager, moduleManager);
    }
  }

  private void createUserContexts(Composite parent) {
    Section section =
        toolkit.createSection(parent, Section.EXPANDED | Section.TITLE_BAR);
    section.setText("Your Module Contexts");

    ScrolledForm insideScrolledForm = toolkit.createScrolledForm(section);
    insideScrolledForm.setExpandHorizontal(true);
    insideScrolledForm.setExpandVertical(true);
    Composite body = insideScrolledForm.getBody();

    GridLayout layout = new GridLayout();
    layout.numColumns = 1;
    body.setLayout(layout);

    makeHyperlink(body, "Create new context", new IHyperlinkListener() {
      public void linkActivated(HyperlinkEvent e) {
        EclipseModuleDialog.this.close();
        NewContextDialog.display(shell, messenger, projectManager, moduleManager);
      }

      public void linkEntered(HyperlinkEvent e) {
      }

      public void linkExited(HyperlinkEvent e) {
      }
    });

    for (ModuleContextRepresentation moduleContext : moduleContexts) {
      if (moduleContext instanceof CustomModuleContextRepresentation 
          || moduleContext instanceof ApplicationModuleContextRepresentation) {
        String tooltip = moduleContext.getLongName();
        Button checkbox =
            makeCheckbox(body, activeModuleContexts.contains(moduleContext),
                moduleContext.getShortName(), tooltip);
        checkboxes.put(moduleContext.getName(), checkbox);
      }
    }
    body.pack();
    insideScrolledForm.pack();
    insideScrolledForm.reflow(true);
    section.setClient(insideScrolledForm);
    section.pack();
  }

  private void createPremadeContexts(Composite parent) {
    Section section =
        toolkit.createSection(parent, Section.EXPANDED | Section.TITLE_BAR);
    section.setText("Autogenerated Module Contexts");

    ScrolledForm insideScrolledForm = toolkit.createScrolledForm(section);
    insideScrolledForm.setExpandHorizontal(true);
    insideScrolledForm.setExpandVertical(true);
    Composite body = insideScrolledForm.getBody();

    GridLayout layout = new GridLayout();
    layout.numColumns = 1;
    body.setLayout(layout);

    boolean hasAutoModuleContexts = false;
    for (ModuleContextRepresentation moduleContext : moduleContexts) {
      if (!(moduleContext instanceof CustomModuleContextRepresentation ||
          moduleContext instanceof ApplicationModuleContextRepresentation)) {
        hasAutoModuleContexts = true;
        break;
      }
    }
    if (!hasAutoModuleContexts) {
      makeText(body, SWT.NONE, "No Module Contexts Available");
    } else {
      makeHyperlink(body, "Activate all", new IHyperlinkListener() {
        public void linkActivated(HyperlinkEvent e) {
          for (Button checkbox : checkboxes.values()) {
            checkbox.setSelection(true);
          }
        }

        public void linkEntered(HyperlinkEvent e) {
        }

        public void linkExited(HyperlinkEvent e) {
        }
      });
      makeHyperlink(body, "Deactivate all", new IHyperlinkListener() {
        public void linkActivated(HyperlinkEvent e) {
          for (Button checkbox : checkboxes.values()) {
            checkbox.setSelection(false);
          }
        }

        public void linkEntered(HyperlinkEvent e) {
        }

        public void linkExited(HyperlinkEvent e) {
        }
      });

      for (ModuleContextRepresentation moduleContext : moduleContexts) {
        if (!(moduleContext instanceof CustomModuleContextRepresentation)) {
          StringBuilder text = new StringBuilder();
          text.append("Guice.createInjector(");
          int count = 0;
          for (ModuleInstanceRepresentation module : moduleContext.getModules()) {
            text.append(module.getCreationString());
            count++;
            if (count < moduleContext.getModules().size()) {
              text.append(", ");
            }
          }
          text.append(");");
          Button checkbox =
              makeCheckbox(body, activeModuleContexts.contains(moduleContext),
                  ClassNameUtility.shorten(moduleContext.getName()), text.toString());
          checkboxes.put(moduleContext.getName(), checkbox);
        }
      }
    }
    section.setClient(insideScrolledForm);
    section.pack();
  }

  private Label makeText(Composite parent, int style, String text) {
    return toolkit.createLabel(parent, text, style);
  }

  private Button makeCheckbox(Composite parent, boolean selected, String text,
      String tooltip) {
    Button button = toolkit.createButton(parent, text, SWT.CHECK);
    button.setSelection(selected);
    button.setToolTipText(tooltip);
    return button;
  }

  private Hyperlink makeHyperlink(Composite parent, String text,
      IHyperlinkListener listener) {
    Hyperlink link = toolkit.createHyperlink(parent, text, SWT.NONE);
    link.addHyperlinkListener(listener);
    return link;
  }

  @Override
  protected void okPressed() {
    try {
      preferencesPage.saveSettings();
      if (checkboxes.size() > 0) {
        for (String context : checkboxes.keySet()) {
          if (checkboxes.get(context).getSelection()) {
            moduleManager.activateModuleContext(context);
          } else {
            moduleManager.deactivateModuleContext(context);
          }
        }
      }
    } catch (Throwable t) {
      messenger.logException("Module Dialog Exception", t);
    }
    super.okPressed();
  }
}
