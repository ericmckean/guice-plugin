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

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.tools.JavaManager;
import com.google.inject.tools.MockingGuiceToolsModule;
import com.google.inject.tools.Fakes.FakeCodeRunner;
import com.google.inject.tools.Fakes.FakeJavaManager;
import com.google.inject.tools.GuiceToolsModule.ModuleManagerFactory;
import com.google.inject.tools.SampleModuleScenario.WorkingModule;
import com.google.inject.tools.module.ModuleContextRepresentation;
import com.google.inject.tools.module.ModuleContextRepresentationImpl;
import com.google.inject.tools.module.ModuleManager;
import com.google.inject.tools.module.ModuleRepresentation;
import com.google.inject.tools.module.ModuleRepresentationImpl;
import com.google.inject.tools.module.ModulesSource;
import com.google.inject.tools.module.ModuleContextRepresentation.ModuleInstanceRepresentation;

import junit.framework.TestCase;

import org.easymock.EasyMock;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Unit test the ModuleManager implementation.
 * 
 * @author Darren Creutz <dcreutz@gmail.com>
 */
public class ModuleManagerTest extends TestCase {
  /**
   * Test that adding and removing module contexts works as expected.
   */
  public void testAddRemoveModuleContexts() throws Throwable {
    JavaManager project = new FakeJavaManager();
    ModuleInstanceRepresentation workingModuleInstance =
        new ModuleInstanceRepresentation("WorkingModule");
    ModuleInstanceRepresentation brokenModuleInstance =
        new ModuleInstanceRepresentation("BrokenModule");
    ModuleContextRepresentation workingModuleContext =
        new ModuleContextRepresentationImpl("Working Module Context")
            .add(workingModuleInstance);
    ModuleContextRepresentation brokenModuleContext =
        new ModuleContextRepresentationImpl("Broken Module Context")
            .add(brokenModuleInstance);
    ModuleContextRepresentation emptyModuleContext =
        new ModuleContextRepresentationImpl("Empty Module Context");

    ModulesSource modulesListener = EasyMock.createMock(ModulesSource.class);
    EasyMock.expect(modulesListener.getModules(project)).andReturn(
        Collections.<String> emptySet());
    EasyMock.replay(modulesListener);
    Injector injector =
        Guice.createInjector(new MockingGuiceToolsModule()
            .useRealModuleManager().useModulesListener(modulesListener)
            .useCodeRunner(new FakeCodeRunner()));

    ModuleManager moduleManager =
        injector.getInstance(ModuleManagerFactory.class).create(project);
    moduleManager.waitForInitialization();
    moduleManager.findNewContexts(true, true);
    moduleManager.updateModules(true, true);
    moduleManager.addModuleContext(workingModuleContext, true);
    assertTrue(moduleManager.getModuleContexts().contains(workingModuleContext));
    assertTrue(moduleManager.getModuleContexts().size() == 1);
    moduleManager.addModuleContext(emptyModuleContext, true);
    assertTrue(moduleManager.getModuleContexts().contains(workingModuleContext));
    assertTrue(moduleManager.getModuleContexts().contains(emptyModuleContext));
    assertTrue(moduleManager.getModuleContexts().size() == 2);
    moduleManager.removeModuleContext(workingModuleContext);
    assertTrue(moduleManager.getModuleContexts().contains(emptyModuleContext));
    assertTrue(moduleManager.getModuleContexts().size() == 1);
    moduleManager.addModuleContext(brokenModuleContext, true);
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
  public void testAddRemoveModules() throws Exception {
    JavaManager project = EasyMock.createMock(JavaManager.class);
    ModuleRepresentation workingModule =
        new ModuleRepresentationImpl("WorkingModule");
    ModuleRepresentation brokenModule =
        new ModuleRepresentationImpl("BrokenModule");
    ModuleRepresentation moduleWithArguments =
        new ModuleRepresentationImpl("ModuleWithArguments");

    ModulesSource modulesListener = EasyMock.createMock(ModulesSource.class);
    EasyMock.expect(modulesListener.getModules(project)).andReturn(
        Collections.<String> emptySet());
    EasyMock.replay(modulesListener);

    Injector injector =
        Guice.createInjector(new MockingGuiceToolsModule()
            .useRealModuleManager().useModulesListener(modulesListener)
            .useCodeRunner(new FakeCodeRunner()));

    ModuleManager moduleManager =
        injector.getInstance(ModuleManagerFactory.class).create(project);
    moduleManager.waitForInitialization();
    moduleManager.updateModules(true, true);
    moduleManager.addModule(workingModule, false);
    assertTrue(moduleManager.getModules().contains(workingModule));
    assertTrue(moduleManager.getModules().size() == 1);
    moduleManager.addModule(moduleWithArguments, false);
    assertTrue(moduleManager.getModules().contains(workingModule));
    assertTrue(moduleManager.getModules().contains(moduleWithArguments));
    assertTrue(moduleManager.getModules().size() == 2);
    moduleManager.removeModule(workingModule);
    assertTrue(moduleManager.getModules().contains(moduleWithArguments));
    assertTrue(moduleManager.getModules().size() == 1);
    moduleManager.addModule(brokenModule, false);
    assertTrue(moduleManager.getModules().contains(moduleWithArguments));
    assertTrue(moduleManager.getModules().contains(brokenModule));
    assertTrue(moduleManager.getModules().size() == 2);
    moduleManager.clearModules();
    assertTrue(moduleManager.getModules().isEmpty());
  }

  /**
   * Test that the ModuleManager correctly adds and removes by name.
   */
  public void testAddRemoveByName() throws Exception {
    JavaManager project = EasyMock.createMock(JavaManager.class);

    ModulesSource modulesListener = EasyMock.createMock(ModulesSource.class);
    Set<String> moduleNames = new HashSet<String>();
    moduleNames.add(WorkingModule.class.getName());
    EasyMock.expect(modulesListener.getModules(project)).andReturn(moduleNames);
    EasyMock.replay(modulesListener);

    Injector injector =
        Guice.createInjector(new MockingGuiceToolsModule()
            .useRealModuleManager().useModulesListener(modulesListener)
            .useCodeRunner(new FakeCodeRunner()));

    ModuleManager moduleManager =
        injector.getInstance(ModuleManagerFactory.class).create(project);
    moduleManager.waitForInitialization();
    moduleManager.findNewContexts(true, true);
    moduleManager.updateModules(true, true);
    assertTrue(moduleManager.getModules().size() == 1);
    ModuleRepresentation module = moduleManager.getModules().iterator().next();
    assertTrue(module.getName().equals(WorkingModule.class.getName()));
    moduleManager.removeModule(WorkingModule.class.getName());
    assertTrue(moduleManager.getModules().isEmpty());
    moduleManager.addModule(WorkingModule.class.getName(), false);
    assertTrue(moduleManager.getModules().size() == 1);
    module = moduleManager.getModules().iterator().next();
    assertTrue(module.getName().equals(WorkingModule.class.getName()));
    EasyMock.verify(modulesListener);
  }

  /**
   * Test that the ModuleManager correctly initializes modules.
   */
  public void testInitializesModules() throws Exception {
    JavaManager project = EasyMock.createMock(JavaManager.class);

    ModulesSource modulesListener = EasyMock.createMock(ModulesSource.class);
    Set<String> moduleNames = new HashSet<String>();
    moduleNames.add(WorkingModule.class.getName());
    EasyMock.expect(modulesListener.getModules(project)).andReturn(moduleNames);
    EasyMock.replay(modulesListener);

    Injector injector =
        Guice.createInjector(new MockingGuiceToolsModule()
            .useRealModuleManager().useModulesListener(modulesListener)
            .useCodeRunner(new FakeCodeRunner()));

    ModuleManager moduleManager =
        injector.getInstance(ModuleManagerFactory.class).create(project);
    moduleManager.waitForInitialization();
    moduleManager.findNewContexts(true, true);
    moduleManager.updateModules(true, true);
    assertTrue(moduleManager.getModules().size() == 1);
    ModuleRepresentation module = moduleManager.getModules().iterator().next();
    assertTrue(module.getName().equals(WorkingModule.class.getName()));
    EasyMock.verify(modulesListener);
  }
}
