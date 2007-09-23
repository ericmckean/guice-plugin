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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ElementChangedEvent;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IElementChangedListener;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaElementDelta;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.ITypeHierarchyChangedListener;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

import com.google.inject.Singleton;
import com.google.inject.Inject;
import com.google.inject.tools.ideplugin.JavaProject;
import com.google.inject.tools.ideplugin.AbstractSourceImpl;
import com.google.inject.tools.suite.Messenger;
import com.google.inject.tools.suite.ProgressHandler.ProgressMonitor;

/**
 * Eclipse implementation of the {@link com.google.inject.tools.ideplugin.Source}.
 * 
 * {@inheritDoc com.google.inject.tools.ideplugin.Source}
 * 
 * @author Darren Creutz (dcreutz@gmail.com)
 */
@Singleton
abstract class EclipseSourceImpl extends AbstractSourceImpl {
  private final Map<EclipseJavaProject, ITypeHierarchy> typeHierarchies;
  private final Map<EclipseJavaProject, Set<String>> cachedHierarchies;
  private final Map<EclipseJavaProject, IType> types;
  private final Map<EclipseJavaProject, ITypeHierarchyChangedListener> typeListeners;
  private boolean listenForChanges;
  private ElementChangedListener changeListener;
  
  @Inject
  public EclipseSourceImpl(Messenger messenger) {
    super(messenger);
    typeHierarchies = new HashMap<EclipseJavaProject, ITypeHierarchy>();
    cachedHierarchies = new HashMap<EclipseJavaProject, Set<String>>();
    types = new HashMap<EclipseJavaProject, IType>();
    typeListeners = new HashMap<EclipseJavaProject, ITypeHierarchyChangedListener>();
  }
  
  public void listenForChanges(boolean listenForChanges) {
    if (this.listenForChanges && !listenForChanges) {
      stopListeningForChanges();
    }
    if (listenForChanges && !this.listenForChanges) {
      startListeningForChanges();
    }
    this.listenForChanges = listenForChanges;
  }
  
  public boolean isListeningForChanges() {
    return listenForChanges;
  }
  
  private void startListeningForChanges() {
    changeListener = new ElementChangedListener();
    for (JavaProject project : projects) {
      createTypeHierarchy(project);
    }
    JavaCore.addElementChangedListener(changeListener, ElementChangedEvent.POST_CHANGE);
  }
  
  private void stopListeningForChanges() {
    JavaCore.removeElementChangedListener(changeListener);
    for (JavaProject project : projects) {
      if (typeHierarchies.get(project) != null) {
        typeHierarchies.get(project).removeTypeHierarchyChangedListener(
            typeListeners.get(project));
      }
      cachedHierarchies.remove(project);
      typeListeners.remove(project);
      typeHierarchies.remove(project);
      types.remove(project);
    }
    changeListener = null;
  }
  
  @Override
  protected void initialize(JavaProject javaManager) {
    super.initialize(javaManager);
    if (listenForChanges) {
      createTypeHierarchy(javaManager);
    }
  }
  
  protected abstract String getTypeName();
  
  protected abstract boolean isTypeWeCareAbout(IType type) throws JavaModelException;
  
  protected void createTypeHierarchy(JavaProject project) {
    try {
      EclipseJavaProject javaManager = (EclipseJavaProject)project;
      IType type =
        javaManager.getIJavaProject().findType(getTypeName());
      if (type != null && type.getJavaProject().equals(javaManager.getIJavaProject())) {
        types.put(javaManager, type);
        typeHierarchies.put(javaManager, types.get(javaManager)
            .newTypeHierarchy(null));
        if (typeHierarchies.get(javaManager) != null) {
          cachedHierarchies.put(javaManager, readHierarchy(javaManager));
          typeListeners.put(javaManager, new MyTypeHierarchyChangedListener(javaManager));
          typeHierarchies.get(javaManager).addTypeHierarchyChangedListener(
              typeListeners.get(javaManager));
        } else {
          cachedHierarchies.put(javaManager, new HashSet<String>());
        }
      }
    } catch (Throwable throwable) {
      hadProblem(throwable);
    }
  }
  
  private Set<String> readHierarchy(EclipseJavaProject project) {
    Set<String> types = new HashSet<String>();
    for (IType type : typeHierarchies.get(project).getAllSubtypes(this.types.get(project))) {
      try {
        if (isTypeWeCareAbout(type)) {
          types.add(type.getFullyQualifiedName());
        }
      } catch (JavaModelException e) {}
    }
    return types;
  }
  
  class MyTypeHierarchyChangedListener implements ITypeHierarchyChangedListener {
    private final EclipseJavaProject project;
    public MyTypeHierarchyChangedListener(EclipseJavaProject project) {
      this.project = project;
    }
    public void typeHierarchyChanged(ITypeHierarchy hierarchy) {
      typeHierarchies.put(project, hierarchy);
      Set<String> oldTypes = cachedHierarchies.get(project);
      Set<String> newTypes = readHierarchy(project);
      for (String oldType : oldTypes) {
        if (!newTypes.contains(oldType)) {
          EclipseSourceImpl.this.removed(project, oldType);
        }
      }
      for (String newType : newTypes) {
        if (!oldTypes.contains(newType)) {
          EclipseSourceImpl.this.added(project, newType);
        }
      }
      cachedHierarchies.put(project, newTypes);
    }
  }
  
  static class NotEclipseJavaProjectException extends RuntimeException {
    private static final long serialVersionUID = 5868250037957080902L;
    private final JavaProject javaManager;
    
    public NotEclipseJavaProjectException(JavaProject javaManager) {
      this.javaManager = javaManager;
    }
    
    @Override
    public String toString() {
      return "Not an Eclipse Java Project: " + javaManager.toString();
    }
  }
  
  @Override
  protected Set<String> locate(JavaProject javaManager,
      ProgressMonitor monitor) throws Throwable {
    if (javaManager instanceof EclipseJavaProject) {
      return locate((EclipseJavaProject) javaManager, monitor);
    } else {
      throw new NotEclipseJavaProjectException(javaManager);
    }
  }
  
  protected Set<String> locate(EclipseJavaProject javaManager, ProgressMonitor monitor)
      throws Throwable {
    monitor.begin("Building Guice Type Hierarchy", 2);
    IType type = javaManager.getIJavaProject().findType(getTypeName());
    IProgressMonitor eclipsemonitor = 
        ((EclipseProgressHandler.EclipseProgressMonitor)monitor).getSubIProgressMonitor(1);
    ITypeHierarchy hierarchy = type.newTypeHierarchy(eclipsemonitor);
    final Set<String> names = new HashSet<String>();
    IType[] subclasses = hierarchy.getAllSubtypes(type);
    ProgressMonitor secondmonitor = monitor.getSubMonitor(1);
    secondmonitor.begin("Analyzing Guice Type Hierarchy", subclasses.length);
    for (IType subclass : subclasses) {
      try {
        if (subclass.isClass()) {
          if (!Flags.isAbstract(subclass.getFlags())) {
            if (isTypeWeCareAbout(subclass)) {
              names.add(subclass.getFullyQualifiedName());
            }
          }
        }
      } catch (Throwable t) {
        hadProblem(t);
      }
      secondmonitor.worked(1);
    }
    secondmonitor.done();
    monitor.done();
    return names;
  }
  
  protected class ElementChangedListener implements
      IElementChangedListener {
    public void elementChanged(ElementChangedEvent event) {
      try {
        handleDelta(event.getDelta());
      } catch (JavaModelException e) {
        hadProblem(e);
      }
    }
    
    private void handleDelta(IJavaElementDelta delta) throws JavaModelException {
      IJavaElement element = delta.getElement();
      if (element instanceof ICompilationUnit) {
        IJavaProject project = element.getJavaProject();
        EclipseJavaProject javaManager = new EclipseJavaProject(project);
        ICompilationUnit cu = (ICompilationUnit) element;
        for (IType type : cu.getAllTypes()) {
          if (typeHierarchies.get(javaManager) != null
              && typeHierarchies.get(javaManager).contains(type)
              && EclipseSourceImpl.this.isTypeWeCareAbout(type)) {
            switch (delta.getKind()) {
              case IJavaElementDelta.ADDED:
                EclipseSourceImpl.this.added(javaManager, type
                    .getFullyQualifiedName());
                break;
              case IJavaElementDelta.CHANGED:
                EclipseSourceImpl.this.changed(javaManager, type
                    .getFullyQualifiedName());
                break;
              case IJavaElementDelta.REMOVED:
                EclipseSourceImpl.this.removed(javaManager, type
                    .getFullyQualifiedName());
                break;
              default:
                // do nothing
            }
          }
        }
      }
      for (IJavaElementDelta child : delta.getAffectedChildren()) {
        handleDelta(child);
      }
    }
  }
}
