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

package com.google.inject.tools;

import junit.framework.TestCase;
import org.easymock.EasyMock;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.tools.JavaManager;
import com.google.inject.tools.SampleModuleScenario.WorkingModule;
import com.google.inject.tools.Fakes.FakeCodeRunner;
import com.google.inject.tools.Fakes.MockingGuiceToolsModule;
import com.google.inject.tools.module.ModuleContextRepresentation;
import com.google.inject.tools.module.ModuleContextRepresentationImpl;
import com.google.inject.tools.module.ModuleManager;
import com.google.inject.tools.module.ModuleRepresentation;
import com.google.inject.tools.module.ModuleRepresentationImpl;
import com.google.inject.tools.module.ModulesNotifier;
import com.google.inject.tools.module.ModuleContextRepresentation.ModuleInstanceRepresentation;

import java.util.Collections;
import java.util.Set;
import java.util.HashSet;

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
    JavaManager project = EasyMock.createMock(JavaManager.class);
    ModuleInstanceRepresentation workingModuleInstance =
      new ModuleInstanceRepresentation("WorkingModule");
    ModuleInstanceRepresentation brokenModuleInstance =
      new ModuleInstanceRepresentation("BrokenModule");
    ModuleContextRepresentation workingModuleContext = 
      new ModuleContextRepresentationImpl("Working Module Context").add(workingModuleInstance);
    ModuleContextRepresentation brokenModuleContext = 
      new ModuleContextRepresentationImpl("Broken Module Context").add(brokenModuleInstance);
    ModuleContextRepresentation emptyModuleContext = 
      new ModuleContextRepresentationImpl("Empty Module Context");
    
    ModulesNotifier modulesListener = EasyMock.createMock(ModulesNotifier.class);
    EasyMock.expect(modulesListener.findModules()).andReturn(Collections.<String>emptySet());
    modulesListener.projectChanged(project);
    EasyMock.replay(modulesListener);
    Injector injector = Guice.createInjector(
        new MockingGuiceToolsModule().useRealModuleManager()
        .useModulesListener(modulesListener)
        .useCodeRunner(new FakeCodeRunner()));
    
    ModuleManager moduleManager = injector.getInstance(ModuleManager.class);
    moduleManager.updateModules(project, true);
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
  public void testAddRemoveModules() {
    JavaManager project = EasyMock.createMock(JavaManager.class);
    ModuleRepresentation workingModule =
      new ModuleRepresentationImpl("WorkingModule");
    ModuleRepresentation brokenModule =
      new ModuleRepresentationImpl("BrokenModule");
    ModuleRepresentation moduleWithArguments =
      new ModuleRepresentationImpl("ModuleWithArguments");
    
    ModulesNotifier modulesListener = EasyMock.createMock(ModulesNotifier.class);
    EasyMock.expect(modulesListener.findModules()).andReturn(Collections.<String>emptySet());
    modulesListener.projectChanged(project);
    EasyMock.replay(modulesListener);
    
    Injector injector = Guice.createInjector(
        new MockingGuiceToolsModule().useRealModuleManager()
        .useModulesListener(modulesListener)
        .useCodeRunner(new FakeCodeRunner()));
    
    ModuleManager moduleManager = injector.getInstance(ModuleManager.class);
    moduleManager.updateModules(project, true);
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
  public void testAddRemoveByName() {
    JavaManager project = EasyMock.createMock(JavaManager.class);
    
    ModulesNotifier modulesListener = EasyMock.createMock(ModulesNotifier.class);
    Set<String> moduleNames = new HashSet<String>();
    moduleNames.add(WorkingModule.class.getName());
    modulesListener.projectChanged(project);
    EasyMock.expect(modulesListener.findModules()).andReturn(moduleNames);
    EasyMock.replay(modulesListener);
    
    Injector injector = Guice.createInjector(
        new MockingGuiceToolsModule().useRealModuleManager()
        .useModulesListener(modulesListener)
        .useCodeRunner(new FakeCodeRunner()));
    
    ModuleManager moduleManager = injector.getInstance(ModuleManager.class);
    moduleManager.updateModules(project, true);
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
  public void testInitializesModules() {
    JavaManager project = EasyMock.createMock(JavaManager.class);
    
    ModulesNotifier modulesListener = EasyMock.createMock(ModulesNotifier.class);
    Set<String> moduleNames = new HashSet<String>();
    moduleNames.add(WorkingModule.class.getName());
    modulesListener.projectChanged(project);
    EasyMock.expect(modulesListener.findModules()).andReturn(moduleNames);
    EasyMock.replay(modulesListener);
    
    Injector injector = Guice.createInjector(
        new MockingGuiceToolsModule().useRealModuleManager()
        .useModulesListener(modulesListener)
        .useCodeRunner(new FakeCodeRunner()));
    
    ModuleManager moduleManager = injector.getInstance(ModuleManager.class);
    moduleManager.updateModules(project, true);
    assertTrue(moduleManager.getModules().size() == 1);
    ModuleRepresentation module = moduleManager.getModules().iterator().next();
    assertTrue(module.getName().equals(WorkingModule.class.getName()));
    EasyMock.verify(modulesListener);
  }
}
