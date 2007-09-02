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

package com.google.inject.tools.ideplugin.intellij;

import com.google.inject.tools.ideplugin.GuicePlugin;
import com.google.inject.tools.ideplugin.JavaProject;
import com.google.inject.tools.ideplugin.module.ModuleSelectionView;
import com.google.inject.tools.ideplugin.results.Results;
import com.google.inject.tools.ideplugin.results.ResultsView;
import com.google.inject.tools.suite.GuiceToolsModule;

/**
 * IntelliJ implementation of the GuicePlugin.
 * 
 * {@inheritDoc GuicePlugin}
 * 
 * @author Darren Creutz (dcreutz@gmail.com)
 */
public class IntellijGuicePlugin extends GuicePlugin {
  /**
   * Create an IntellijGuicePlugin.
   * 
   * @param module the guice module to inject from
   */
  public IntellijGuicePlugin(IntellijPluginModule module,
      GuiceToolsModule toolsModule) {
    super(module, toolsModule);
  }
  
  public static class ResultsViewImpl implements ResultsView {
    public void displayResults(Results results) {
      
    }
  }
  
  public static class ModuleSelectionViewImpl implements ModuleSelectionView {
    public void show(JavaProject project) {
      
    }
  }
}
