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

package com.google.inject.tools.ideplugin.test;

import com.google.inject.tools.ideplugin.GuicePlugin;
import com.google.inject.tools.ideplugin.results.ResultsView;
import com.google.inject.tools.ideplugin.module.ModuleSelectionView;
import org.easymock.EasyMock;

/**
 * Mock the {@link GuicePlugin} object.
 * 
 * @author Darren Creutz <dcreutz@gmail.com>
 */
public class MockGuicePlugin extends GuicePlugin {
  public MockGuicePlugin() {
    super(new MockGuicePluginModule());
  }
  
  public ResultsView getResultsView() {
    return EasyMock.createMock(ResultsView.class);
  }
  
  public ModuleSelectionView getModuleSelectionView() {
    return EasyMock.createMock(ModuleSelectionView.class);
  }
}
