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

package com.google.inject.tools.suite;

import java.net.URL;
import java.util.Set;

import junit.framework.TestCase;

import com.google.inject.Guice;
import com.google.inject.binder.AnnotatedBindingBuilder;
import com.google.inject.tools.suite.SampleModuleScenario.MockInjectedInterface2;
import com.google.inject.tools.suite.SampleModuleScenario.Red;
import com.google.inject.tools.suite.SampleModuleScenario.RedService;
import com.google.inject.tools.suite.SampleModuleScenario.Service;
import com.google.inject.tools.suite.SampleModuleScenario.WorkingModule;
import com.google.inject.tools.suite.SampleModuleScenario.WorkingModule2;
import com.google.inject.tools.suite.module.ModuleContextRepresentation;
import com.google.inject.tools.suite.module.ModuleManager;
import com.google.inject.tools.suite.snippets.BindingCodeLocation;
import com.google.inject.tools.suite.snippets.CodeLocation;
import com.google.inject.tools.suite.snippets.ModuleContextSnippet;

/**
 * Test that the tools suite works as advertised.
 * 
 * @author Darren Creutz (dcreutz@gmail.com)
 */
public class SampleToolsFrameworkUseCase extends TestCase {
  static class MyToolsModule extends GuiceToolsModule {
    @Override
    protected void bindJavaManager(AnnotatedBindingBuilder<JavaManager> bindJavaManager) {
      bindJavaManager.to(MyJavaManager.class);
    }
  }
  
  static class MyJavaManager extends DefaultJavaManager {
    private static final URL SNIPPETURL = 
      ModuleContextSnippet.class.getProtectionDomain().getCodeSource().getLocation();
    private static final URL CODEURL =
      SampleModuleScenario.class.getProtectionDomain().getCodeSource().getLocation();

    private static String SNIPPETCLASSPATH = createClasspath(SNIPPETURL);
    private static String CODECLASSPATH = createClasspath(CODEURL);
    
    private static String createClasspath(URL url) {
      try {
        String location = url.toURI().getPath();
        return location.substring(location.indexOf('/'));
      } catch (Throwable t) {
        return null;
      }
    }
    
    @Override
    public String getProjectClasspath() {
      return CODECLASSPATH;
    }
    
    @Override
    public String getSnippetsClasspath() {
      return SNIPPETCLASSPATH;
    }
    
    @Override
    public String getGuiceClasspath() {
      String base = CODECLASSPATH.substring(0, CODECLASSPATH.lastIndexOf("bin")) + "lib/Guice/";
      String guice = base + "guice_r350+dcreutz1.jar";
      String asm = base + "asm-2.2.3.jar";
      String cglib = base + "cglib-2.2_beta1.jar";
      String aop = base + "aopalliance.jar";
      return guice + ":" + asm + ":" + cglib + ":" + aop;
    }
  }
  
  public void testToolsFramework() {
    ModuleManager moduleManager = 
      Guice.createInjector(new MyToolsModule()).getInstance(ModuleManager.class);
    
    moduleManager.createModuleContext("My Context");
    moduleManager.addToModuleContext("My Context", WorkingModule.class.getName());
    moduleManager.addToModuleContext("My Context", WorkingModule2.class.getName());
    
    moduleManager.update();
    
    assertTrue(moduleManager.getModuleContexts().size() == 1);
    assertTrue(moduleManager.getActiveModuleContexts().size() == 1);
    
    ModuleContextRepresentation context = moduleManager.getModuleContext("My Context");
    assertTrue(context.contains(WorkingModule.class.getName()));
    Set<CodeLocation> locations = context.findLocations(Service.class.getName());
    assertTrue(locations.size() == 2);
    CodeLocation location = context.findLocation(Service.class.getName(), Red.class.getName());
    assertNotNull(location);
    assertTrue(location instanceof BindingCodeLocation);
    BindingCodeLocation bindingLocation = (BindingCodeLocation)location;
    assertTrue(bindingLocation.bindTo().equals(RedService.class.getName()));
    assertTrue(context.contains(WorkingModule2.class.getName()));
    Set<CodeLocation> locations2 = context.findLocations(MockInjectedInterface2.class.getName());
    assertTrue(locations2.size() == 1);
    assertTrue(locations2.iterator().next().getProblems().isEmpty());
  }
}
