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

import com.google.inject.Inject;
import com.google.inject.tools.suite.JavaManager;
import com.google.inject.tools.suite.Messenger;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * {@inheritDoc CustomContextDefinitionSource}
 * 
 * @author Darren Creutz (dcreutz@gmail.com)
 */
public abstract class CustomContextDefinitionSourceImpl implements
    CustomContextDefinitionSource {
  private final Set<CustomContextDefinitionListener> listeners;
  protected final Messenger messenger;
  protected final ProjectManager projectManager;
  private final Map<JavaManager, Set<String>> contexts;

  @Inject
  public CustomContextDefinitionSourceImpl(ProjectManager projectManager,
      Messenger messenger) {
    this.projectManager = projectManager;
    this.messenger = messenger;
    this.listeners = new HashSet<CustomContextDefinitionListener>();
    this.contexts = new HashMap<JavaManager, Set<String>>();
  }
  
  /**
   * Locate custom contexts.
   */
  protected abstract Set<String> locateContexts(JavaManager javaManager)
      throws Throwable;
  
  public Set<String> getContexts(JavaManager javaManager) {
    if (contexts.get(javaManager) == null) {
      initialize(javaManager);
    }
    try {
      keepContextsByName(javaManager, locateContexts(javaManager));
    } catch (Throwable throwable) {
      hadProblem(throwable);
    }
    return new HashSet<String>(contexts.get(javaManager));
  }

  protected synchronized void keepContextsByName(JavaManager javaManager,
      Set<String> contextNames) {
    Set<String> newContexts = new HashSet<String>(contextNames);
    Set<String> removeContexts = new HashSet<String>();
    for (String context : contexts.get(javaManager)) {
      boolean keep = false;
      for (String name : contextNames) {
        if (name.equals(context)) {
          keep = true;
          newContexts.remove(name);
        }
      }
      if (!keep) {
        removeContexts.add(context);
      }
    }
    for (String context : removeContexts) {
      contexts.get(javaManager).remove(context);
    }
    for (String context : newContexts) {
      contexts.get(javaManager).add(context);
    }
  }
  
  protected void initialize(JavaManager javaManager) {
    if (contexts.get(javaManager) == null) {
      contexts.put(javaManager, new HashSet<String>());
    }
  }

  public synchronized void addListener(CustomContextDefinitionListener listener) {
    listeners.add(listener);
  }

  public synchronized void removeListener(
      CustomContextDefinitionListener listener) {
    listeners.remove(listener);
  }

  protected void hadProblem(Throwable exception) {
    messenger.logException("Context Listener error", exception);
  }

  protected void contextDefinitionChanged(JavaManager javaManager,
      String contextDefinitionName) {
    for (CustomContextDefinitionListener listener : listeners) {
      listener.contextDefinitionChanged(this, javaManager,
          contextDefinitionName);
    }
  }

  protected void contextDefinitionRemoved(JavaManager javaManager,
      String contextDefinitionName) {
    for (CustomContextDefinitionListener listener : listeners) {
      listener.contextDefinitionRemoved(this, javaManager,
          contextDefinitionName);
    }
  }

  protected void contextDefinitionAdded(JavaManager javaManager,
      String contextDefinitionName) {
    for (CustomContextDefinitionListener listener : listeners) {
      listener.contextDefinitionAdded(this, javaManager, contextDefinitionName);
    }
  }
}
