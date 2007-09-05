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

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.inject.Inject;
import com.google.inject.tools.ideplugin.JavaProject;
import com.google.inject.tools.suite.JavaManager;
import com.google.inject.tools.suite.Messenger;

/**
 * Abstract implementation of the {@link ModulesSource} for the IDE plugin.
 * 
 * @author Darren Creutz (dcreutz@gmail.com)
 */
public abstract class ModulesListener implements ModulesSource {
  protected final Messenger messenger;
  private final Set<ModulesSourceListener> listeners;
  private final Map<JavaProject, Set<String>> modules;

  @Inject
  public ModulesListener(Messenger messenger) {
    this.messenger = messenger;
    this.listeners = new HashSet<ModulesSourceListener>();
    this.modules = new HashMap<JavaProject, Set<String>>();
  }

  /**
   * Create listeners and find initially opened projects.
   */
  public abstract Set<JavaProject> getOpenProjects();

  /**
   * Locate the modules.
   */
  protected abstract Set<String> locateModules(JavaProject javaProject)
      throws Throwable;

  public Set<String> getModules(JavaManager javaManager) {
    if (javaManager instanceof JavaProject) {
      JavaProject project = (JavaProject)javaManager;
      if (modules.get(project) == null) {
        initialize(project);
      }
      try {
        keepModulesByName(project, locateModules(project));
      } catch (Throwable throwable) {
        hadProblem(throwable);
      }
      return new HashSet<String>(modules.get(project));
    } else {
      return Collections.<String>emptySet();
    }
  }

  protected synchronized void keepModulesByName(JavaProject javaManager,
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

  protected void initialize(JavaProject javaManager) {
    if (modules.get(javaManager) == null) {
      modules.put(javaManager, new HashSet<String>());
    }
  }

  protected void hadProblem(Throwable exception) {
    //messenger.logException("Modules Listener error", exception);
  }

  public void addListener(ModulesSourceListener listener) {
    listeners.add(listener);
  }

  public void removeListener(ModulesSourceListener listener) {
    listeners.remove(listener);
  }

  protected void moduleChanged(JavaProject javaManager, String moduleName) {
    for (ModulesSourceListener listener : listeners) {
      listener.moduleChanged(this, javaManager, moduleName);
    }
  }

  protected void moduleRemoved(JavaProject javaManager, String moduleName) {
    for (ModulesSourceListener listener : listeners) {
      listener.moduleRemoved(this, javaManager, moduleName);
    }
  }

  protected void moduleAdded(JavaProject javaManager, String moduleName) {
    for (ModulesSourceListener listener : listeners) {
      listener.moduleAdded(this, javaManager, moduleName);
    }
  }
  
  protected void javaManagerAdded(JavaProject javaManager) {
    for (ModulesSourceListener listener : listeners) {
      listener.javaManagerAdded(this, javaManager);
    }
  }
  
  protected void javaManagerRemoved(JavaProject javaManager) {
    for (ModulesSourceListener listener : listeners) {
      listener.javaManagerRemoved(this, javaManager);
    }
  }
}
