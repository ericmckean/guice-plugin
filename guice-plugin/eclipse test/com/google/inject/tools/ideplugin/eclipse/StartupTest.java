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

import junit.framework.TestCase;
import com.google.inject.Injector;
import com.google.inject.Guice;
import com.google.inject.tools.ideplugin.module.ModuleManager;
import com.google.inject.tools.ideplugin.results.ResultsView;
import com.google.inject.tools.ideplugin.module.ModulesListener;
import com.google.inject.tools.ideplugin.module.ModuleSelectionView;
import com.google.inject.tools.ideplugin.results.ResultsHandler;
import com.google.inject.tools.ideplugin.Messenger;
import com.google.inject.tools.ideplugin.GuicePluginModule;
import com.google.inject.tools.ideplugin.ProgressHandler;
import com.google.inject.tools.ideplugin.problem.ProblemsHandler;
import com.google.inject.tools.ideplugin.ActionsHandler;
import com.google.inject.tools.ideplugin.test.MockJavaElement;
import com.google.inject.tools.ideplugin.JavaElement;

/** 
 * Test the activator and therefore the plugin object and the module for our plugin for guice 
 * related errors.
 * This is important since if the plugin throws an uncaught exception, we do not get notified
 * instead Eclipse just runs without the plugin.
 * 
 * @author Darren Creutz <dcreutz@gmail.com>
 */
public class StartupTest extends TestCase {
  /** 
   * Create a new activator and therefore a new GuicePlugin.
   */
  public void testActivatorConstructor() {
    @SuppressWarnings({"unused"})
    Activator activator = new Activator();
    assertNotNull(Activator.getGuicePlugin());
  }
  
  public void testCreatingInjections() {
    Activator activator = new Activator();
    EclipsePluginModule module = new EclipsePluginModule();
    module.setModuleSelectionView(new EclipseGuicePlugin.ModuleSelectionViewImpl());
    module.setResultsView(new EclipseGuicePlugin.ResultsViewImpl());
    Injector injector = Guice.createInjector(module);
    assertNotNull(injector.getInstance(ModuleManager.class));
    assertNotNull(injector.getInstance(ModulesListener.class));
    assertNotNull(injector.getInstance(ResultsView.class));
    assertNotNull(injector.getInstance(ModuleSelectionView.class));
    assertNotNull(injector.getInstance(ResultsHandler.class));
    assertNotNull(injector.getInstance(ProblemsHandler.class));
    assertNotNull(injector.getInstance(ActionsHandler.class));
    assertNotNull(injector.getInstance(Messenger.class));
    assertNotNull(injector.getInstance(GuicePluginModule.CodeRunnerFactory.class));
    assertNotNull(injector.getInstance(ProgressHandler.class));
  }
  
  public void testCreateBindingsEngine() {
    boolean calledMessenger = false;
    EclipsePluginModule module = new EclipsePluginModule();
    module.setModuleSelectionView(new EclipseGuicePlugin.ModuleSelectionViewImpl());
    module.setResultsView(new EclipseGuicePlugin.ResultsViewImpl());
    new EclipseGuicePlugin(module).getBindingsEngine(new MockJavaElement(JavaElement.Type.FIELD));
  }
}
