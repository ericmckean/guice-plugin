/**
 * Copyright (C) 2007 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.inject.tools.ideplugin.eclipse;

import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;

import com.google.inject.tools.ideplugin.GuicePlugin;
import com.google.inject.tools.ideplugin.module.ModuleSelectionView;
import com.google.inject.tools.ideplugin.results.ResultsView;
import com.google.inject.tools.ideplugin.results.Results;

/**
 * Eclipse implementation of the GuicePlugin.
 * 
 * @author Darren Creutz <dcreutz@gmail.com>
 */
public class EclipseGuicePlugin extends GuicePlugin {
  public static class ResultsViewImpl implements ResultsView {
    public void displayResults(Results results) {
      try {
        IViewPart viewPart = PlatformUI.getWorkbench().getWorkbenchWindows()[0].getActivePage().showView("com.google.inject.tools.ideplugin.eclipse.EclipseResultsView");
        ((EclipseResultsView)viewPart).displayResults(results);
      } catch (Exception e) {
        //TODO: what to do here?
      }
    }
  }
  
  public static class ModuleSelectionViewImpl implements ModuleSelectionView {
  }
  
  private final ResultsView resultsViewImpl;
  private final ModuleSelectionView moduleSelectionViewImpl;
  
  /**
   * Create an EclipseGuicePlugin.
   * 
   * @param module the guice module to inject from
   */
  public EclipseGuicePlugin(EclipsePluginModule module) {
    super(module);
    this.resultsViewImpl = module.getResultsView();
    this.moduleSelectionViewImpl = module.getModuleSelectionView();
  }
  
  /**
   * (non-Javadoc)
   * @see com.google.inject.tools.ideplugin.GuicePlugin#getResultsView()
   */
  @Override
  public ResultsView getResultsView() {
    return resultsViewImpl;
  }
  
  /**
   * (non-Javadoc)
   * @see com.google.inject.tools.ideplugin.GuicePlugin#getModuleSelectionView()
   */
  @Override
  public ModuleSelectionView getModuleSelectionView() {
    return moduleSelectionViewImpl;
  }
}
