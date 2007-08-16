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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.jdt.core.ElementChangedEvent;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IElementChangedListener;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.ITypeHierarchyChangedListener;
import org.eclipse.jdt.core.JavaCore;
import com.google.inject.Singleton;
import com.google.inject.Inject;
import com.google.inject.tools.Messenger;
import com.google.inject.tools.ideplugin.module.ModulesListener;
import com.google.inject.tools.module.ModuleManager;
import com.google.inject.tools.module.ModulesNotifier;

/**
 * Eclipse implementation of the {@link ModulesNotifier}.
 * 
 * @author Darren Creutz <dcreutz@gmail.com>
 */
@Singleton
public class EclipseModulesListener extends ModulesListener {
  private ITypeHierarchy typeHierarchy = null;
  private MyTypeHierarchyChangedListener typeHierarchyListener;
  private IType type;
  
  /**
   * Create an EclipseModulesListener.  This should be injected.
   */
  @Inject
  public EclipseModulesListener(ModuleManager moduleManager,Messenger messenger) {
    super(moduleManager, messenger);
  }
  
  private class MyTypeHierarchyChangedListener implements ITypeHierarchyChangedListener {
    public void typeHierarchyChanged(ITypeHierarchy typeHierarchy) {
      EclipseModulesListener.this.setTypeHierarchy(typeHierarchy);
      typeHierarchy.addTypeHierarchyChangedListener(this);
    }
  }
  
  private void setTypeHierarchy(ITypeHierarchy typeHierarchy) {
    this.typeHierarchy = typeHierarchy;
    findModules();
  }
  
  @Override
  protected void initialize() throws Throwable {
    if (javaManager != null) {
      JavaCore.addElementChangedListener(new ModuleElementChangedListener(), ElementChangedEvent.POST_CHANGE);
      type = ((EclipseJavaProject)javaManager).getIJavaProject().findType("com.google.inject.Module");
      typeHierarchy = type.newTypeHierarchy(null);
      typeHierarchyListener = new MyTypeHierarchyChangedListener();
      if (typeHierarchy!=null) {
        typeHierarchy.addTypeHierarchyChangedListener(typeHierarchyListener);
      }
    }
  }
  
  @Override
  protected Set<String> locateModules() throws Throwable {
    if (javaManager != null) {
      final Set<String> moduleNames = new HashSet<String>();
      if (typeHierarchy == null) {
        initialize();
      }
      typeHierarchy.refresh(null);
      IType[] subclasses = typeHierarchy.getAllSubtypes(type);
      for (IType subclass : subclasses) {
        if (subclass.isClass()) {
          if (!Flags.isAbstract(subclass.getFlags())) {
            moduleNames.add(subclass.getFullyQualifiedName());
          }
        }
      }
      return moduleNames;
    } else {
      return Collections.<String>emptySet();
    }
  }
  
  //TODO: test that this works
  protected class ModuleElementChangedListener implements IElementChangedListener {
    public void elementChanged(ElementChangedEvent event) {
      if (event.getDelta().getElement() instanceof IType) {
        IType type = (IType)event.getDelta().getElement();
        if (typeHierarchy.contains(type)) {
          moduleManager.moduleChanged(type.getFullyQualifiedName());
        }
      }
    }
  }
}
