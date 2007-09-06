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

import com.google.inject.tools.ideplugin.JavaProject;

import java.util.Set;

/**
 * The source for finding custom module contexts defined by the user
 * ({@link com.google.inject.tools.suite.module.CustomModuleContextRepresentation}).
 * 
 * @author Darren Creutz (dcreutz@gmail.com)
 */
public interface CustomContextDefinitionSource {
  /**
   * A listener for changes in the custom contexts.
   */
  public interface CustomContextDefinitionListener {
    public void contextDefinitionAdded(CustomContextDefinitionSource source,
        JavaProject javaProject, String contextDefinitionName);

    public void contextDefinitionRemoved(CustomContextDefinitionSource source,
        JavaProject javaProject, String contextDefinitionName);

    public void contextDefinitionChanged(CustomContextDefinitionSource source,
        JavaProject javaProject, String contextDefinitionName);
  }

  public void addListener(CustomContextDefinitionListener listener);

  public void removeListener(CustomContextDefinitionListener listener);

  /**
   * Find and return all custom contexts; usually called just before calling
   * addListener(this).
   * 
   * @param javaProject the java context to find custom module contexts for
   */
  public Set<String> getContexts(JavaProject javaProject);
  
  /**
   * Called by the client when a possible refresh is needed.
   */
  public void refresh(JavaProject javaProject);
  
  public abstract boolean isListeningForChanges();
  
  public abstract void listenForChanges(boolean listenForChanges);
}
