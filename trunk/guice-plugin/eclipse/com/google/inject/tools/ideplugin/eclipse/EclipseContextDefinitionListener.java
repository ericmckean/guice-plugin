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
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.NormalAnnotation;

import com.google.inject.Singleton;
import com.google.inject.Inject;
import com.google.inject.tools.ideplugin.CustomContextDefinitionSourceImpl;
import com.google.inject.tools.ideplugin.JavaProject;
import com.google.inject.tools.ideplugin.ProjectManager;
import com.google.inject.tools.ideplugin.module.ModulesSource;
import com.google.inject.tools.suite.Messenger;

/**
 * Eclipse implementation of the {@link ModulesSource}.
 * 
 * {@inheritDoc ModulesListener}
 * 
 * @author Darren Creutz (dcreutz@gmail.com)
 */
@Singleton
class EclipseContextDefinitionListener extends CustomContextDefinitionSourceImpl {
  private final Map<EclipseJavaProject, ITypeHierarchy> contextTypeHierarchies;
  private final Map<EclipseJavaProject, MyContextTypeHierarchyChangedListener> contextTypeHierarchyListeners;
  private final Map<EclipseJavaProject, IType> contextTypes;

  /**
   * Create an EclipseModulesListener. This should be injected.
   */
  @Inject
  public EclipseContextDefinitionListener(ProjectManager projectManager,
      Messenger messenger) {
    super(projectManager, messenger);
    contextTypeHierarchies = new HashMap<EclipseJavaProject, ITypeHierarchy>();
    contextTypeHierarchyListeners =
        new HashMap<EclipseJavaProject, MyContextTypeHierarchyChangedListener>();
    contextTypes = new HashMap<EclipseJavaProject, IType>();
    JavaCore.addElementChangedListener(new ModuleElementChangedListener(),
        ElementChangedEvent.POST_CHANGE);
  }

  private class MyContextTypeHierarchyChangedListener implements
      ITypeHierarchyChangedListener {
    private final EclipseJavaProject javaManager;

    public MyContextTypeHierarchyChangedListener(EclipseJavaProject javaManager) {
      this.javaManager = javaManager;
    }

    public void typeHierarchyChanged(ITypeHierarchy typeHierarchy) {
      EclipseContextDefinitionListener.this.setContextTypeHierarchy(javaManager,
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
  protected void initialize(JavaProject javaManager) {
    super.initialize(javaManager);
    if (javaManager instanceof EclipseJavaProject) {
      initialize2((EclipseJavaProject) javaManager);
    }
  }
  
  public void refresh(JavaProject javaManager) {
    if (contextTypes.get(javaManager) == null 
        || contextTypeHierarchies.get(javaManager) == null) {
      if (javaManager instanceof EclipseJavaProject) {
        initialize2((EclipseJavaProject) javaManager);
      }
    }
  }

  protected void initialize2(EclipseJavaProject javaManager) {
    try {
      IType contextType =
          javaManager
              .getIJavaProject()
              .findType(
                  com.google.inject.Module.class
                      .getName());
      if (contextType != null && contextType.getJavaProject().equals(javaManager.getIJavaProject())) {
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
      //hadProblem(throwable);
    }
  }

  public static class NotEclipseJavaProjectException extends RuntimeException {
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
  protected Set<String> locateContexts(JavaProject javaManager)
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
            if (isAnnotatedWithApplicationModule(subclass)) {
              contextNames.add(subclass.getFullyQualifiedName());
            }
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
        try {
          handleDelta(event.getDelta());
        } catch (JavaModelException e) {}
      }
    }

    private void handleDelta(IJavaElementDelta delta) throws JavaModelException {
      IJavaElement element = delta.getElement();
      if (element instanceof ICompilationUnit) {
        IJavaProject project = element.getJavaProject();
        EclipseJavaProject javaManager = new EclipseJavaProject(project);
        ICompilationUnit cu = (ICompilationUnit) element;
        for (IType type : cu.getAllTypes()) {
          if (contextTypeHierarchies.get(javaManager) != null
              && contextTypeHierarchies.get(javaManager).contains(type)
              && isAnnotatedWithApplicationModule(type)) {
            switch (delta.getKind()) {
              case IJavaElementDelta.ADDED:
                EclipseContextDefinitionListener.this.contextDefinitionAdded(javaManager, type
                    .getFullyQualifiedName());
                break;
              case IJavaElementDelta.CHANGED:
                EclipseContextDefinitionListener.this.contextDefinitionChanged(javaManager, type
                    .getFullyQualifiedName());
                break;
              case IJavaElementDelta.REMOVED:
                EclipseContextDefinitionListener.this.contextDefinitionRemoved(javaManager, type
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
  
  private boolean isAnnotatedWithApplicationModule(IType type) {
    try {
      ASTParser parser = ASTParser.newParser(AST.JLS3);
      parser.setKind(ASTParser.K_COMPILATION_UNIT);
      parser.setSource(type.getCompilationUnit());
      CompilationUnit cu = (CompilationUnit) parser.createAST(null);
      MyASTVisitor visitor = new MyASTVisitor();
      cu.accept(visitor);
      return visitor.hasApplicationModuleAnnotation();
    } catch (Throwable t) {
      return false;
    }
  }
  
  private static class MyASTVisitor extends ASTVisitor {
    private boolean hasApplicationModuleAnnotation = false;
    @Override
    public boolean visit(NormalAnnotation node) {
      if (node.getTypeName().getFullyQualifiedName().equals("com.google.inject.ApplicationModule")) {
        hasApplicationModuleAnnotation = true;
      }
      return false;
    }
    public boolean hasApplicationModuleAnnotation() {
      return hasApplicationModuleAnnotation;
    }
  }
}
