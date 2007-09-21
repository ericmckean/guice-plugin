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

package com.google.inject.tools.ideplugin;

import java.util.HashSet;
import java.util.Set;

import com.google.inject.Inject;
import com.google.inject.tools.ideplugin.JavaProject;
import com.google.inject.tools.suite.Messenger;

/**
 * Abstract implementation of the {@link ProjectSource} for the IDE plugin.
 * 
 * @author Darren Creutz (dcreutz@gmail.com)
 */
public abstract class AbstractProjectSourceImpl implements ProjectSource {
  protected final Messenger messenger;
  protected final Set<ProjectSourceListener> listeners;

  @Inject
  public AbstractProjectSourceImpl(Messenger messenger) {
    this.messenger = messenger;
    this.listeners = new HashSet<ProjectSourceListener>();
  }
  
  public abstract Set<JavaProject> getOpenProjects();

  public void addListener(ProjectSourceListener listener) {
    listeners.add(listener);
  }

  public void removeListener(ProjectSourceListener listener) {
    listeners.remove(listener);
  }

  protected void javaManagerAdded(JavaProject project) {
    for (ProjectSourceListener listener : listeners) {
      listener.javaManagerAdded(this, project);
    }
  }
  
  protected void javaManagerRemoved(JavaProject project) {
    for (ProjectSourceListener listener : listeners) {
      listener.javaManagerRemoved(this, project);
    }
  }
  
  protected void hadProblem(Throwable exception) {
    //messenger.logException("Source error", exception);
  }
}
