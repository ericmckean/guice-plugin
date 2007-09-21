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
import com.google.inject.tools.suite.code.CodeRunnerFactory;
import com.google.inject.tools.suite.code.CodeRunner;
import com.google.inject.tools.suite.module.ModuleContextRepresentation;
import com.google.inject.tools.suite.module.ModuleContextRepresentationImpl;
import com.google.inject.tools.suite.module.ModuleManager;
import com.google.inject.tools.suite.module.ModuleRepresentation;
import com.google.inject.tools.suite.module.ModuleRepresentationImpl;
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
  private final ProblemsHandler problemsHandler;
  private final CodeRunnerFactory codeRunnerFactory;
  private final Messenger messenger;
  private final HashSet<ModuleRepresentationImpl> modules;
  private final HashSet<ModuleContextRepresentationImpl> moduleContexts;
  private final HashSet<ModuleContextRepresentationImpl> activeModuleContexts;
  private final JavaManager javaManager;
  private boolean runAutomatically;
  private boolean activateByDefault;

  /**
   * Create a ModuleManagerImpl. This should be done by injection.
   */
  @Inject
  public ModuleManagerImpl(ProblemsHandler problemsHandler, Messenger messenger,
      JavaManager javaManager, CodeRunnerFactory codeRunnerFactory) {
    this(problemsHandler, messenger, javaManager,
        codeRunnerFactory, true, true, true);
  }
  
  public ModuleManagerImpl(ProblemsHandler problemsHandler, Messenger messenger,
      JavaManager javaManager, CodeRunnerFactory codeRunnerFactory,
      boolean waitOnInit, boolean runAutomatically, boolean activateByDefault) {
    this.problemsHandler = problemsHandler;
    this.codeRunnerFactory = codeRunnerFactory;
    this.messenger = messenger;
    modules = new HashSet<ModuleRepresentationImpl>();
    moduleContexts = new HashSet<ModuleContextRepresentationImpl>();
    activeModuleContexts = new HashSet<ModuleContextRepresentationImpl>();
    this.javaManager = javaManager;
    this.runAutomatically = runAutomatically;
    this.activateByDefault = activateByDefault;
  }

  public synchronized void addModule(ModuleRepresentation module,
      boolean createContext) {
    if (javaManager != null) {
      modules.add((ModuleRepresentationImpl)module);
      if (module.hasDefaultConstructor()) {
        ModuleContextRepresentationImpl moduleContext =
            new ModuleContextRepresentationImpl(module.getName());
        moduleContext.add(new ModuleInstanceRepresentation(module.getName()));
        moduleContexts.add(moduleContext);
        if (createContext) {
          activeModuleContexts.add(moduleContext);
        }
      }
    }
  }

  public synchronized void addModule(String moduleName, boolean createContext) {
    if (javaManager != null) {
      for (ModuleRepresentation module : modules) {
        if (module.getName().equals(moduleName)) {
          return;
        }
      }
      addModule(new ModuleRepresentationImpl(moduleName), createContext);
    }
  }

  public synchronized void removeModule(String moduleName) {
    if (javaManager != null) {
      ModuleRepresentation moduleToRemove = null;
      for (ModuleRepresentation module : modules) {
        if (module.getName().equals(moduleName)) {
          moduleToRemove = module;
        }
      }
      removeModule(moduleToRemove);
    }
  }

  public synchronized void removeModule(ModuleRepresentation module) {
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
      ModuleContextRepresentation moduleContext, boolean active) {
    if (javaManager != null && moduleContext instanceof ModuleContextRepresentationImpl) {
      moduleContexts.add((ModuleContextRepresentationImpl)moduleContext);
      if (active) {
        activeModuleContexts.add((ModuleContextRepresentationImpl)moduleContext);
        if (runAutomatically) {
          cleanModuleContexts(false, true);
        }
      }
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
      for (ModuleContextRepresentationImpl moduleContext : moduleContexts) {
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
      ModuleContextRepresentation moduleContext) {
    if (javaManager != null) {
      moduleContexts.remove(moduleContext);
      activeModuleContexts.remove(moduleContext);
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

  public boolean update(boolean waitFor, boolean backgroundAutomatically) {
    if (javaManager != null) {
      return cleanModuleContexts(waitFor, backgroundAutomatically);
    } else {
      return true;
    }
  }

  public boolean rerunModules(boolean waitFor, boolean backgroundAutomatically) {
    synchronized (this) {
      for (ModuleContextRepresentationImpl context : activeModuleContexts) {
        context.markDirty();
      }
    }
    return update(waitFor, backgroundAutomatically);
  }

  public boolean update() {
    return update(true, true);
  }

  public boolean rerunModules() {
    return rerunModules(true, true);
  }

  private static abstract class ModuleManagerThread extends Thread {
    protected final ModuleManagerImpl moduleManager;
    protected final PostUpdater postUpdater;
    protected final boolean backgroundAutomatically;

    public ModuleManagerThread(ModuleManagerImpl moduleManager,
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
    public UpdateModulesThread(ModuleManagerImpl moduleManager,
        PostUpdater postUpdater, boolean backgroundAutomatically) {
      super(moduleManager, postUpdater, backgroundAutomatically);
    }

    @Override
    protected boolean myTask(boolean backgroundAutomatically) {
      return moduleManager.update(true, backgroundAutomatically);
    }
  }

  private static class RerunModulesThread extends ModuleManagerThread {
    public RerunModulesThread(ModuleManagerImpl moduleManager,
        PostUpdater postUpdater, boolean backgroundAutomatically) {
      super(moduleManager, postUpdater, backgroundAutomatically);
    }

    @Override
    protected boolean myTask(boolean backgroundAutomatically) {
      return moduleManager.rerunModules(true, backgroundAutomatically);
    }
  }

  public void update(final PostUpdater postUpdater,
      final boolean backgroundAutomatically) {
    new UpdateModulesThread(this, postUpdater, backgroundAutomatically).start();
  }

  public void rerunModules(final PostUpdater postUpdater,
      final boolean backgroundAutomatically) {
    new RerunModulesThread(this, postUpdater, backgroundAutomatically).start();
  }

  /*
   * Tells the contexts to run themselves anew. Uses the progress handler.
   */
  protected boolean cleanModuleContexts(boolean waitFor,
      boolean backgroundAutomatically) {
    CodeRunner codeRunner = codeRunnerFactory.create(javaManager);
    synchronized (this) {
      for (ModuleContextRepresentationImpl moduleContext : activeModuleContexts) {
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
      boolean backgroundAutomatically, Set<ModuleRepresentationImpl> modulesToClean) {
    CodeRunner codeRunner = codeRunnerFactory.create(javaManager);
    synchronized (this) {
      for (ModuleRepresentationImpl module : modulesToClean) {
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

  private Set<ModuleRepresentationImpl> getModulesInActiveContexts() {
    Set<ModuleRepresentationImpl> activeModules =
        new HashSet<ModuleRepresentationImpl>();
    if (javaManager != null) {
      for (ModuleContextRepresentationImpl moduleContext : activeModuleContexts) {
        for (ModuleInstanceRepresentation module : moduleContext.getModules()) {
          activeModules.add(getModule(module.getClassName()));
        }
      }
    }
    return activeModules;
  }

  protected synchronized ModuleRepresentationImpl getModule(String name) {
    for (ModuleRepresentationImpl module : modules) {
      if (module.getName().equals(name)) {
        return module;
      }
    }
    return null;
  }

  public void acceptCodeRunResult(CodeSnippetResult result) {
    problemsHandler.foundProblems(result.getAllProblems());
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
      update(false, true);
    }
  }

  public boolean activateModulesByDefault() {
    return activateByDefault;
  }

  public void setActivateModulesByDefault(boolean activateByDefault) {
    this.activateByDefault = activateByDefault;
  }

  public synchronized void activateModuleContext(
      ModuleContextRepresentationImpl moduleContext) {
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
    ModuleContextRepresentationImpl context = null;
    synchronized (this) {
      for (ModuleContextRepresentationImpl moduleContext : moduleContexts) {
        if (moduleContext.getName().equals(contextName)) {
          context = moduleContext;
        }
      }
    }
    if (context != null) {
      context.markDirty();
    }
  }
  
  public void addApplicationContext(String className) {
    addApplicationContext(className, className);
  }
  
  public void addApplicationContext(String name, String className) {
    addModuleContext(new ApplicationModuleContextRepresentationImpl(name,
        className), true);
  }

  public void removeApplicationContext(String contextName) {
    removeModuleContext(contextName);
  }

  public void applicationContextChanged(String contextName) {
    ModuleContextRepresentationImpl context = null;
    synchronized (this) {
      for (ModuleContextRepresentationImpl moduleContext : moduleContexts) {
        if (moduleContext.getName().equals(contextName)) {
          context = moduleContext;
        }
      }
    }
    if (context != null) {
      context.markDirty();
    }
  }
  
  public ModuleContextRepresentation getModuleContext(String name) {
    for (ModuleContextRepresentation moduleContext : moduleContexts) {
      if (moduleContext.getName().equals(name)) {
        return moduleContext;
      }
    }
    return null;
  }
  
  public ModuleContextRepresentation createModuleContext(String name) {
    if (getModuleContext(name) != null) {
      return getModuleContext(name);
    }
    ModuleContextRepresentationImpl moduleContext = new ModuleContextRepresentationImpl(name);
    moduleContexts.add(moduleContext);
    if (activateByDefault) activeModuleContexts.add(moduleContext);
    return moduleContext;
  }
  
  public boolean updateModules() {
    return cleanAllModules(true, true);
  }
  
  public boolean updateModules(boolean waitFor, boolean backgroundAutomatically) {
    return cleanAllModules(waitFor, backgroundAutomatically);
  }
  
  public void activateModuleContext(String contextName) {
    activateModuleContext((ModuleContextRepresentationImpl)getModuleContext(contextName));
  }
  
  public void deactivateModuleContext(String contextName) {
    deactivateModuleContext(getModuleContext(contextName));
  }
}
