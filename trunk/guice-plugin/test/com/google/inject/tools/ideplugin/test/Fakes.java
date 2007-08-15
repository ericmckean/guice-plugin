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

package com.google.inject.tools.ideplugin.test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashSet;

import org.easymock.EasyMock;

import com.google.inject.CreationException;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.binder.AnnotatedBindingBuilder;
import com.google.inject.spi.Message;
import com.google.inject.tools.ideplugin.ActionsHandler;
import com.google.inject.tools.ideplugin.GuicePlugin;
import com.google.inject.tools.ideplugin.GuicePluginModule;
import com.google.inject.tools.ideplugin.JavaElement;
import com.google.inject.tools.ideplugin.JavaProject;
import com.google.inject.tools.ideplugin.Messenger;
import com.google.inject.tools.ideplugin.ProgressHandler;
import com.google.inject.tools.ideplugin.code.CodeRunner;
import com.google.inject.tools.ideplugin.module.ModuleManager;
import com.google.inject.tools.ideplugin.module.ModuleSelectionView;
import com.google.inject.tools.ideplugin.module.ModulesListener;
import com.google.inject.tools.ideplugin.problem.ProblemsHandler;
import com.google.inject.tools.ideplugin.results.CodeLocationsResults;
import com.google.inject.tools.ideplugin.results.ResultsHandler;
import com.google.inject.tools.ideplugin.results.ResultsView;
import com.google.inject.tools.ideplugin.snippets.CodeProblem;
import com.google.inject.tools.ideplugin.snippets.CodeSnippet;
import com.google.inject.tools.ideplugin.snippets.CodeSnippetResult;

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

  public static class FakeCreationException extends CreationException {
    /**
     * Automatically generated serial version UID.
     */
    private static final long serialVersionUID = -6889671178292449161L;
    private static HashSet<Message> messages = makeMessages();
    private static HashSet<Message> makeMessages() {
      HashSet<Message> collection = new HashSet<Message>();
      collection.add(new Message("Mock Guice Message."));
      return collection;
    }
    
    /**
     * Create the Mock object.
     */
    public FakeCreationException() {
      super(messages);
    }
    
    /**
     * (non-Javadoc)
     * @see java.lang.Throwable#toString()
     */
    @Override
    public String toString() {
      return "Mock Creation Exception.";
    }
  }
  
  public static class FakeGuicePlugin extends GuicePlugin {
    public FakeGuicePlugin() {
      super(new MockingGuicePluginModule());
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
    
    public JavaProject getJavaProject() {
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
  
  public static class MockingGuicePluginModule extends GuicePluginModule {
    private boolean useRealModuleManager = false;
    private boolean useRealResultsHandler = false;
    private boolean useRealProblemsHandler = false;
    private boolean useRealBindingsEngine = false;
    
    private ModuleManager moduleManager = null;
    private ResultsHandler resultsHandler = null;
    private ProblemsHandler problemsHandler = null;
    private ResultsView resultsView = null;
    private ModuleSelectionView moduleSelectionView = null;
    private ActionsHandler actionsHandler = null;
    private ModulesListener modulesListener = null;
    private Messenger messenger = null;
    private CodeRunner codeRunner = null;
    private ProgressHandler progressHandler = null;
    
    /**
     * Create a purely mocked module.
     */
    public MockingGuicePluginModule() {
    }
    
    /**
     * Tell the module to use a real ModuleManager.
     */
    public MockingGuicePluginModule useRealModuleManager() {
      useRealModuleManager = true;
      return this;
    }
    
    /**
     * Tell the module to use a real ResultsHandler.
     */
    public MockingGuicePluginModule useRealResultsHandler() {
      useRealResultsHandler = true;
      return this;
    }
    
    /**
     * Tell the module to use a real ProblemsHandler.
     */
    public MockingGuicePluginModule useRealProblemsHandler() {
      useRealProblemsHandler = true;
      return this;
    }
    
    public MockingGuicePluginModule useRealBindingsEngine() {
      useRealBindingsEngine = true;
      return this;
    }
    
    public MockingGuicePluginModule useModuleManager(ModuleManager moduleManager) {
      this.moduleManager = moduleManager;
      return this;
    }
    
    public MockingGuicePluginModule useResultsHandler(ResultsHandler resultsHandler) {
      this.resultsHandler = resultsHandler;
      return this;
    }
    
    public MockingGuicePluginModule useProblemsHandler(ProblemsHandler problemsHandler) {
      this.problemsHandler = problemsHandler;
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
    
    public MockingGuicePluginModule useMessenger(Messenger messenger) {
      this.messenger = messenger;
      return this;
    }
    
    public MockingGuicePluginModule useCodeRunner(CodeRunner codeRunner) {
      this.codeRunner = codeRunner;
      return this;
    }
    
    public MockingGuicePluginModule useModulesListener(ModulesListener modulesListener) {
      this.modulesListener = modulesListener;
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
    
    /**
     * (non-Javadoc)
     * @see com.google.inject.tools.ideplugin.GuicePluginModule#bindModuleManager()
     */
    @Override
    protected void bindModuleManager(AnnotatedBindingBuilder<ModuleManager> builder) {
      if (moduleManager!=null) bindToInstance(builder, moduleManager);
      else if (useRealModuleManager) super.bindModuleManager(builder);
      else bindToMockInstance(builder, ModuleManager.class);
    }
    
    /**
     * (non-Javadoc)
     * @see com.google.inject.tools.ideplugin.GuicePluginModule#bindResultsHandler()
     */
    @Override
    protected void bindResultsHandler(AnnotatedBindingBuilder<ResultsHandler> builder) {
      if (resultsHandler!=null) bindToInstance(builder, resultsHandler);
      else if (useRealResultsHandler) super.bindResultsHandler(builder);
      else bindToMockInstance(builder, ResultsHandler.class);
    }
    
    @Override
    protected void bindProblemsHandler(AnnotatedBindingBuilder<ProblemsHandler> builder) {
      if (problemsHandler!=null) bindToInstance(builder, problemsHandler);
      else if (useRealProblemsHandler) super.bindProblemsHandler(builder);
      else bindToMockInstance(builder, ProblemsHandler.class);
    }
    
    /**
     * (non-Javadoc)
     * @see com.google.inject.tools.ideplugin.GuicePluginModule#bindResultsView()
     */
    @Override
    protected void bindResultsView(AnnotatedBindingBuilder<ResultsView> builder) {
      if (resultsView != null) bindToInstance(builder, resultsView);
      else bindToMockInstance(builder, ResultsView.class);
    }
    
    /**
     * (non-Javadoc)
     * @see com.google.inject.tools.ideplugin.GuicePluginModule#bindModuleSelectionView()
     */
    @Override
    protected void bindModuleSelectionView(AnnotatedBindingBuilder<ModuleSelectionView> builder) {
      if (moduleSelectionView != null) bindToInstance(builder, moduleSelectionView);
      else bindToMockInstance(builder, ModuleSelectionView.class);
    }
    
    /**
     * (non-Javadoc)
     * @see com.google.inject.tools.ideplugin.GuicePluginModule#bindMessenger()
     */
    @Override
    protected void bindMessenger(AnnotatedBindingBuilder<Messenger> builder) {
      if (messenger != null) bindToInstance(builder, messenger);
      else bindToMockInstance(builder, Messenger.class);
    }
    
    /**
     * (non-Javadoc)
     * @see com.google.inject.tools.ideplugin.GuicePluginModule#bindActionsHandler()
     */
    @Override
    protected void bindActionsHandler(AnnotatedBindingBuilder<ActionsHandler> builder) {
      if (actionsHandler != null) bindToInstance(builder, actionsHandler);
      else bindToMockInstance(builder, ActionsHandler.class);
    }
    
    /**
     * (non-Javadoc)
     * @see com.google.inject.tools.ideplugin.GuicePluginModule#bindModulesListener()
     */
    @Override
    protected void bindModulesListener(AnnotatedBindingBuilder<ModulesListener> builder) {
      if (modulesListener != null) bindToInstance(builder, modulesListener);
      else bindToMockInstance(builder, ModulesListener.class);
    }
    
    @Override
    protected void bindCodeRunner(AnnotatedBindingBuilder<CodeRunnerFactory> builder) {
      if (codeRunner != null) builder.toInstance(new CodeRunnerInstanceFactory(codeRunner));
      else builder.to(CodeRunnerMockFactory.class);
    }
    
    @Override
    protected void bindProgressHandler(AnnotatedBindingBuilder<ProgressHandler> builder) {
      if (progressHandler != null) bindToInstance(builder, progressHandler);
      else bindToMockInstance(builder, ProgressHandler.class);
    }
    
    public static class ProxyException extends RuntimeException {
      private final Object proxy;
      private final Method method;
      public ProxyException(Object proxy, Method method, Object[] args) {
        this.proxy = proxy;
        this.method = method;
      }
      @Override
      public String toString() {
        return "Proxy exception: method " + method.toString() + " called on proxy " + proxy.toString();
      }
    }
    
    @SuppressWarnings({"unchecked"})
    private <T> void bindToMockInstance(AnnotatedBindingBuilder<T> builder, Class<T> theClass) {
      builder.toProvider(new MockFactory<T>(theClass));
    }
    
    @SuppressWarnings({"unchecked"})
    private <T> void bindToInstance(AnnotatedBindingBuilder<T> builder,T instance) {
      builder.toInstance(instance);
    }
    
    protected static class MockFactory<T> implements Provider<T> {
      private final Class<T> theClass;
      public MockFactory(Class<T> theClass) {
        this.theClass = theClass;
      }
      @SuppressWarnings({"unchecked"})
      public T get() {
        return EasyMock.createMock(theClass);
        //TODO: fix this??
        //return (T)Proxy.newProxyInstance(theClass.getClassLoader(), 
        //                                 theClass.getInterfaces(), 
        //                                 new ProxyHandler<T>());
      }
    }
    
    public static class ProxyHandler<T> implements InvocationHandler {
      public Object invoke(Object proxy, Method method, Object[] args) {
        return new ProxyException(proxy,method,args);
      }
    }
    
    public static class CodeRunnerInstanceFactory implements CodeRunnerFactory {
      private final CodeRunner instance;
      public CodeRunnerInstanceFactory(CodeRunner instance) {
        this.instance = instance;
      }
      public CodeRunner create(JavaProject project) {
        return instance;
      }
    }
    
    public static class CodeRunnerMockFactory implements CodeRunnerFactory {
      @Inject
      public CodeRunnerMockFactory() {}
      public CodeRunner create(JavaProject project) {
        return EasyMock.createMock(CodeRunner.class);
        //return (CodeRunner)Proxy.newProxyInstance(CodeRunner.class.getClassLoader(), CodeRunner.class.getInterfaces(), new ProxyHandler<CodeRunner>());
      }
    }
  }
  
  public static class TestSnippet extends CodeSnippet {
    public TestSnippet(int secsToTake) {
      super();
      try {
        Thread.sleep(secsToTake * 1000);
      } catch (Exception exception) {
        //do nothing
      }
    }
    
    public static class TestSnippetResult extends CodeSnippetResult {
      private final String blah = "blah";
      public TestSnippetResult() {
        super(new HashSet<CodeProblem>());
      }
      public String getBlah() {
        return blah;
      }
    }
    
    @Override
    public CodeSnippetResult getResult() {
      return new TestSnippetResult();
    }
    public static void main(String[] args) {
      int secsToTake;
      if (args.length > 0) secsToTake = Integer.valueOf(args[0]);
      else secsToTake = -1;
      new TestSnippet(secsToTake).printResult(System.out);
    }
  }
}
