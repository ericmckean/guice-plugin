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

package com.google.inject.tools.suite.module;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.tools.suite.JavaManager;
import com.google.inject.tools.suite.MockingGuiceToolsModule;
import com.google.inject.tools.suite.Fakes.FakeCodeRunner;
import com.google.inject.tools.suite.Fakes.FakeJavaManager;
import com.google.inject.tools.suite.SampleModuleScenario.WorkingModule;
import com.google.inject.tools.suite.module.ModuleContextRepresentation;
import com.google.inject.tools.suite.module.ModuleContextRepresentationImpl;
import com.google.inject.tools.suite.module.ModuleManager;
import com.google.inject.tools.suite.module.ModuleRepresentation;
import com.google.inject.tools.suite.module.ModuleRepresentationImpl;
import com.google.inject.tools.suite.module.ModuleContextRepresentation.ModuleInstanceRepresentation;

import junit.framework.TestCase;

import org.easymock.EasyMock;

import java.util.HashSet;
import java.util.Set;

/**
 * Unit test the ModuleManager implementation.
 * 
 * @author Darren Creutz (dcreutz@gmail.com)
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

    Injector injector =
        Guice.createInjector(new MockingGuiceToolsModule()
            .useRealModuleManager()
            .useCodeRunner(new FakeCodeRunner()));

    ModuleManagerImpl moduleManager = (ModuleManagerImpl)
        injector.getInstance(ModuleManagerFactory.class).create(project, false, false);
    moduleManager.update(true, true);
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

    Injector injector =
        Guice.createInjector(new MockingGuiceToolsModule()
            .useRealModuleManager()
            .useCodeRunner(new FakeCodeRunner()));

    ModuleManagerImpl moduleManager = (ModuleManagerImpl)
        injector.getInstance(ModuleManagerFactory.class).create(project, false, false);
    moduleManager.update(true, true);
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

    Set<String> moduleNames = new HashSet<String>();
    moduleNames.add(WorkingModule.class.getName());

    Injector injector =
        Guice.createInjector(new MockingGuiceToolsModule()
            .useRealModuleManager()
            .useCodeRunner(new FakeCodeRunner()));

    ModuleManagerImpl moduleManager = (ModuleManagerImpl)
        injector.getInstance(ModuleManagerFactory.class).create(project, false, false);
    moduleManager.addModule(WorkingModule.class.getName(), false);
    moduleManager.updateModules(true, true);
    moduleManager.update(true, true);
    assertTrue(moduleManager.getModules().size() == 1);
    ModuleRepresentation module = moduleManager.getModules().iterator().next();
    assertTrue(module.getName().equals(WorkingModule.class.getName()));
    moduleManager.removeModule(WorkingModule.class.getName());
    assertTrue(moduleManager.getModules().isEmpty());
    moduleManager.addModule(WorkingModule.class.getName(), false);
    assertTrue(moduleManager.getModules().size() == 1);
    module = moduleManager.getModules().iterator().next();
    assertTrue(module.getName().equals(WorkingModule.class.getName()));
  }

  /**
   * Test that the ModuleManager correctly initializes modules.
   */
  public void testInitializesModules() throws Exception {
    JavaManager project = EasyMock.createMock(JavaManager.class);

    Set<String> moduleNames = new HashSet<String>();
    moduleNames.add(WorkingModule.class.getName());
    Injector injector =
        Guice.createInjector(new MockingGuiceToolsModule()
            .useRealModuleManager()
            .useCodeRunner(new FakeCodeRunner()));

    ModuleManager moduleManager =
        injector.getInstance(ModuleManagerFactory.class).create(project, false, false);
    moduleManager.addModule(WorkingModule.class.getName(), false);
    moduleManager.update(true, true);
    assertTrue(moduleManager.getModules().size() == 1);
    ModuleRepresentation module = moduleManager.getModules().iterator().next();
    assertTrue(module.getName().equals(WorkingModule.class.getName()));
  }
}
