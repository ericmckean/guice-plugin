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

/**
 * Represent the project wide settings for the plugin.
 * 
 * @author Darren Creutz (dcreutz@gmail.com)
 */
public class ProjectSettings {
  /**
   * Should modules be treated as contexts by default.
   */
  public boolean activateByDefault;
  /**
   * Should module contexts be (re)run automatically in response to changes.
   */
  public boolean runAutomatically;
  /**
   * Should the plugin listen for changes to contexts.
   */
  public boolean listenForChanges;
  
  //TODO: rest of settings
  
  /**
   * Create settings with default values.
   */
  public ProjectSettings() {
    activateByDefault = false;
    runAutomatically = false;
  }
}
