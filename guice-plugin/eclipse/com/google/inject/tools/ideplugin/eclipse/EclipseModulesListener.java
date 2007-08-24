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

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
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
import com.google.inject.Singleton;
import com.google.inject.Inject;
import com.google.inject.tools.JavaManager;
import com.google.inject.tools.Messenger;
import com.google.inject.tools.ideplugin.ProjectManager;
import com.google.inject.tools.ideplugin.module.ModulesListener;
import com.google.inject.tools.module.ModulesSource;

/**
 * Eclipse implementation of the {@link ModulesSource}. anInterface
 * 
 * @author Darren Creutz <dcreutz@gmail.com>
 */
@Singleton
public class EclipseModulesListener extends ModulesListener {
  private final Map<EclipseJavaProject, ITypeHierarchy> typeHierarchies;
  private final Map<EclipseJavaProject, MyTypeHierarchyChangedListener> typeHierarchyListeners;
  private final Map<EclipseJavaProject, IType> types;
  private final Map<EclipseJavaProject, ITypeHierarchy> contextTypeHierarchies;
  private final Map<EclipseJavaProject, MyContextTypeHierarchyChangedListener> contextTypeHierarchyListeners;
  private final Map<EclipseJavaProject, IType> contextTypes;

  /**
   * Create an EclipseModulesListener. This should be injected.
   */
  @Inject
  public EclipseModulesListener(ProjectManager projectManager,
      Messenger messenger) {
    super(projectManager, messenger);
    typeHierarchies = new HashMap<EclipseJavaProject, ITypeHierarchy>();
    typeHierarchyListeners =
        new HashMap<EclipseJavaProject, MyTypeHierarchyChangedListener>();
    types = new HashMap<EclipseJavaProject, IType>();
    contextTypeHierarchies = new HashMap<EclipseJavaProject, ITypeHierarchy>();
    contextTypeHierarchyListeners =
        new HashMap<EclipseJavaProject, MyContextTypeHierarchyChangedListener>();
    contextTypes = new HashMap<EclipseJavaProject, IType>();
    JavaCore.addElementChangedListener(new ModuleElementChangedListener(),
        ElementChangedEvent.POST_CHANGE);
  }

  @Override
  public Set<JavaManager> getOpenProjects() {
    Set<JavaManager> projects = new HashSet<JavaManager>();
    try {
      if (ResourcesPlugin.getWorkspace() != null) {
        for (IProject project : ResourcesPlugin.getWorkspace().getRoot()
            .getProjects()) {
          IJavaProject javaProject = JavaCore.create(project);
          projects.add(new EclipseJavaProject(javaProject));
        }
      }
    } catch (IllegalStateException exception) {
      // workspace is not open
      // means we are in testing mode
    }
    return projects;
  }

  private class MyTypeHierarchyChangedListener implements
      ITypeHierarchyChangedListener {
    private final EclipseJavaProject javaManager;

    public MyTypeHierarchyChangedListener(EclipseJavaProject javaManager) {
      this.javaManager = javaManager;
    }

    public void typeHierarchyChanged(ITypeHierarchy typeHierarchy) {
      EclipseModulesListener.this.setTypeHierarchy(javaManager, typeHierarchy);
      typeHierarchy.addTypeHierarchyChangedListener(this);
    }
  }

  private void setTypeHierarchy(EclipseJavaProject javaManager,
      ITypeHierarchy typeHierarchy) {
    typeHierarchies.put(javaManager, typeHierarchy);
    try {
      keepModulesByName(javaManager, locateModules(javaManager));
    } catch (Throwable throwable) {
      hadProblem(throwable);
    }
  }

  private class MyContextTypeHierarchyChangedListener implements
      ITypeHierarchyChangedListener {
    private final EclipseJavaProject javaManager;

    public MyContextTypeHierarchyChangedListener(EclipseJavaProject javaManager) {
      this.javaManager = javaManager;
    }

    public void typeHierarchyChanged(ITypeHierarchy typeHierarchy) {
      EclipseModulesListener.this.setContextTypeHierarchy(javaManager,
          typeHierarchy);
      typeHierarchy.addTypeHierarchyChangedListener(this);
    }
  }

  private void setContextTypeHierarchy(EclipseJavaProject javaManager,
      ITypeHierarchy typeHierarchy) {
    contextTypeHierarchies.put(javaManager, typeHierarchy);
    try {
      keepContextsByName(javaManager, locateContexts(javaManager));
    } catch (Throwable throwable) {
      hadProblem(throwable);
    }
  }

  @Override
  protected void initialize(JavaManager javaManager) {
    super.initialize(javaManager);
    if (javaManager instanceof EclipseJavaProject) {
      initialize2((EclipseJavaProject) javaManager);
    }
  }

  protected void initialize2(EclipseJavaProject javaManager) {
    try {
      IType moduleType =
          javaManager.getIJavaProject().findType(
              com.google.inject.Module.class.getName());
      if (moduleType != null) {
        types.put(javaManager, moduleType);
        typeHierarchies.put(javaManager, types.get(javaManager)
            .newTypeHierarchy(null));
        typeHierarchyListeners.put(javaManager,
            new MyTypeHierarchyChangedListener(javaManager));
        if (typeHierarchies.get(javaManager) != null) {
          typeHierarchies.get(javaManager).addTypeHierarchyChangedListener(
              typeHierarchyListeners.get(javaManager));
        }
      }

      IType contextType =
          javaManager
              .getIJavaProject()
              .findType(
                  com.google.inject.tools.ideplugin.GuiceIDEPluginContextDefinition.class
                      .getName());
      if (contextType != null) {
        contextTypes.put(javaManager, contextType);
        contextTypeHierarchies.put(javaManager, contextTypes.get(javaManager)
            .newTypeHierarchy(null));
        contextTypeHierarchyListeners.put(javaManager,
            new MyContextTypeHierarchyChangedListener(javaManager));
        if (contextTypeHierarchies.get(javaManager) != null) {
          contextTypeHierarchies.get(javaManager)
              .addTypeHierarchyChangedListener(
                  contextTypeHierarchyListeners.get(javaManager));
        }
      }
    } catch (Throwable throwable) {
      hadProblem(throwable);
    }
  }

  public static class NotEclipseJavaProjectException extends RuntimeException {
    /**
     * 
     */
    private static final long serialVersionUID = 5868250037957080902L;
    private final JavaManager javaManager;

    public NotEclipseJavaProjectException(JavaManager javaManager) {
      this.javaManager = javaManager;
    }

    @Override
    public String toString() {
      return "Not an Eclipse Java Project: " + javaManager.toString();
    }
  }

  @Override
  protected Set<String> locateModules(JavaManager javaManager) throws Throwable {
    if (javaManager instanceof EclipseJavaProject) {
      return locateModules((EclipseJavaProject) javaManager);
    } else {
      throw new NotEclipseJavaProjectException(javaManager);
    }
  }

  protected Set<String> locateModules(EclipseJavaProject javaManager)
      throws Throwable {
    if (javaManager != null && typeHierarchies.get(javaManager) != null) {
      final Set<String> moduleNames = new HashSet<String>();
      typeHierarchies.get(javaManager).refresh(null);
      IType[] subclasses =
          typeHierarchies.get(javaManager).getAllSubtypes(
              types.get(javaManager));
      for (IType subclass : subclasses) {
        if (subclass.isClass()) {
          if (!Flags.isAbstract(subclass.getFlags())) {
            moduleNames.add(subclass.getFullyQualifiedName());
          }
        }
      }
      return moduleNames;
    } else {
      return Collections.<String> emptySet();
    }
  }

  @Override
  protected Set<String> locateContexts(JavaManager javaManager)
      throws Throwable {
    if (javaManager instanceof EclipseJavaProject) {
      return locateContexts((EclipseJavaProject) javaManager);
    } else {
      throw new NotEclipseJavaProjectException(javaManager);
    }
  }

  protected Set<String> locateContexts(EclipseJavaProject javaManager)
      throws Throwable {
    if (javaManager != null && contextTypeHierarchies.get(javaManager) != null) {
      final Set<String> contextNames = new HashSet<String>();
      contextTypeHierarchies.get(javaManager).refresh(null);
      IType[] subclasses =
          contextTypeHierarchies.get(javaManager).getAllSubtypes(
              contextTypes.get(javaManager));
      for (IType subclass : subclasses) {
        if (subclass.isClass()) {
          if (!Flags.isAbstract(subclass.getFlags())) {
            contextNames.add(subclass.getFullyQualifiedName());
          }
        }
      }
      return contextNames;
    } else {
      return Collections.<String> emptySet();
    }
  }

  protected class ModuleElementChangedListener implements
      IElementChangedListener {
    public void elementChanged(ElementChangedEvent event) {
      if (event.getDelta().getElement() instanceof IJavaProject) {
        EclipseJavaProject javaManager =
            new EclipseJavaProject((IJavaProject) event.getDelta().getElement());
        switch (event.getDelta().getKind()) {
          case IJavaElementDelta.F_CLOSED:
            projectManager.projectClosed(javaManager);
            break;
          case IJavaElementDelta.F_OPENED:
            projectManager.projectOpened(javaManager);
            break;
          default:
            // do nothing
        }
      } else {
        handleDelta(event.getDelta());
      }
    }

    private void handleDelta(IJavaElementDelta delta) {
      IJavaElement element = delta.getElement();
      if (element instanceof ICompilationUnit) {
        IJavaProject project = element.getJavaProject();
        EclipseJavaProject javaManager = new EclipseJavaProject(project);
        ICompilationUnit cu = (ICompilationUnit) element;
        // TODO: deal with inner classes...
        IType type = cu.findPrimaryType();
        if (type != null) {
          if (typeHierarchies.get(javaManager).contains(type)) {
            switch (delta.getKind()) {
              case IJavaElementDelta.ADDED:
                EclipseModulesListener.this.moduleAdded(javaManager, type
                    .getFullyQualifiedName());
                break;
              case IJavaElementDelta.CHANGED:
                EclipseModulesListener.this.moduleChanged(javaManager, type
                    .getFullyQualifiedName());
                break;
              case IJavaElementDelta.REMOVED:
                EclipseModulesListener.this.moduleRemoved(javaManager, type
                    .getFullyQualifiedName());
                break;
              default:
                // do nothing
            }
          }
          if (contextTypeHierarchies.get(javaManager).contains(type)) {
            switch (delta.getKind()) {
              case IJavaElementDelta.ADDED:
                EclipseModulesListener.this.contextAdded(javaManager, type
                    .getFullyQualifiedName());
                break;
              case IJavaElementDelta.CHANGED:
                EclipseModulesListener.this.contextChanged(javaManager, type
                    .getFullyQualifiedName());
                break;
              case IJavaElementDelta.REMOVED:
                EclipseModulesListener.this.contextRemoved(javaManager, type
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
