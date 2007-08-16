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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
//import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.Set;
import org.easymock.EasyMock;
import com.google.inject.CreationException;
import com.google.inject.Inject;
import com.google.inject.binder.AnnotatedBindingBuilder;
import com.google.inject.spi.Message;
import com.google.inject.tools.JavaManager;
import com.google.inject.tools.Messenger;
import com.google.inject.tools.ProblemsHandler;
import com.google.inject.tools.code.CodeRunner;
import com.google.inject.tools.module.ModuleManager;
import com.google.inject.tools.module.ModulesNotifier;
import com.google.inject.tools.snippets.CodeProblem;
import com.google.inject.tools.snippets.CodeSnippet;
import com.google.inject.tools.snippets.CodeSnippetResult;

/**
 * Fake objects for use in testing.
 * 
 * @author Darren Creutz <dcreutz@gmail.com>
 */
public class Fakes {
  public static class FakeCreationException extends CreationException {
    /**
     * Automatically generated serial version UID.
     */
    private static final long serialVersionUID = -6889671178292449161L;
    private static Set<Message> messages = Collections.singleton(new Message("Mock Guice Message."));
    
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
  
  public static class MockingGuiceToolsModule extends GuiceToolsModule {
    private boolean useRealModuleManager = false;
    
    private ModuleManager moduleManager = null;
    private Messenger messenger = null;
    private CodeRunner codeRunner = null;
    private ProblemsHandler problemsHandler = null;
    private ModulesNotifier modulesListener = null;
    
    /**
     * Tell the module to use a real ModuleManager.
     */
    public MockingGuiceToolsModule useRealModuleManager() {
      useRealModuleManager = true;
      return this;
    }
    
    public MockingGuiceToolsModule useModuleManager(ModuleManager moduleManager) {
      this.moduleManager = moduleManager;
      return this;
    }
    
    public MockingGuiceToolsModule useModulesListener(ModulesNotifier modulesListener) {
      this.modulesListener = modulesListener;
      return this;
    }
    
    public MockingGuiceToolsModule useProblemsHandler(ProblemsHandler problemsHandler) {
      this.problemsHandler = problemsHandler;
      return this;
    }
    
    public MockingGuiceToolsModule useMessenger(Messenger messenger) {
      this.messenger = messenger;
      return this;
    }
    
    public MockingGuiceToolsModule useCodeRunner(CodeRunner codeRunner) {
      this.codeRunner = codeRunner;
      return this;
    }
    
    @Override
    protected void bindModuleManager(AnnotatedBindingBuilder<ModuleManager> builder) {
      if (moduleManager!=null) bindToInstance(builder, moduleManager);
      else if (useRealModuleManager) super.bindModuleManager(builder);
      else bindToMockInstance(builder, ModuleManager.class);
    }
    
    @Override
    protected void bindProblemsHandler(AnnotatedBindingBuilder<ProblemsHandler> builder) {
      if (problemsHandler!=null) bindToInstance(builder, problemsHandler);
      else bindToMockInstance(builder, ProblemsHandler.class);
    }
    
    @Override
    protected void bindMessenger(AnnotatedBindingBuilder<Messenger> builder) {
      if (messenger != null) bindToInstance(builder, messenger);
      else bindToMockInstance(builder, Messenger.class);
    }
    
    @Override
    protected void bindModulesListener(AnnotatedBindingBuilder<ModulesNotifier> builder) {
      if (modulesListener != null) bindToInstance(builder, modulesListener);
      else bindToMockInstance(builder, ModulesNotifier.class);
    }
    
    @Override
    protected void bindCodeRunner(AnnotatedBindingBuilder<CodeRunnerFactory> builder) {
      if (codeRunner != null) builder.toInstance(new CodeRunnerInstanceFactory(codeRunner));
      else builder.to(CodeRunnerMockFactory.class);
    }
    
    @SuppressWarnings({"unchecked"})
    protected <T> void bindToMockInstance(AnnotatedBindingBuilder<T> builder, Class<T> theClass) {
      builder.toInstance(new ProxyMock<T>(theClass).getInstance());
    }
    
    @SuppressWarnings({"unchecked"})
    protected <T> void bindToInstance(AnnotatedBindingBuilder<T> builder,T instance) {
      builder.toInstance(instance);
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
    
    public static class ProxyMock<T> implements InvocationHandler {
      private final Class<T> theClass;
      public ProxyMock(Class<T> theClass) {
        this.theClass = theClass;
      }
      @SuppressWarnings({"unchecked"})
      public T getInstance() {
        //return (T)Proxy.newProxyInstance(theClass.getClassLoader(), 
        //    theClass.getInterfaces(), this);
        return EasyMock.createMock(theClass);
      }
      public Object invoke(Object proxy, Method method, Object[] args) {
        try {
          return method.getReturnType().newInstance();
        } catch (Exception e) {
          return null;
        }
      }
    }
    
    public static class CodeRunnerInstanceFactory implements CodeRunnerFactory {
      private final CodeRunner instance;
      public CodeRunnerInstanceFactory(CodeRunner instance) {
        this.instance = instance;
      }
      public CodeRunner create(JavaManager project) {
        return instance;
      }
    }
    
    public static class CodeRunnerMockFactory extends CodeRunnerInstanceFactory {
      @Inject
      public CodeRunnerMockFactory() {
        super(new ProxyMock<CodeRunner>(CodeRunner.class).getInstance());
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
        super(Collections.<CodeProblem>emptySet());
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
  
  public static class FakeCodeRunner implements CodeRunner {
    public void addListener(CodeRunListener listener) {}
    public Messenger getMessenger() {
      return null;
    }
    public boolean isCancelled() {
      return false;
    }
    public boolean isDone() {
      return false;
    }
    public boolean isDone(Runnable runnable) {
      return false;
    }
    public void kill() {}
    public void kill(Runnable runnable) {}
    public void notifyResult(Runnable runnable, CodeSnippetResult result) {}
    public void queue(Runnable runnable) {}
    public void run(String label, boolean backgroundAutomatically) {}
    public void run(String label) {}
    public void waitFor() {}
    public void waitFor(Runnable runnable) {}
  }
}
