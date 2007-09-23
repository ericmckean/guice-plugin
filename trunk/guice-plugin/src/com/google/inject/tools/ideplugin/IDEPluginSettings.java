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

import java.util.ArrayList;
import java.util.List;

import com.google.inject.tools.suite.Settings;

/**
 * Represent the project wide settings for the plugin.
 * 
 * @author Darren Creutz (dcreutz@gmail.com)
 */
public class IDEPluginSettings implements Settings {
  public static final String activateByDefaultName = "Activate Contexts By Default";
  public static final String listenForChangesName = "Listen for Changes";
  public static final String runAutomaticallyName = "Run Contexts Automatically";
  
  public interface ProjectSettingsVisitor {
    public void visit(String name, boolean value);
  }
  
  public interface ProjectSettingsSaver {
    public boolean getBoolean(String name);
  }
  
  /**
   * Should modules be treated as contexts by default.
   */
  private boolean activateByDefault;
  /**
   * Should module contexts be (re)run automatically in response to changes.
   */
  private boolean runAutomatically;
  /**
   * Should the plugin listen for changes to contexts.
   */
  private boolean listenForChanges;
  
  /**
   * Create settings with default values.
   */
  public IDEPluginSettings() {
    activateByDefault = false;
    runAutomatically = false;
    listenForChanges = false;
  }
  
  /**
   * Create settings based on saved values.
   */
  public IDEPluginSettings(String serialized) {
    List<Boolean> values = new ArrayList<Boolean>();
    parse(serialized, values);
    activateByDefault = values.get(0);
    runAutomatically = values.get(1);
    listenForChanges = values.get(2);
  }
  
  private void parse(String string, List<Boolean> values) {
    int index = string.indexOf(';');
    if (index == -1) {
      values.add(Boolean.valueOf(string));
    } else {
      values.add(Boolean.valueOf(string.substring(0, index)));
      parse(string.substring(index+1), values);
    }
  }
  
  /**
   * Create settings from a visitor.
   */
  public IDEPluginSettings(ProjectSettingsSaver saver) {
    activateByDefault = saver.getBoolean(activateByDefaultName);
    listenForChanges = saver.getBoolean(listenForChangesName);
    runAutomatically = saver.getBoolean(runAutomaticallyName);
  }
  
  /**
   * Export values as string for saving.
   */
  public String serialize() {
    return String.valueOf(activateByDefault) + ";" + String.valueOf(runAutomatically)
      + ";" + String.valueOf(listenForChanges);
  }
  
  public void accept(ProjectSettingsVisitor visitor) {
    visitor.visit(activateByDefaultName, activateByDefault);
    visitor.visit(listenForChangesName, listenForChanges);
    visitor.visit(runAutomaticallyName, runAutomatically);
  }
  
  @Override
  public int hashCode() {
    return (activateByDefault ? 4 : 0) + (listenForChanges ? 2 : 0) + (runAutomatically ? 1 : 0);
  }
  
  @Override
  public boolean equals(Object object) {
    if (!(object instanceof IDEPluginSettings)) return false;
    IDEPluginSettings settings = (IDEPluginSettings)object;
    return (settings.activateByDefault == activateByDefault)
    && (settings.listenForChanges == listenForChanges)
    && (settings.runAutomatically == runAutomatically);
  }
  
  @Override
  public String toString() {
    return "ProjectSettings: activateByDefault="+activateByDefault+" listenForChanges="
      +listenForChanges+" runAutomatically="+runAutomatically;
  }
  
  public boolean activateByDefault() {
    return activateByDefault;
  }
  
  public boolean runAutomatically() {
    return runAutomatically;
  }
  
  public boolean listenForChanges() {
    return listenForChanges;
  }
  
  public void setActivateByDefault(boolean activateByDefault) {
    this.activateByDefault = activateByDefault;
  }
  
  public void setRunAutomatically(boolean runAutomatically) {
    this.runAutomatically = runAutomatically;
  }
  
  public void setListenForChanges(boolean listenForChanges) {
    this.listenForChanges = listenForChanges;
  }
}
