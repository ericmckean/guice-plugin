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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.inject.Inject;
import com.google.inject.tools.ideplugin.JavaProject;
import com.google.inject.tools.suite.Messenger;
import com.google.inject.tools.suite.ProgressHandler.ProgressMonitor;

/**
 * Abstract implementation of the {@link Source} for the IDE plugin.
 * 
 * @author Darren Creutz (dcreutz@gmail.com)
 */
public abstract class AbstractSourceImpl implements Source {
  protected final Messenger messenger;
  protected final Set<SourceListener> listeners;
  protected final Set<JavaProject> projects;
  private final Map<JavaProject, Set<String>> things;

  @Inject
  public AbstractSourceImpl(Messenger messenger) {
    this.messenger = messenger;
    this.listeners = new HashSet<SourceListener>();
    this.projects = new HashSet<JavaProject>();
    this.things = new HashMap<JavaProject, Set<String>>();
  }

  /**
   * Locate the modules.
   */
  protected abstract Set<String> locate(JavaProject javaProject,
      ProgressMonitor progressMonitor) throws Throwable;

  public Set<String> get(JavaProject project, ProgressMonitor progressMonitor) {
    if (things.get(project) == null) {
      initialize(project);
    }
    try {
      keepByName(project, locate(project, progressMonitor));
    } catch (Throwable throwable) {
      hadProblem(throwable);
    }
    return new HashSet<String>(things.get(project));
  }

  protected synchronized void keepByName(JavaProject javaManager, Set<String> names) {
    Set<String> newThings = new HashSet<String>(names);
    Set<String> removeThings = new HashSet<String>();
    for (String thing : things.get(javaManager)) {
      boolean keep = false;
      for (String name : names) {
        if (name.equals(thing)) {
          keep = true;
          newThings.remove(name);
        }
      }
      if (!keep) {
        removeThings.add(thing);
      }
    }
    for (String thing : removeThings) {
      things.get(javaManager).remove(thing);
    }
    for (String name : newThings) {
      things.get(javaManager).add(name);
    }
  }

  protected void initialize(JavaProject javaManager) {
    projects.add(javaManager);
    if (things.get(javaManager) == null) {
      things.put(javaManager, new HashSet<String>());
    }
  }

  protected void hadProblem(Throwable exception) {
    //messenger.logException("Source error", exception);
  }

  public void addListener(SourceListener listener) {
    listeners.add(listener);
  }

  public void removeListener(SourceListener listener) {
    listeners.remove(listener);
  }

  protected void changed(JavaProject javaManager, String name) {
    for (SourceListener listener : listeners) {
      listener.changed(this, javaManager, name);
    }
  }

  protected void removed(JavaProject javaManager, String name) {
    for (SourceListener listener : listeners) {
      listener.removed(this, javaManager, name);
    }
  }

  protected void added(JavaProject javaManager, String name) {
    for (SourceListener listener : listeners) {
      listener.added(this, javaManager, name);
    }
  }
}
