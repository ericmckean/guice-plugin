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
import java.lang.reflect.Proxy;

import org.easymock.EasyMock;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.tools.ideplugin.JavaProject;
import com.google.inject.tools.ideplugin.Messenger;
import com.google.inject.tools.ideplugin.ActionsHandler;
import com.google.inject.tools.ideplugin.GuicePluginModule;
import com.google.inject.tools.ideplugin.ProgressHandler;
import com.google.inject.tools.ideplugin.module.ModuleManager;
import com.google.inject.tools.ideplugin.module.ModuleSelectionView;
import com.google.inject.tools.ideplugin.module.ModulesListener;
import com.google.inject.tools.ideplugin.problem.ProblemsHandler;
import com.google.inject.tools.ideplugin.results.ResultsHandler;
import com.google.inject.tools.ideplugin.results.ResultsView;
import com.google.inject.tools.ideplugin.code.CodeRunner;

/**
 * Guice {@link com.google.inject.Module} that mocks the plugin dependencies.
 * 
 * @author Darren Creutz <dcreutz@gmail.com>
 */
public class MockGuicePluginModule extends GuicePluginModule {
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
	public MockGuicePluginModule() {
	}
	
	/**
	 * Tell the module to use a real ModuleManager.
	 */
	public MockGuicePluginModule useRealModuleManager() {
		useRealModuleManager = true;
		return this;
	}
	
	/**
	 * Tell the module to use a real ResultsHandler.
	 */
	public MockGuicePluginModule useRealResultsHandler() {
		useRealResultsHandler = true;
		return this;
	}
	
	/**
	 * Tell the module to use a real ProblemsHandler.
	 */
	public MockGuicePluginModule useRealProblemsHandler() {
		useRealProblemsHandler = true;
		return this;
	}
	
	public MockGuicePluginModule useRealBindingsEngine() {
		useRealBindingsEngine = true;
		return this;
	}
  
  public MockGuicePluginModule useModuleManager(ModuleManager moduleManager) {
    this.moduleManager = moduleManager;
    return this;
  }
  
  public MockGuicePluginModule useResultsHandler(ResultsHandler resultsHandler) {
    this.resultsHandler = resultsHandler;
    return this;
  }
  
  public MockGuicePluginModule useProblemsHandler(ProblemsHandler problemsHandler) {
    this.problemsHandler = problemsHandler;
    return this;
  }
  
  public MockGuicePluginModule useResultsView(ResultsView resultsView) {
    this.resultsView = resultsView;
    return this;
  }
  
  public MockGuicePluginModule useModuleSelectionView(ModuleSelectionView moduleSelectionView) {
    this.moduleSelectionView = moduleSelectionView;
    return this;
  }
  
  public MockGuicePluginModule useActionsHandler(ActionsHandler actionsHandler) {
    this.actionsHandler = actionsHandler;
    return this;
  }
  
  public MockGuicePluginModule useMessenger(Messenger messenger) {
    this.messenger = messenger;
    return this;
  }
  
  public MockGuicePluginModule useCodeRunner(CodeRunner codeRunner) {
    this.codeRunner = codeRunner;
    return this;
  }
  
  public MockGuicePluginModule useModulesListener(ModulesListener modulesListener) {
    this.modulesListener = modulesListener;
    return this;
  }
  
  public MockGuicePluginModule useProgressHandler(ProgressHandler progressHandler) {
    this.progressHandler = progressHandler;
    return this;
  }
	
	@Override
	protected void bindBindingsEngine() {
		if (useRealBindingsEngine) super.bindBindingsEngine();
    else bindToMockInstance(BindingsEngineFactory.class);
	}
	
	/**
	 * (non-Javadoc)
	 * @see com.google.inject.tools.ideplugin.GuicePluginModule#bindModuleManager()
	 */
	@Override
	protected void bindModuleManager() {
    if (moduleManager!=null) bindToInstance(ModuleManager.class,moduleManager);
    else if (useRealModuleManager) super.bindModuleManager();
    else bindToMockInstance(ModuleManager.class);
	}
	
	/**
	 * (non-Javadoc)
	 * @see com.google.inject.tools.ideplugin.GuicePluginModule#bindResultsHandler()
	 */
	@Override
	protected void bindResultsHandler() {
    if (resultsHandler!=null) bindToInstance(ResultsHandler.class,resultsHandler);
    else if (useRealResultsHandler) super.bindResultsHandler();
    else bindToMockInstance(ResultsHandler.class);
	}
	
	@Override
	protected void bindProblemsHandler() {
    if (problemsHandler!=null) bindToInstance(ProblemsHandler.class,problemsHandler);
    else if (useRealProblemsHandler) super.bindProblemsHandler();
    else bindToMockInstance(ProblemsHandler.class);
	}
	
	/**
	 * (non-Javadoc)
	 * @see com.google.inject.tools.ideplugin.GuicePluginModule#bindResultsView()
	 */
	@Override
	protected void bindResultsView() {
    if (resultsView != null) bindToInstance(ResultsView.class,resultsView);
    else bindToMockInstance(ResultsView.class);
	}
	
	/**
	 * (non-Javadoc)
	 * @see com.google.inject.tools.ideplugin.GuicePluginModule#bindModuleSelectionView()
	 */
	@Override
	protected void bindModuleSelectionView() {
    if (moduleSelectionView != null) bindToInstance(ModuleSelectionView.class,moduleSelectionView);
    else bindToMockInstance(ModuleSelectionView.class);
	}
	
	/**
	 * (non-Javadoc)
	 * @see com.google.inject.tools.ideplugin.GuicePluginModule#bindMessenger()
	 */
	@Override
	protected void bindMessenger() {
    if (messenger != null) bindToInstance(Messenger.class,messenger);
    else bindToMockInstance(Messenger.class);
	}
	
	/**
	 * (non-Javadoc)
	 * @see com.google.inject.tools.ideplugin.GuicePluginModule#bindActionsHandler()
	 */
	@Override
	protected void bindActionsHandler() {
    if (actionsHandler != null) bindToInstance(ActionsHandler.class,actionsHandler);
    else bindToMockInstance(ActionsHandler.class);
	}
	
	/**
	 * (non-Javadoc)
	 * @see com.google.inject.tools.ideplugin.GuicePluginModule#bindModulesListener()
	 */
	@Override
	protected void bindModulesListener() {
    if (modulesListener != null) bindToInstance(ModulesListener.class,modulesListener);
    else bindToMockInstance(ModulesListener.class);
	}
	
	@Override
	protected void bindCodeRunner() {
    if (codeRunner != null) bind(CodeRunnerFactory.class).toInstance(new CodeRunnerInstanceFactory(codeRunner));
    else bind(CodeRunnerFactory.class).to(CodeRunnerMockFactory.class);
	}
  
  @Override
  protected void bindProgressHandler() {
    if (progressHandler != null) bindToInstance(ProgressHandler.class,progressHandler);
    else bindToMockInstance(ProgressHandler.class);
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
	private <T> void bindToMockInstance(Class<T> theClass) {
		bind(theClass).toProvider(new MockFactory<T>(theClass));
  }
    
  @SuppressWarnings({"unchecked"})
  private void bindToInstance(Class theClass,Object object) {
    bind(theClass).toInstance(object);
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
      return (CodeRunner)Proxy.newProxyInstance(CodeRunner.class.getClassLoader(), CodeRunner.class.getInterfaces(), new ProxyHandler<CodeRunner>());
    }
  }
}