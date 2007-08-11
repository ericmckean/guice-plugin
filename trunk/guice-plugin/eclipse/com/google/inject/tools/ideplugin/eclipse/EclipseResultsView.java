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

import java.util.ArrayList;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.*;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.*;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.SWT;
import org.eclipse.core.runtime.IAdaptable;
import com.google.inject.tools.ideplugin.results.Results;
import com.google.inject.tools.ideplugin.results.ResultsView;
import com.google.inject.tools.ideplugin.ActionsHandler;

/**
 * The Eclipse implementation of the {@link ResultsView}, a view for displaying results and error 
 * messages (a view is a tab in the lower panel).
 * 
 * @author Darren Creutz <dcreutz@gmail.com>
 */
public class EclipseResultsView extends ViewPart implements ResultsView {
  private TreeViewer viewer;
  private DrillDownAdapter drillDownAdapter;
  private Action action1;
  private Action action2;
  private Action doubleClickAction;
  private ViewContentProvider viewContentProvider;
  private Results results;
  
  private class TreeObject implements IAdaptable {
    private final String name;
    private final ActionsHandler.Action action;
    private TreeParent parent;
    
    public TreeObject(String name,ActionsHandler.Action action) {
      this.name = name;
      this.action = action;
    }
    public String getName() {
      return name;
    }
    public void setParent(TreeParent parent) {
      this.parent = parent;
    }
    public TreeParent getParent() {
      return parent;
    }
    public ActionsHandler.Action getAction() {
      return action;
    }
    @Override
    public String toString() {
      return getName();
    }
    @SuppressWarnings("unchecked")
    public Object getAdapter(Class key) {
      return null;
    }
  }
  
  private class TreeParent extends TreeObject {
    private ArrayList<TreeObject> children;
    public TreeParent(String name,ActionsHandler.Action action) {
      super(name,action);
      children = new ArrayList<TreeObject>();
    }
    public void addChild(TreeObject child) {
      children.add(child);
      child.setParent(this);
    }
    public void removeChild(TreeObject child) {
      children.remove(child);
      child.setParent(null);
    }
    public TreeObject [] getChildren() {
      return children.toArray(new TreeObject[children.size()]);
    }
    public boolean hasChildren() {
      return children.size()>0;
    }
  }
  
  private class ViewContentProvider implements IStructuredContentProvider, 
  ITreeContentProvider {
    private TreeParent invisibleRoot;
    
    public void inputChanged(Viewer v, Object oldInput, Object newInput) {
    }
    public void dispose() {
    }
    public Object[] getElements(Object parent) {
      if (parent.equals(getViewSite())) {
        if (invisibleRoot==null) initialize();
        return getChildren(invisibleRoot);
      }
      return getChildren(parent);
    }
    public Object getParent(Object child) {
      if (child instanceof TreeObject) {
        return ((TreeObject)child).getParent();
      }
      return null;
    }
    public Object [] getChildren(Object parent) {
      if (parent instanceof TreeParent) {
        return ((TreeParent)parent).getChildren();
      }
      return new Object[0];
    }
    public boolean hasChildren(Object parent) {
      if (parent instanceof TreeParent)
        return ((TreeParent)parent).hasChildren();
      return false;
    }
    
    public void useResults(Results results) {
      EclipseResultsView.this.results = results;
      for (TreeObject child : invisibleRoot.getChildren()) {
        invisibleRoot.removeChild(child);
      }
      invisibleRoot.addChild(makeTree(results.getRoot()));
    }
    
    private void initialize() {
      invisibleRoot = new TreeParent("",new ActionsHandler.NullAction());
      if (results != null) {
        invisibleRoot.addChild(makeTree(results.getRoot()));
      }
    }
    
    private TreeObject makeTree(Results.Node node) {
      //TODO: actions!
      if (node.children().isEmpty()) {
        return new TreeObject(node.getTextString(),null);
      } else {
        TreeParent parent = new TreeParent(node.getTextString(),null);
        for (Results.Node child : node.children()) {
          parent.addChild(makeTree(child));
        }
        return parent;
      }
    }
  }
  private class ViewLabelProvider extends LabelProvider {
    @Override
    public String getText(Object obj) {
      return obj.toString();
    }
    @Override
    public Image getImage(Object obj) {
      String imageKey = ISharedImages.IMG_OBJ_ELEMENT;
      if (obj instanceof TreeParent)
        imageKey = ISharedImages.IMG_OBJ_FOLDER;
      return PlatformUI.getWorkbench().getSharedImages().getImage(imageKey);
    }
  }
  private class NameSorter extends ViewerSorter {
  }
  
  /**
   * The constructor.  This will be called by Eclipse internally.
   */
  public EclipseResultsView() {
  }
  
  //TODO: actions
  private ActionsHandler getActionsHandler() {
    return Activator.getGuicePlugin()!=null ? Activator.getGuicePlugin().getActionsHandler() : null;
  }
  
  /**
   * This is a callback that will allow us
   * to create the viewer and initialize it.
   */
  @Override
  public void createPartControl(Composite parent) {
    viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
    drillDownAdapter = new DrillDownAdapter(viewer);
    viewContentProvider = new ViewContentProvider();
    viewer.setContentProvider(viewContentProvider);
    viewer.setLabelProvider(new ViewLabelProvider());
    viewer.setSorter(new NameSorter());
    viewer.setInput(getViewSite());
    makeActions();
    hookContextMenu();
    hookDoubleClickAction();
    contributeToActionBars();
  }
  
  private void hookContextMenu() {
    MenuManager menuMgr = new MenuManager("#PopupMenu");
    menuMgr.setRemoveAllWhenShown(true);
    menuMgr.addMenuListener(new IMenuListener() {
      public void menuAboutToShow(IMenuManager manager) {
        EclipseResultsView.this.fillContextMenu(manager);
      }
    });
    Menu menu = menuMgr.createContextMenu(viewer.getControl());
    viewer.getControl().setMenu(menu);
    getSite().registerContextMenu(menuMgr, viewer);
  }
  
  private void contributeToActionBars() {
    IActionBars bars = getViewSite().getActionBars();
    fillLocalPullDown(bars.getMenuManager());
    fillLocalToolBar(bars.getToolBarManager());
  }
  
  private void fillLocalPullDown(IMenuManager manager) {
    manager.add(action1);
    manager.add(new Separator());
    manager.add(action2);
  }
  
  private void fillContextMenu(IMenuManager manager) {
    manager.add(action1);
    manager.add(action2);
    manager.add(new Separator());
    drillDownAdapter.addNavigationActions(manager);
    // Other plug-ins can contribute there actions here
    manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
  }
  
  private void fillLocalToolBar(IToolBarManager manager) {
    manager.add(action1);
    manager.add(action2);
    manager.add(new Separator());
    drillDownAdapter.addNavigationActions(manager);
  }
  
  private void makeActions() {
    action1 = new Action() {
      @Override
      public void run() {
        showMessage("Action 1 executed");
      }
    };
    action1.setText("Action 1");
    action1.setToolTipText("Action 1 tooltip");
    action1.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
        getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
    
    action2 = new Action() {
      @Override
      public void run() {
        showMessage("Action 2 executed");
      }
    };
    action2.setText("Action 2");
    action2.setToolTipText("Action 2 tooltip");
    action2.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
        getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
    doubleClickAction = new Action() {
      @Override
      public void run() {
        ISelection selection = viewer.getSelection();
        Object obj = ((IStructuredSelection)selection).getFirstElement();
        showMessage("Double-click detected on "+obj.toString());
      }
    };
  }
  
  private void hookDoubleClickAction() {
    viewer.addDoubleClickListener(new IDoubleClickListener() {
      public void doubleClick(DoubleClickEvent event) {
        doubleClickAction.run();
      }
    });
  }
  private void showMessage(String message) {
    MessageDialog.openInformation(
        viewer.getControl().getShell(),
        "Guice View",
        message);
  }
  
  /**
   * (non-Javadoc)
   * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
   */
  @Override
  public void setFocus() {
    viewer.getControl().setFocus();
  }
  
  /**
   * 
   */
  public void displayResults(Results results) {
    this.results = results;
    viewContentProvider.useResults(this.results);
    viewer.refresh();
    viewer.expandAll();
    try {
      this.getViewSite().getWorkbenchWindow().getActivePage().showView("com.google.inject.tools.ideplugin.eclipse.EclipseResultsView");
    } catch (Exception e) {
      showMessage(e.toString());
    }
  }
}