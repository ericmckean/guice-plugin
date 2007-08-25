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

package com.google.inject.tools.ideplugin.eclipse;

import junit.framework.TestCase;
import com.google.inject.Injector;
import com.google.inject.Guice;
import com.google.inject.tools.ideplugin.Fakes.FakeJavaElement;
import com.google.inject.tools.ideplugin.eclipse.EclipsePluginModule.EclipseGuiceToolsModule;
import com.google.inject.tools.ideplugin.results.ResultsView;
import com.google.inject.tools.ideplugin.results.ResultsHandler;
import com.google.inject.tools.ideplugin.ActionsHandler;
import com.google.inject.tools.ideplugin.JavaElement;
import com.google.inject.tools.ideplugin.ProjectManager;
import com.google.inject.tools.ideplugin.module.ModuleSelectionView;
import com.google.inject.tools.suite.GuiceToolsModule;
import com.google.inject.tools.suite.Messenger;
import com.google.inject.tools.suite.ProblemsHandler;
import com.google.inject.tools.suite.ProgressHandler;
import com.google.inject.tools.suite.Fakes.FakeJavaManager;
import com.google.inject.tools.suite.module.ModulesSource;

/**
 * Test the activator and therefore the plugin object and the module for our
 * plugin for guice related errors. This is important since if the plugin throws
 * an uncaught exception, we do not get notified instead Eclipse just runs
 * without the plugin.
 * 
 * @author Darren Creutz <dcreutz@gmail.com>
 */
public class StartupTest extends TestCase {
  /**
   * Create a new activator and therefore a new GuicePlugin.
   */
  public void testActivatorConstructor() {
    @SuppressWarnings( {"unused"})
    Activator activator = new Activator();
    assertNotNull(Activator.getGuicePlugin());
  }

  public void testCreatingInjections() {
    Activator activator = new Activator();
    EclipsePluginModule module = new EclipsePluginModule();
    EclipseGuiceToolsModule toolsModule = new EclipseGuiceToolsModule();
    Injector injector = Guice.createInjector(module, toolsModule);
    assertNotNull(injector.getInstance(ProjectManager.class));
    assertNotNull(injector.getInstance(ModulesSource.class));
    assertNotNull(injector.getInstance(ResultsView.class));
    assertNotNull(injector.getInstance(ModuleSelectionView.class));
    assertNotNull(injector.getInstance(ResultsHandler.class));
    assertNotNull(injector.getInstance(ProblemsHandler.class));
    assertNotNull(injector.getInstance(ActionsHandler.class));
    assertNotNull(injector.getInstance(Messenger.class));
    assertNotNull(injector
        .getInstance(GuiceToolsModule.CodeRunnerFactory.class));
    assertNotNull(injector.getInstance(ProgressHandler.class));
  }

  public void testCreateModuleManager() {
    EclipsePluginModule module = new EclipsePluginModule();
    GuiceToolsModule toolsModule = new EclipseGuiceToolsModule();
    new EclipseGuicePlugin(module, toolsModule)
        .getModuleManager(new FakeJavaManager());
  }

  public void testCreateBindingsEngine() {
    boolean calledMessenger = false;
    EclipsePluginModule module = new EclipsePluginModule();
    GuiceToolsModule toolsModule = new EclipseGuiceToolsModule();
    new EclipseGuicePlugin(module, toolsModule).getBindingsEngine(
        new FakeJavaElement(JavaElement.Type.FIELD), new FakeJavaManager());
  }
}
