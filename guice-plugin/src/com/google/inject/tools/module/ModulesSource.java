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

package com.google.inject.tools.module;

import com.google.inject.tools.suite.JavaManager;

import java.util.Set;

/**
 * Responsible for listening to changes in the user's code involving
 * {@link com.google.inject.Module}s. IDE specific implementations should
 * notify the ModuleManager when changes occur by creating
 * {@link ModuleRepresentation} objects and passing them to the manager.
 * 
 * @author Darren Creutz <dcreutz@gmail.com>
 */
public interface ModulesSource {
  /**
   * Find and return all the modules available.
   * This should be called just before addListener(this).
   * 
   * @param javaManager the java context to get the modules for
   */
  public Set<String> getModules(JavaManager javaManager);

  public void addListener(ModulesSourceListener listener);

  public void removeListener(ModulesSourceListener listener);

  /**
   * Listener for changes in the modules available.
   * 
   * @author Darren Creutz <dcreutz@gmail.com>
   */
  public interface ModulesSourceListener {
    void moduleChanged(ModulesSource source, JavaManager javaManager,
        String module);

    void moduleAdded(ModulesSource source, JavaManager javaManager,
        String module);

    void moduleRemoved(ModulesSource source, JavaManager javaManager,
        String module);
  }
}
