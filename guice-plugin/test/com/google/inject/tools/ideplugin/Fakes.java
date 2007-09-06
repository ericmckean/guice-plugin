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

package com.google.inject.tools.ideplugin;

import com.google.inject.binder.AnnotatedBindingBuilder;
import com.google.inject.tools.ideplugin.results.CodeLocationsResults;
import com.google.inject.tools.ideplugin.results.ResultsView;
import com.google.inject.tools.suite.JavaManager;
import com.google.inject.tools.suite.ProblemsHandler;

import org.easymock.EasyMock;

/**
 * Fake objects for use in testing.
 * 
 * @author Darren Creutz (dcreutz@gmail.com)
 */
public class Fakes {
  public static class FakeCodeLocationsResults extends CodeLocationsResults {
    public FakeCodeLocationsResults() {
      super("Mock Results", null);
    }
  }

  public static class FakeGuicePlugin extends GuicePlugin {
    public FakeGuicePlugin() {
      super(new MockingGuicePluginModule(), new MockingGuiceToolsModule());
    }

    public ResultsView getResultsView() {
      return EasyMock.createMock(ResultsView.class);
    }

    @Override
    public ModuleSelectionView getModuleSelectionView() {
      return EasyMock.createMock(ModuleSelectionView.class);
    }
  }

  public static class FakeJavaElement implements JavaElement {
    private Type type;
    private String name;
    private String className;

    public FakeJavaElement(Type type) {
      this.type = type;
      switch (type) {
        case PARAMETER:
          name = new String("TestMethod");
          className = new String("TestClass");
          break;
        case FIELD:
          name = new String("TestField");
          className = new String("TestClass");
          break;
        default:
          name = new String("TestField");
          className = new String("TestClass");
          break;
      }
    }

    public String getClassName() {
      return className;
    }

    public JavaManager getJavaProject() {
      return null;
    }

    public String getName() {
      return name;
    }

    public Type getType() {
      return type;
    }

    @Override
    public boolean equals(Object object) {
      if (object instanceof JavaElement) {
        JavaElement element = (JavaElement) object;
        return className.equals(element.getClassName())
            && name.equals(element.getName()) && type.equals(element.getType());
      } else {
        return false;
      }
    }

    @Override
    public int hashCode() {
      return 1;
    }

    public boolean isInjectionPoint() {
      return false;
    }
    
    public boolean isConcreteClass() {
      return false;
    }
    
    public String getAnnotations() {
      return null;
    }
  }

  public static class MockingGuiceToolsModule extends
      com.google.inject.tools.suite.MockingGuiceToolsModule {
    private boolean useRealProblemsHandler = false;

    public MockingGuiceToolsModule useRealProblemsHandler() {
      useRealProblemsHandler = true;
      return this;
    }

    @Override
    protected void bindProblemsHandler(
        AnnotatedBindingBuilder<ProblemsHandler> builder) {
      if (useRealProblemsHandler) {
        super.bindProblemsHandler(builder);
      } else {
        bindToMockInstance(builder, ProblemsHandler.class);
      }
    }
  }

  public static class FakeActionsHandler implements ActionsHandler {
    public void run(GotoCodeLocation action) {
    }

    public void run(GotoFile action) {
    }

    public void run(NullAction action) {
    }

    public void run(Action action) {
    }
  }
}
