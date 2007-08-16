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

import org.easymock.EasyMock;
import com.google.inject.binder.AnnotatedBindingBuilder;
import com.google.inject.tools.JavaManager;
import com.google.inject.tools.ProblemsHandler;
import com.google.inject.tools.ProgressHandler;
import com.google.inject.tools.Fakes.MockingGuiceToolsModule.ProxyMock;
import com.google.inject.tools.ideplugin.ActionsHandler;
import com.google.inject.tools.ideplugin.GuicePlugin;
import com.google.inject.tools.ideplugin.GuicePluginModule;
import com.google.inject.tools.ideplugin.JavaElement;
import com.google.inject.tools.ideplugin.results.CodeLocationsResults;
import com.google.inject.tools.ideplugin.results.ResultsHandler;
import com.google.inject.tools.ideplugin.results.ResultsView;
import com.google.inject.tools.ideplugin.module.ModuleSelectionView;

/**
 * Fake objects for use in testing.
 * 
 * @author Darren Creutz <dcreutz@gmail.com>
 */
public class Fakes {
  public static class FakeCodeLocationsResults extends CodeLocationsResults {
    /**
     * Create the Mock object.
     */
    public FakeCodeLocationsResults() {
      super("Mock Results",null);
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
    
    /**
     * Create a mock Java element of the given type.
     *
     * @param type the type
     */
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
    
    /**
     * (non-Javadoc)
     * @see com.google.inject.tools.ideplugin.JavaElement#getClassName()
     */
    public String getClassName() {
      return className;
    }
    
    public JavaManager getJavaProject() {
      return null;
    }
    
    /**
     * (non-Javadoc)
     * @see com.google.inject.tools.ideplugin.JavaElement#getName()
     */
    public String getName() {
      return name;
    }
    
    /**
     * (non-Javadoc)
     * @see com.google.inject.tools.ideplugin.JavaElement#getType()
     */
    public Type getType() {
      return type;
    }
    
    /**
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object object) {
      if (object instanceof JavaElement) {
        JavaElement element = (JavaElement)object;
        return className.equals(element.getClassName()) && name.equals(element.getName()) && type.equals(element.getType());
      } else return false;
    }
    
    @Override
    public int hashCode() {
      return 1;
    }
    
    /**
     * (non-Javadoc)
     * @see com.google.inject.tools.ideplugin.JavaElement#isInjectionPoint()
     */
    public boolean isInjectionPoint() {
      return false;
    }
  }
  
  public static class MockingGuiceToolsModule extends com.google.inject.tools.Fakes.MockingGuiceToolsModule {
    private boolean useRealProblemsHandler = false;
    
    /**
     * Tell the module to use a real ProblemsHandler.
     */
    public MockingGuiceToolsModule useRealProblemsHandler() {
      useRealProblemsHandler = true;
      return this;
    }
    
    @Override
    protected void bindProblemsHandler(AnnotatedBindingBuilder<ProblemsHandler> builder) {
      if (useRealProblemsHandler) super.bindProblemsHandler(builder);
      else bindToMockInstance(builder, ProblemsHandler.class);
    }
  }
  
  public static class MockingGuicePluginModule extends GuicePluginModule {
    private boolean useRealResultsHandler = false;
    private boolean useRealBindingsEngine = false;
    
    private ResultsHandler resultsHandler = null;
    private ResultsView resultsView = null;
    private ModuleSelectionView moduleSelectionView = null;
    private ActionsHandler actionsHandler = null;
    private ProgressHandler progressHandler = null;
    
    /**
     * Create a purely mocked module.
     */
    public MockingGuicePluginModule() {
    }
    
    /**
     * Tell the module to use a real ResultsHandler.
     */
    public MockingGuicePluginModule useRealResultsHandler() {
      useRealResultsHandler = true;
      return this;
    }
    
    public MockingGuicePluginModule useRealBindingsEngine() {
      useRealBindingsEngine = true;
      return this;
    }
    
    public MockingGuicePluginModule useResultsHandler(ResultsHandler resultsHandler) {
      this.resultsHandler = resultsHandler;
      return this;
    }
    
    public MockingGuicePluginModule useResultsView(ResultsView resultsView) {
      this.resultsView = resultsView;
      return this;
    }
    
    public MockingGuicePluginModule useModuleSelectionView(ModuleSelectionView moduleSelectionView) {
      this.moduleSelectionView = moduleSelectionView;
      return this;
    }
    
    public MockingGuicePluginModule useActionsHandler(ActionsHandler actionsHandler) {
      this.actionsHandler = actionsHandler;
      return this;
    }
    
    public MockingGuicePluginModule useProgressHandler(ProgressHandler progressHandler) {
      this.progressHandler = progressHandler;
      return this;
    }
    
    @Override
    protected void bindBindingsEngine(AnnotatedBindingBuilder<BindingsEngineFactory> builder) {
      if (useRealBindingsEngine) super.bindBindingsEngine(builder);
      else bindToMockInstance(builder, BindingsEngineFactory.class);
    }
    
    @Override
    protected void bindResultsHandler(AnnotatedBindingBuilder<ResultsHandler> builder) {
      if (resultsHandler!=null) bindToInstance(builder, resultsHandler);
      else if (useRealResultsHandler) super.bindResultsHandler(builder);
      else bindToMockInstance(builder, ResultsHandler.class);
    }
    
    @Override
    protected void bindResultsView(AnnotatedBindingBuilder<ResultsView> builder) {
      if (resultsView != null) bindToInstance(builder, resultsView);
      else bindToMockInstance(builder, ResultsView.class);
    }
    
    @Override
    protected void bindModuleSelectionView(AnnotatedBindingBuilder<ModuleSelectionView> builder) {
      if (moduleSelectionView != null) bindToInstance(builder, moduleSelectionView);
      else bindToMockInstance(builder, ModuleSelectionView.class);
    }
    
    @Override
    protected void bindActionsHandler(AnnotatedBindingBuilder<ActionsHandler> builder) {
      if (actionsHandler != null) bindToInstance(builder, actionsHandler);
      else bindToMockInstance(builder, ActionsHandler.class);
    }
    
    @Override
    protected void bindProgressHandler(AnnotatedBindingBuilder<ProgressHandler> builder) {
      if (progressHandler != null) bindToInstance(builder, progressHandler);
      else bindToMockInstance(builder, ProgressHandler.class);
    }
    
    @SuppressWarnings({"unchecked"})
    protected <T> void bindToMockInstance(AnnotatedBindingBuilder<T> builder, Class<T> theClass) {
      builder.toInstance(new ProxyMock<T>(theClass).getInstance());
    }
    
    @SuppressWarnings({"unchecked"})
    protected <T> void bindToInstance(AnnotatedBindingBuilder<T> builder,T instance) {
      builder.toInstance(instance);
    }
  }
  
  public static class FakeActionsHandler extends ActionsHandler {
    @Override
    public void run(GotoCodeLocation action) {}
    @Override
    public void run(GotoFile action) {}
  }
}
