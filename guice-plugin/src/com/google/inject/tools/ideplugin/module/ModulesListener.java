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

package com.google.inject.tools.ideplugin.module;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.inject.Inject;
import com.google.inject.tools.ideplugin.CustomContextDefinitionSource;
import com.google.inject.tools.ideplugin.ProjectManager;
import com.google.inject.tools.suite.JavaManager;
import com.google.inject.tools.suite.Messenger;
import com.google.inject.tools.suite.module.ModulesSource;

/**
 * Abstract implementation of the {@link ModulesSource} and
 * {@link CustomContextDefinitionSource} for the IDE plugins.
 * 
 * {@inheritDoc ModulesSource}
 * 
 * {@inheritDoc CustomContextDefinitionSource}
 * 
 * @author Darren Creutz (dcreutz@gmail.com)
 */
public abstract class ModulesListener implements ModulesSource,
    CustomContextDefinitionSource {
  protected final Messenger messenger;
  protected final ProjectManager projectManager;
  private final Set<ModulesSourceListener> listeners;
  private final Map<JavaManager, Set<String>> modules;
  private final Set<CustomContextDefinitionListener> contextListeners;
  private final Map<JavaManager, Set<String>> contexts;

  /**
   * Create an EclipseModulesListener. This should be injected.
   */
  @Inject
  public ModulesListener(ProjectManager projectManager, Messenger messenger) {
    this.projectManager = projectManager;
    this.messenger = messenger;
    this.listeners = new HashSet<ModulesSourceListener>();
    this.modules = new HashMap<JavaManager, Set<String>>();
    this.contextListeners = new HashSet<CustomContextDefinitionListener>();
    this.contexts = new HashMap<JavaManager, Set<String>>();
  }

  /**
   * Create listeners and find initially opened projects.
   */
  public abstract Set<JavaManager> getOpenProjects();

  /**
   * Locate the modules.
   */
  protected abstract Set<String> locateModules(JavaManager javaManager)
      throws Throwable;

  /**
   * Locate custom contexts.
   */
  protected abstract Set<String> locateContexts(JavaManager javaManager)
      throws Throwable;

  public Set<String> getModules(JavaManager javaManager) {
    if (modules.get(javaManager) == null) {
      initialize(javaManager);
    }
    try {
      keepModulesByName(javaManager, locateModules(javaManager));
    } catch (Throwable throwable) {
      hadProblem(throwable);
    }
    return new HashSet<String>(modules.get(javaManager));
  }

  protected synchronized void keepModulesByName(JavaManager javaManager,
      Set<String> modulesNames) {
    Set<String> newModules = new HashSet<String>(modulesNames);
    Set<String> removeModules = new HashSet<String>();
    for (String module : modules.get(javaManager)) {
      boolean keep = false;
      for (String name : modulesNames) {
        if (name.equals(module)) {
          keep = true;
          newModules.remove(name);
        }
      }
      if (!keep) {
        removeModules.add(module);
      }
    }
    for (String module : removeModules) {
      modules.get(javaManager).remove(module);
    }
    for (String moduleName : newModules) {
      modules.get(javaManager).add(moduleName);
    }
  }

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
    if (modules.get(javaManager) == null) {
      modules.put(javaManager, new HashSet<String>());
    }
    if (contexts.get(javaManager) == null) {
      contexts.put(javaManager, new HashSet<String>());
    }
  }

  protected void hadProblem(Throwable exception) {
    messenger.logException("Modules Listener error", exception);
  }

  public void addListener(ModulesSourceListener listener) {
    listeners.add(listener);
  }

  public void removeListener(ModulesSourceListener listener) {
    listeners.remove(listener);
  }

  public void addListener(CustomContextDefinitionListener listener) {
    contextListeners.add(listener);
  }

  public void removeListener(CustomContextDefinitionListener listener) {
    contextListeners.remove(listener);
  }

  protected void moduleChanged(JavaManager javaManager, String moduleName) {
    for (ModulesSourceListener listener : listeners) {
      listener.moduleChanged(this, javaManager, moduleName);
    }
  }

  protected void moduleRemoved(JavaManager javaManager, String moduleName) {
    for (ModulesSourceListener listener : listeners) {
      listener.moduleRemoved(this, javaManager, moduleName);
    }
  }

  protected void moduleAdded(JavaManager javaManager, String moduleName) {
    for (ModulesSourceListener listener : listeners) {
      listener.moduleAdded(this, javaManager, moduleName);
    }
  }

  protected void contextChanged(JavaManager javaManager, String contextName) {
    for (CustomContextDefinitionListener listener : contextListeners) {
      listener.contextDefinitionChanged(this, javaManager, contextName);
    }
  }

  protected void contextRemoved(JavaManager javaManager, String contextName) {
    for (CustomContextDefinitionListener listener : contextListeners) {
      listener.contextDefinitionRemoved(this, javaManager, contextName);
    }
  }

  protected void contextAdded(JavaManager javaManager, String contextName) {
    for (CustomContextDefinitionListener listener : contextListeners) {
      listener.contextDefinitionAdded(this, javaManager, contextName);
    }
  }
}
