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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.ElementChangedEvent;
import org.eclipse.jdt.core.IElementChangedListener;
import org.eclipse.jdt.core.IJavaElementDelta;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

import com.google.inject.Singleton;
import com.google.inject.Inject;
import com.google.inject.tools.ideplugin.JavaProject;
import com.google.inject.tools.ideplugin.ProjectSourceImpl;
import com.google.inject.tools.suite.Messenger;

/**
 * Eclipse implementation of the {@link ProjectSource}.
 * 
 * {@inheritDoc ProjectSource}
 * 
 * @author Darren Creutz (dcreutz@gmail.com)
 */
@Singleton
class EclipseProjectSource extends ProjectSourceImpl {
  private boolean listenForChanges;
  private ElementChangedListener changeListener;

  @Inject
  public EclipseProjectSource(Messenger messenger) {
    super(messenger);
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
    JavaCore.addElementChangedListener(changeListener, ElementChangedEvent.POST_CHANGE);
  }
  
  private void stopListeningForChanges() {
    JavaCore.removeElementChangedListener(changeListener);
    changeListener = null;
  }

  @Override
  public Set<JavaProject> getOpenProjects() {
    Set<JavaProject> projects = new HashSet<JavaProject>();
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

  protected class ElementChangedListener implements
      IElementChangedListener {
    public void elementChanged(ElementChangedEvent event) {
      if (event.getDelta().getElement() instanceof IJavaProject) {
        EclipseJavaProject javaManager =
            new EclipseJavaProject((IJavaProject) event.getDelta().getElement());
        switch (event.getDelta().getKind()) {
          case IJavaElementDelta.F_CLOSED:
            javaManagerRemoved(javaManager);
            break;
          case IJavaElementDelta.F_OPENED:
            javaManagerAdded(javaManager);
            break;
          default:
            // do nothing
        }
      }
    }
  }
}
