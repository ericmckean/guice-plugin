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
import junit.framework.TestCase;

import java.net.URL;
import java.util.Set;

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
    @Override
    protected void bindMessenger(AnnotatedBindingBuilder<Messenger> bindMessenger) {
      bindMessenger.to(MyMessenger.class);
    }
  }
  
  static class MyMessenger extends DefaultMessenger {
    @Override
    public void logCodeRunnerMessage(String msg) {
    }
  }
  
  static class MyJavaManager extends DefaultJavaManager {
    private static final URL SNIPPETURL = 
      ModuleContextSnippet.class.getProtectionDomain().getCodeSource().getLocation();
    private static final URL CODEURL =
      SampleModuleScenario.class.getProtectionDomain().getCodeSource().getLocation();

    private static final String SNIPPETCLASSPATH = createClasspath(SNIPPETURL);
    private static final String CODECLASSPATH = createClasspath(CODEURL);

	private static final String PATH_SEPARATOR = System.getProperty("path.separator");

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
        final String base       = CODECLASSPATH.substring(0, CODECLASSPATH.lastIndexOf("bin")) + "lib/Guice/";
        final String guice      = base + "guice-snapshot20080909.jar";
        final String asm        = base + "asm-2.2.3.jar";
        final String cglib      = base + "cglib-2.2_beta1.jar";
        final String aop        = base + "aopalliance.jar";

        return new StringBuilder()
                    .append(guice).append(PATH_SEPARATOR)
                    .append(asm).append(PATH_SEPARATOR)
                    .append(cglib).append(PATH_SEPARATOR)
                    .append(aop)
                .toString();
    }
  }
  
  public void testToolsFramework() {
    ModuleManager moduleManager = 
      Guice.createInjector(new MyToolsModule()).getInstance(ModuleManager.class);
	  ModuleContextRepresentation mcontext = moduleManager.createModuleContext("My Context");
    mcontext.addModule(WorkingModule.class.getName());
    mcontext.addModule(WorkingModule2.class.getName());
    
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
    assertTrue(bindingLocation.bindTo().equals("class " + RedService.class.getName()));
    assertTrue(context.contains(WorkingModule2.class.getName()));
    Set<CodeLocation> locations2 = context.findLocations(MockInjectedInterface2.class.getName());
    assertTrue(locations2.size() == 1);
    assertTrue(locations2.iterator().next().getProblems().isEmpty());
  }
}
