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

package com.google.inject.tools.ideplugin;

import junit.framework.TestCase;
import org.easymock.EasyMock;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.tools.ideplugin.module.ModuleInstanceRepresentation;
import com.google.inject.tools.ideplugin.module.ModuleManager;
import com.google.inject.tools.ideplugin.module.ModuleContextRepresentation;
import com.google.inject.tools.ideplugin.module.ModuleContextRepresentationImpl;
import com.google.inject.tools.ideplugin.module.ModuleRepresentation;
import com.google.inject.tools.ideplugin.module.ModuleRepresentationImpl;
import com.google.inject.tools.ideplugin.module.ModulesListener;
import com.google.inject.tools.ideplugin.test.MockGuicePluginModule;
import java.util.Set;
import java.util.HashSet;

/**
 * Unit test the ModuleManager implementation.
 * 
 * @author Darren Creutz <dcreutz@gmail.com>
 */
public class ModuleManagerTest extends TestCase {
  private static ModuleRepresentation workingModule =
    new ModuleRepresentationImpl("WorkingModule");
  private static ModuleRepresentation brokenModule =
    new ModuleRepresentationImpl("BrokenModule");
  private static ModuleRepresentation moduleWithArguments =
    new ModuleRepresentationImpl("ModuleWithArguments");
  
  private static ModuleInstanceRepresentation workingModuleInstance =
    new ModuleInstanceRepresentation("WorkingModule");
  private static ModuleInstanceRepresentation brokenModuleInstance =
    new ModuleInstanceRepresentation("BrokenModule");
  
  private static ModuleContextRepresentation workingModuleContext = 
    new ModuleContextRepresentationImpl("Working Module Context").add(workingModuleInstance);
  private static ModuleContextRepresentation brokenModuleContext = 
    new ModuleContextRepresentationImpl("Broken Module Context").add(brokenModuleInstance);
  private static ModuleContextRepresentation emptyModuleContext = 
    new ModuleContextRepresentationImpl("Empty Module Context");
  
  private static JavaProject project = EasyMock.createMock(JavaProject.class);
  
  /**
   * Test that adding and removing module contexts works as expected.
   */
  public void testAddRemoveModuleContexts() {
    Injector injector = Guice.createInjector(
        new MockGuicePluginModule().useRealModuleManager()
        .useModulesListener(EasyMock.createMock(ModulesListener.class)));
    ModulesListener modulesListener = injector.getInstance(ModulesListener.class);
    EasyMock.expect(modulesListener.findModules()).andReturn(new HashSet<String>());
    modulesListener.projectChanged(project);
    EasyMock.replay(modulesListener);
    ModuleManager moduleManager = injector.getInstance(ModuleManager.class);
    moduleManager.updateModules(project, true);
    moduleManager.addModuleContext(workingModuleContext);
    assertTrue(moduleManager.getModuleContexts().contains(workingModuleContext));
    assertTrue(moduleManager.getModuleContexts().size() == 1);
    moduleManager.addModuleContext(emptyModuleContext);
    assertTrue(moduleManager.getModuleContexts().contains(workingModuleContext));
    assertTrue(moduleManager.getModuleContexts().contains(emptyModuleContext));
    assertTrue(moduleManager.getModuleContexts().size() == 2);
    moduleManager.removeModuleContext(workingModuleContext);
    assertTrue(moduleManager.getModuleContexts().contains(emptyModuleContext));
    assertTrue(moduleManager.getModuleContexts().size() == 1);
    moduleManager.addModuleContext(brokenModuleContext);
    assertTrue(moduleManager.getModuleContexts().contains(emptyModuleContext));
    assertTrue(moduleManager.getModuleContexts().contains(brokenModuleContext));
    assertTrue(moduleManager.getModuleContexts().size() == 2);
    moduleManager.clearModuleContexts();
    assertTrue(moduleManager.getModuleContexts().isEmpty());
    EasyMock.verify(modulesListener);
  }
  
  /**
   * Test that adding and removing modules works as expected.
   */
  public void testAddRemoveModules() {
    Injector injector = Guice.createInjector(
        new MockGuicePluginModule().useRealModuleManager()
        .useModulesListener(EasyMock.createMock(ModulesListener.class)));
    ModulesListener modulesListener = injector.getInstance(ModulesListener.class);
    EasyMock.expect(modulesListener.findModules()).andReturn(new HashSet<String>());
    modulesListener.projectChanged(project);
    EasyMock.replay(modulesListener);
    ModuleManager moduleManager = injector.getInstance(ModuleManager.class);
    moduleManager.updateModules(project, true);
    moduleManager.addModule(workingModule);
    assertTrue(moduleManager.getModules().contains(workingModule));
    assertTrue(moduleManager.getModules().size() == 1);
    moduleManager.addModule(moduleWithArguments);
    assertTrue(moduleManager.getModules().contains(workingModule));
    assertTrue(moduleManager.getModules().contains(moduleWithArguments));
    assertTrue(moduleManager.getModules().size() == 2);
    moduleManager.removeModule(workingModule);
    assertTrue(moduleManager.getModules().contains(moduleWithArguments));
    assertTrue(moduleManager.getModules().size() == 1);
    moduleManager.addModule(brokenModule);
    assertTrue(moduleManager.getModules().contains(moduleWithArguments));
    assertTrue(moduleManager.getModules().contains(brokenModule));
    assertTrue(moduleManager.getModules().size() == 2);
    moduleManager.clearModules();
    assertTrue(moduleManager.getModules().isEmpty());
  }
  
  /**
   * Test that the ModuleManager correctly adds and removes by name.
   */
  public void testAddRemoveByName() {
    Injector injector = Guice.createInjector(
        new MockGuicePluginModule().useRealModuleManager()
        .useModulesListener(EasyMock.createMock(ModulesListener.class)));
    ModulesListener modulesListener = injector.getInstance(ModulesListener.class);
    Set<String> moduleNames = new HashSet<String>();
    moduleNames.add("com.google.inject.tools.ideplugin.test.WorkingModule");
    modulesListener.projectChanged(project);
    EasyMock.expect(modulesListener.findModules()).andReturn(moduleNames);
    EasyMock.replay(modulesListener);
    ModuleManager moduleManager = injector.getInstance(ModuleManager.class);
    moduleManager.updateModules(project, true);
    assertTrue(moduleManager.getModules().size() == 1);
    ModuleRepresentation module = moduleManager.getModules().iterator().next();
    assertTrue(module.getName().equals("com.google.inject.tools.ideplugin.test.WorkingModule"));
    moduleManager.removeModule("com.google.inject.tools.ideplugin.test.WorkingModule");
    assertTrue(moduleManager.getModules().isEmpty());
    moduleManager.addModule("com.google.inject.tools.ideplugin.test.WorkingModule");
    assertTrue(moduleManager.getModules().size() == 1);
    module = moduleManager.getModules().iterator().next();
    assertTrue(module.getName().equals("com.google.inject.tools.ideplugin.test.WorkingModule"));
    EasyMock.verify(modulesListener);
  }
  
  /**
   * Test that the ModuleManager correctly initializes modules.
   */
  public void testInitializesModules() {
    Injector injector = Guice.createInjector(
        new MockGuicePluginModule().useRealModuleManager()
        .useModulesListener(EasyMock.createMock(ModulesListener.class)));
    ModulesListener modulesListener = injector.getInstance(ModulesListener.class);
    Set<String> moduleNames = new HashSet<String>();
    moduleNames.add("com.google.inject.tools.ideplugin.test.WorkingModule");
    modulesListener.projectChanged(project);
    EasyMock.expect(modulesListener.findModules()).andReturn(moduleNames);
    EasyMock.replay(modulesListener);
    ModuleManager moduleManager = injector.getInstance(ModuleManager.class);
    moduleManager.updateModules(project, true);
    assertTrue(moduleManager.getModules().size() == 1);
    ModuleRepresentation module = moduleManager.getModules().iterator().next();
    assertTrue(module.getName().equals("com.google.inject.tools.ideplugin.test.WorkingModule"));
    EasyMock.verify(modulesListener);
  }
}
