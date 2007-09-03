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

import com.google.inject.Inject;
import com.google.inject.tools.suite.JavaManager;
import com.google.inject.tools.suite.Messenger;
import com.google.inject.tools.suite.ProblemsHandler;
import com.google.inject.tools.suite.GuiceToolsModule.CodeRunnerFactory;
import com.google.inject.tools.suite.code.CodeRunner;
import com.google.inject.tools.suite.module.ModuleContextRepresentation;
import com.google.inject.tools.suite.module.ModuleContextRepresentationImpl;
import com.google.inject.tools.suite.module.ModuleManager;
import com.google.inject.tools.suite.module.ModuleRepresentation;
import com.google.inject.tools.suite.module.ModuleRepresentationImpl;
import com.google.inject.tools.suite.module.ModulesSource;
import com.google.inject.tools.suite.module.ModuleContextRepresentation.ModuleInstanceRepresentation;
import com.google.inject.tools.suite.snippets.CodeSnippetResult;

import java.util.HashSet;
import java.util.Set;

/**
 * Standard implementation of the {@link ModuleManager}.
 * 
 * @author Darren Creutz (dcreutz@gmail.com)
 */
class ModuleManagerImpl implements ModuleManager,
    CodeRunner.CodeRunListener {
  private final ModulesSource modulesListener;
  private final ProblemsHandler problemsHandler;
  private final CodeRunnerFactory codeRunnerFactory;
  private final Messenger messenger;
  private final HashSet<ModuleRepresentation> modules;
  private final HashSet<ModuleContextRepresentation> moduleContexts;
  private final HashSet<ModuleContextRepresentation> activeModuleContexts;
  private final JavaManager javaManager;
  private boolean runAutomatically;
  private boolean activateByDefault;
  private boolean initing;
  private final InitThread initThread;

  /**
   * Create a ModuleManagerImpl. This should be done by injection.
   */
  @Inject
  public ModuleManagerImpl(ModulesSource modulesListener,
      ProblemsHandler problemsHandler, Messenger messenger,
      JavaManager javaManager, CodeRunnerFactory codeRunnerFactory) {
    this(modulesListener, problemsHandler, messenger, javaManager,
        codeRunnerFactory, true);
  }
  
  public ModuleManagerImpl(ModulesSource modulesListener,
      ProblemsHandler problemsHandler, Messenger messenger,
      JavaManager javaManager, CodeRunnerFactory codeRunnerFactory,
      boolean waitOnInit) {
    this.modulesListener = modulesListener;
    this.problemsHandler = problemsHandler;
    this.codeRunnerFactory = codeRunnerFactory;
    this.messenger = messenger;
    modules = new HashSet<ModuleRepresentation>();
    moduleContexts = new HashSet<ModuleContextRepresentation>();
    activeModuleContexts = new HashSet<ModuleContextRepresentation>();
    this.javaManager = javaManager;

    this.runAutomatically = false;
    this.activateByDefault = false;

    if (waitOnInit) {
      initThread = null;
      initModules();
      initContexts();
      initing = false;
    } else {
      initing = true;
      initThread = new InitThread();
      initThread.start();
    }
  }

  // this is to avoid blocking loading in the UI if there is one
  private class InitThread extends Thread {
    @Override
    public void run() {
      synchronized (ModuleManagerImpl.this) {
        initModules();
        initContexts();
        initing = false;
      }
    }
  }

  public void waitForInitialization() throws InterruptedException {
    if (initing) {
      initThread.join();
    }
  }

  /*
   * Ask the ModulesListener for all the modules in the user's code.
   */
  private synchronized void initModules() {
    if (javaManager != null) {
      Set<String> moduleNames = modulesListener.getModules(javaManager);
      for (String moduleName : moduleNames) {
        initModule(moduleName);
      }
    }
    if (runAutomatically) {
      cleanAllModules(true, true);
    }
  }

  private void initModule(String moduleName) {
    modules.add(new ModuleRepresentationImpl(moduleName));
  }

  public boolean findNewContexts(boolean waitFor,
      boolean backgroundAutomatically) {
    boolean result = cleanAllModules(waitFor, backgroundAutomatically);
    initContexts();
    return result;
  }

  /*
   * Create module contexts for each module that we can.
   */
  private synchronized void initContexts() {
    for (ModuleRepresentation module : modules) {
      if (module.hasDefaultConstructor()) {
        ModuleContextRepresentation moduleContext =
            new ModuleContextRepresentationImpl(module.getName());
        ModuleInstanceRepresentation moduleInstance =
            new ModuleInstanceRepresentation(module.getName());
        moduleContext.add(moduleInstance);
        moduleContexts.add(moduleContext);
        if (activateByDefault) {
          activeModuleContexts.add(moduleContext);
        }
      }
    }
  }

  public synchronized void addModule(ModuleRepresentation module,
      boolean createContext) throws NoJavaManagerException {
    if (javaManager != null) {
      modules.add(module);
      if (module.hasDefaultConstructor()) {
        ModuleContextRepresentation moduleContext =
            new ModuleContextRepresentationImpl(module.getName());
        moduleContext.add(new ModuleInstanceRepresentation(module.getName()));
        moduleContexts.add(moduleContext);
        if (createContext) {
          activeModuleContexts.add(moduleContext);
        }
      }
    } else {
      throw new NoJavaManagerException(this);
    }
  }

  public synchronized void initModuleName(String moduleName)
      throws NoJavaManagerException {
    initModule(moduleName);
  }

  public synchronized void addModule(String moduleName, boolean createContext)
      throws NoJavaManagerException {
    if (javaManager != null) {
      for (ModuleRepresentation module : modules) {
        if (module.getName().equals(moduleName)) {
          return;
        }
      }
      addModule(new ModuleRepresentationImpl(moduleName), createContext);
    } else {
      throw new NoJavaManagerException(this);
    }
  }

  public synchronized void removeModule(String moduleName)
      throws NoJavaManagerException {
    if (javaManager != null) {
      ModuleRepresentation moduleToRemove = null;
      for (ModuleRepresentation module : modules) {
        if (module.getName().equals(moduleName)) {
          moduleToRemove = module;
        }
      }
      removeModule(moduleToRemove);
    } else {
      throw new NoJavaManagerException(this);
    }
  }

  public synchronized void removeModule(ModuleRepresentation module)
      throws NoJavaManagerException {
    if (javaManager != null) {
      Set<ModuleContextRepresentation> contextsToRemove =
          new HashSet<ModuleContextRepresentation>();
      modules.remove(module);
      for (ModuleContextRepresentation moduleContext : moduleContexts) {
        if (moduleContext.contains(module.getName())) {
          contextsToRemove.add(moduleContext);
        }
      }
      for (ModuleContextRepresentation moduleContext : contextsToRemove) {
        moduleContexts.remove(moduleContext);
        activeModuleContexts.remove(moduleContext);
      }
    } else {
      throw new NoJavaManagerException(this);
    }
  }

  public synchronized void clearModules() {
    if (javaManager != null) {
      modules.clear();
    }
  }

  public synchronized Set<ModuleRepresentation> getModules() {
    return modules != null ? new HashSet<ModuleRepresentation>(modules) : null;
  }

  public synchronized void addModuleContext(
      ModuleContextRepresentation moduleContext, boolean active)
      throws NoJavaManagerException {
    if (javaManager != null) {
      moduleContexts.add(moduleContext);
      if (active) {
        activeModuleContexts.add(moduleContext);
      }
    } else {
      throw new NoJavaManagerException(this);
    }
  }

  public synchronized void clearModuleContexts() {
    if (javaManager != null) {
      moduleContexts.clear();
    }
  }

  public synchronized Set<ModuleContextRepresentation> getModuleContexts() {
    return moduleContexts != null ? new HashSet<ModuleContextRepresentation>(
        moduleContexts) : null;
  }

  public synchronized Set<ModuleContextRepresentation> getActiveModuleContexts() {
    return activeModuleContexts != null
        ? new HashSet<ModuleContextRepresentation>(activeModuleContexts) : null;
  }

  public void moduleChanged(String moduleName) {
    synchronized (this) {
      for (ModuleContextRepresentation moduleContext : moduleContexts) {
        if (moduleContext.contains(moduleName)) {
          moduleContext.markDirty();
        }
      }
    }
    if (runAutomatically) {
      cleanModules(false, true);
      cleanModuleContexts(false, true);
    }
  }

  public synchronized void removeModuleContext(
      ModuleContextRepresentation moduleContext) throws NoJavaManagerException {
    if (javaManager != null) {
      moduleContexts.remove(moduleContext);
      activeModuleContexts.remove(moduleContext);
    } else {
      throw new NoJavaManagerException(this);
    }
  }

  public synchronized void removeModuleContext(String contextName) {
    ModuleContextRepresentation moduleContext = null;
    for (ModuleContextRepresentation context : moduleContexts) {
      if (context.getName().equals(contextName)) {
        moduleContext = context;
      }
    }
    if (moduleContext != null) {
      removeModuleContext(moduleContext);
    }
  }

  public boolean updateModules(boolean waitFor, boolean backgroundAutomatically) {
    if (javaManager != null) {
      return cleanModuleContexts(waitFor, backgroundAutomatically);
    } else {
      return true;
    }
  }

  public boolean rerunModules(boolean waitFor, boolean backgroundAutomatically) {
    synchronized (this) {
      for (ModuleContextRepresentation context : activeModuleContexts) {
        context.markDirty();
      }
    }
    return updateModules(waitFor, backgroundAutomatically);
  }

  public boolean updateModules() {
    return updateModules(true, true);
  }

  public boolean rerunModules() {
    return rerunModules(true, true);
  }

  public boolean findNewContexts() {
    return findNewContexts(true, true);
  }

  private static abstract class ModuleManagerThread extends Thread {
    protected final ModuleManager moduleManager;
    protected final PostUpdater postUpdater;
    protected final boolean backgroundAutomatically;

    public ModuleManagerThread(ModuleManager moduleManager,
        PostUpdater postUpdater, boolean backgroundAutomatically) {
      this.moduleManager = moduleManager;
      this.postUpdater = postUpdater;
      this.backgroundAutomatically = backgroundAutomatically;
    }

    @Override
    public void run() {
      boolean result = myTask(backgroundAutomatically);
      postUpdater.execute(result);
    }

    protected abstract boolean myTask(boolean backgroundAutomatically);
  }

  private static class UpdateModulesThread extends ModuleManagerThread {
    public UpdateModulesThread(ModuleManager moduleManager,
        PostUpdater postUpdater, boolean backgroundAutomatically) {
      super(moduleManager, postUpdater, backgroundAutomatically);
    }

    @Override
    protected boolean myTask(boolean backgroundAutomatically) {
      return moduleManager.updateModules(true, backgroundAutomatically);
    }
  }

  private static class RerunModulesThread extends ModuleManagerThread {
    public RerunModulesThread(ModuleManager moduleManager,
        PostUpdater postUpdater, boolean backgroundAutomatically) {
      super(moduleManager, postUpdater, backgroundAutomatically);
    }

    @Override
    protected boolean myTask(boolean backgroundAutomatically) {
      return moduleManager.rerunModules(true, backgroundAutomatically);
    }
  }

  private static class FindNewContextsThread extends ModuleManagerThread {
    public FindNewContextsThread(ModuleManager moduleManager,
        PostUpdater postUpdater, boolean backgroundAutomatically) {
      super(moduleManager, postUpdater, backgroundAutomatically);
    }

    @Override
    protected boolean myTask(boolean backgroundAutomatically) {
      return moduleManager.findNewContexts(true, backgroundAutomatically);
    }
  }

  public void updateModules(final PostUpdater postUpdater,
      final boolean backgroundAutomatically) {
    new UpdateModulesThread(this, postUpdater, backgroundAutomatically).start();
  }

  public void rerunModules(final PostUpdater postUpdater,
      final boolean backgroundAutomatically) {
    new RerunModulesThread(this, postUpdater, backgroundAutomatically).start();
  }

  public void findNewContexts(final PostUpdater postUpdater,
      final boolean backgroundAutomatically) {
    new FindNewContextsThread(this, postUpdater, backgroundAutomatically)
        .start();
  }

  /*
   * Tells the contexts to run themselves anew. Uses the progress handler.
   */
  protected boolean cleanModuleContexts(boolean waitFor,
      boolean backgroundAutomatically) {
    CodeRunner codeRunner = codeRunnerFactory.create(javaManager);
    synchronized (this) {
      for (ModuleContextRepresentation moduleContext : activeModuleContexts) {
        if (moduleContext.isDirty()) {
          moduleContext.clean(codeRunner);
        }
      }
    }
    codeRunner.addListener(this);
    codeRunner.run("Running module contexts", backgroundAutomatically);
    if (waitFor) {
      try {
        codeRunner.waitFor();
        return !codeRunner.isCancelled();
      } catch (InterruptedException exception) {
        messenger.logException("ModuleContext cleaning interrupted", exception);
      }
    }
    return true;
  }

  protected boolean cleanAllModules(boolean waitFor,
      boolean backgroundAutomatically) {
    return cleanModules(waitFor, backgroundAutomatically, modules);
  }

  protected boolean cleanModules(boolean waitFor,
      boolean backgroundAutomatically) {
    return cleanModules(waitFor, backgroundAutomatically,
        getModulesInActiveContexts());
  }

  protected boolean cleanModules(boolean waitFor,
      boolean backgroundAutomatically, Set<ModuleRepresentation> modulesToClean) {
    CodeRunner codeRunner = codeRunnerFactory.create(javaManager);
    synchronized (this) {
      for (ModuleRepresentation module : modulesToClean) {
        if (module.isDirty()) {
          module.clean(codeRunner);
        }
      }
    }
    codeRunner.addListener(this);
    codeRunner.run("Running modules", backgroundAutomatically);
    if (waitFor) {
      try {
        codeRunner.waitFor();
        return !codeRunner.isCancelled();
      } catch (InterruptedException exception) {
        messenger.logException("CodeRunner interrupted", exception);
      }
    }
    return true;
  }

  private Set<ModuleRepresentation> getModulesInActiveContexts() {
    Set<ModuleRepresentation> activeModules =
        new HashSet<ModuleRepresentation>();
    if (javaManager != null) {
      for (ModuleContextRepresentation moduleContext : activeModuleContexts) {
        for (ModuleInstanceRepresentation module : moduleContext.getModules()) {
          activeModules.add(getModule(module.getClassName()));
        }
      }
    }
    return activeModules;
  }

  protected synchronized ModuleRepresentation getModule(String name) {
    for (ModuleRepresentation module : modules) {
      if (module.getName().equals(name)) {
        return module;
      }
    }
    return null;
  }

  public void acceptCodeRunResult(CodeSnippetResult result) {
    problemsHandler.foundProblems(result.getProblems());
  }

  public void acceptUserCancelled() {
    // do nothing
  }

  public void acceptDone() {
    // do nothing
  }
  
  public boolean runAutomatically() {
    return runAutomatically;
  }

  public void setRunAutomatically(boolean run) {
    this.runAutomatically = run;
    if (run) {
      updateModules(false, true);
    }
  }

  public boolean activateModulesByDefault() {
    return activateByDefault;
  }

  public void setActivateModulesByDefault(boolean activateByDefault) {
    this.activateByDefault = activateByDefault;
  }

  public synchronized void activateModuleContext(
      ModuleContextRepresentation moduleContext) {
    if (javaManager != null) {
      activeModuleContexts.add(moduleContext);
    }
  }

  public synchronized void deactivateModuleContext(
      ModuleContextRepresentation moduleContext) {
    if (javaManager != null) {
      activeModuleContexts.remove(moduleContext);
    }
  }

  public void addCustomContext(String contextName) {
    addModuleContext(new CustomModuleContextRepresentationImpl(contextName,
        contextName, "getModuleContextDefinition"), true);
  }
  
  public void addCustomContext(String name, String classToUse, String methodToCall) {
    addModuleContext(new CustomModuleContextRepresentationImpl(name,
        classToUse, methodToCall), true);
  }

  public void removeCustomContext(String contextName) {
    removeModuleContext(contextName);
  }

  public void customContextChanged(String contextName) {
    ModuleContextRepresentation context = null;
    synchronized (this) {
      for (ModuleContextRepresentation moduleContext : moduleContexts) {
        if (moduleContext.getName().equals(contextName)) {
          context = moduleContext;
        }
      }
    }
    if (context != null) {
      context.markDirty();
    }
  }
}
