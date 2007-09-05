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

import java.util.HashSet;
import java.util.Set;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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
  private final ModuleManager moduleManager;
  private final ProjectManager projectManager;
  private Set<ModuleContextRepresentation> moduleContexts;
  private Set<ModuleContextRepresentation> activeModuleContexts;
  private boolean activateByDefault;
  private FormToolkit toolkit;
  private final Shell shell;

  public EclipseModuleDialog(Shell parent, ProjectManager projectManager, ModuleManager moduleManager) {
    super(parent);
    this.shell = parent;
    this.moduleManager = moduleManager;
    this.projectManager = projectManager;
    moduleContexts = moduleManager.getModuleContexts();
    activeModuleContexts = moduleManager.getActiveModuleContexts();
    activateByDefault = moduleManager.activateModulesByDefault();
  }

  public static void display(Shell parent, ProjectManager projectManager, ModuleManager moduleManager) {
    EclipseModuleDialog dialog = new EclipseModuleDialog(parent, projectManager, moduleManager);
    dialog.create();
    dialog.getShell().setBounds(200, 200, 500, 400);
    dialog.getShell().setText("Guice Context Configuration");
    dialog.setBlockOnOpen(true);
    dialog.open();
    if (dialog.getReturnCode() == Window.OK) {
      dialog.updateSettings();
    }
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
      createUserContexts(scrolledForm.getBody());
      createPremadeContexts(scrolledForm.getBody());
      makeCheckbox(parent, activateByDefault,
          "Find and activate new modules automatically", "")
          .addSelectionListener(activateByDefaultListener);
    }
    scrolledForm.pack();
    scrolledForm.reflow(true);
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

    public static void display(Shell parent, ProjectManager projectManager, ModuleManager moduleManager) {
      NewContextDialog dialog = new NewContextDialog(parent, moduleManager);
      dialog.create();
      dialog.getShell().setBounds(200, 200, 500, 200);
      dialog.getShell().setText("Create a Guice Context");
      dialog.setBlockOnOpen(true);
      dialog.open();
      if (dialog.getReturnCode() == Window.OK) {
        dialog.saveSettings();
      }
      EclipseModuleDialog.display(parent, projectManager, moduleManager);
    }
  }

  private Set<Button> userCheckboxes;

  private void createUserContexts(Composite parent) {
    customCheckboxListeners = new HashSet<CheckboxListener>();
    Section section =
        toolkit.createSection(parent, Section.TWISTIE | Section.EXPANDED | Section.TITLE_BAR);
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
        NewContextDialog.display(shell, projectManager, moduleManager);
      }

      public void linkEntered(HyperlinkEvent e) {
      }

      public void linkExited(HyperlinkEvent e) {
      }
    });

    userCheckboxes = new HashSet<Button>();
    for (ModuleContextRepresentation moduleContext : moduleContexts) {
      if (moduleContext instanceof CustomModuleContextRepresentation) {
        CheckboxListener listener =
            new CheckboxListener(moduleContext, activeModuleContexts
                .contains(moduleContext));
        String tooltip = moduleContext.getLongName();
        Button checkbox =
            makeCheckbox(body, activeModuleContexts.contains(moduleContext),
                moduleContext.getShortName(), tooltip);
        checkbox.addSelectionListener(listener);
        userCheckboxes.add(checkbox);
        customCheckboxListeners.add(listener);
      }
    }
    body.pack();
    insideScrolledForm.pack();
    insideScrolledForm.reflow(true);
    section.setClient(insideScrolledForm);
    section.pack();
  }

  private Set<Button> checkboxes;

  private void createPremadeContexts(Composite parent) {
    checkboxListeners = new HashSet<CheckboxListener>();
    Section section =
        toolkit.createSection(parent, Section.EXPANDED | Section.TWISTIE | Section.TITLE_BAR);
    section.setText("Autogenerated Module Contexts");

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
                EclipseModuleDialog.display(shell, projectManager, moduleManager);
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

    boolean hasAutoModuleContexts = false;
    for (ModuleContextRepresentation moduleContext : moduleContexts) {
      if (!(moduleContext instanceof CustomModuleContextRepresentation)) {
        hasAutoModuleContexts = true;
        break;
      }
    }
    if (!hasAutoModuleContexts) {
      makeText(body, SWT.NONE, "No Module Contexts Available");
    } else {
      makeHyperlink(body, "Activate all", new IHyperlinkListener() {
        public void linkActivated(HyperlinkEvent e) {
          for (Button checkbox : checkboxes) {
            checkbox.setSelection(true);
          }
          for (CheckboxListener listener : checkboxListeners) {
            listener.setState(true);
          }
        }

        public void linkEntered(HyperlinkEvent e) {
        }

        public void linkExited(HyperlinkEvent e) {
        }
      });
      makeHyperlink(body, "Deactivate all", new IHyperlinkListener() {
        public void linkActivated(HyperlinkEvent e) {
          for (Button checkbox : checkboxes) {
            checkbox.setSelection(false);
          }
          for (CheckboxListener listener : checkboxListeners) {
            listener.setState(false);
          }
        }

        public void linkEntered(HyperlinkEvent e) {
        }

        public void linkExited(HyperlinkEvent e) {
        }
      });

      checkboxes = new HashSet<Button>();
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
          CheckboxListener listener =
              new CheckboxListener(moduleContext, activeModuleContexts
                  .contains(moduleContext));
          Button checkbox =
              makeCheckbox(body, activeModuleContexts.contains(moduleContext),
                  ClassNameUtility.shorten(moduleContext.getName()), text.toString());
          checkbox.addSelectionListener(listener);
          checkboxes.add(checkbox);
          checkboxListeners.add(listener);
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

  private SelectionAdapter activateByDefaultListener = new SelectionAdapter() {
    @Override
    public void widgetSelected(SelectionEvent event) {
      activateByDefault = !activateByDefault;
    }
  };

  private class CheckboxListener extends SelectionAdapter {
    private final ModuleContextRepresentation moduleContext;
    private boolean state;

    public CheckboxListener(ModuleContextRepresentation moduleContext,
        boolean state) {
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

    public void setState(boolean state) {
      this.state = state;
    }

    public boolean getState() {
      return state;
    }
  }

  private Set<CheckboxListener> checkboxListeners;
  private Set<CheckboxListener> customCheckboxListeners;

  protected void updateSettings() {
    moduleManager.setActivateModulesByDefault(activateByDefault);
    for (CheckboxListener checkbox : checkboxListeners) {
      if (checkbox.getState()) {
        moduleManager.activateModuleContext(checkbox.getModuleContext().getName());
      } else {
        moduleManager.deactivateModuleContext(checkbox.getModuleContext().getName());
      }
    }
    for (CheckboxListener checkbox : customCheckboxListeners) {
      if (checkbox.getState()) {
        moduleManager.activateModuleContext(checkbox.getModuleContext().getName());
      } else {
        moduleManager.deactivateModuleContext(checkbox.getModuleContext().getName());
      }
    }
  }
}
