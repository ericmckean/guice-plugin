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

package com.google.inject.tools.module;

import com.google.inject.tools.JavaManager;
import com.google.inject.tools.Messenger;
import com.google.inject.tools.ProblemsHandler;
import com.google.inject.tools.code.CodeRunner;
import com.google.inject.tools.GuiceToolsModule.CodeRunnerFactory;
import com.google.inject.tools.module.ModulesSource;
import com.google.inject.tools.module.ModuleContextRepresentation.ModuleInstanceRepresentation;
import com.google.inject.tools.snippets.CodeSnippetResult;
import com.google.inject.Singleton;
import com.google.inject.Inject;
import java.util.Set;
import java.util.HashSet;

/** 
 * The standard implementation of the ModuleManager.
 * 
 * @author Darren Creutz <dcreutz@gmail.com>
 */
@Singleton
public class ModuleManagerImpl implements ModuleManager, CodeRunner.CodeRunListener {
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
   * Create a ModuleManagerImpl.  This should be done by injection as a singleton.
   */
  @Inject
  public ModuleManagerImpl(ModulesSource modulesListener,
      ProblemsHandler problemsHandler,
      Messenger messenger,
      JavaManager javaManager,
      CodeRunnerFactory codeRunnerFactory) {
    this.modulesListener = modulesListener;
    this.problemsHandler = problemsHandler;
    this.codeRunnerFactory = codeRunnerFactory;
    this.messenger = messenger;
    if (javaManager instanceof NullJavaManager) {
      modules = null;
      moduleContexts = null;
      activeModuleContexts = null;
      this.javaManager = null;
    } else {
      this.javaManager = javaManager;
      modules = new HashSet<ModuleRepresentation>();
      moduleContexts = new HashSet<ModuleContextRepresentation>();
      activeModuleContexts = new HashSet<ModuleContextRepresentation>();
    }

    this.runAutomatically = false;
    this.activateByDefault = false;
    boolean waitOnInit = false;
    
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
  
  //this is to avoid blocking loading in the UI if there is one
  private class InitThread extends Thread {
    @Override
    public void run() {
      initModules();
      initContexts();
      initing = false;
    }
  }
  
  public void waitForInitThread() {
    if (initing) {
      try {
        initThread.join();
      } catch (InterruptedException e) {}
    }
  }
  
  public static class NullJavaManager implements JavaManager {
    public String getJavaCommand() throws Exception {
      return null;
    }
    public String getProjectClasspath() throws Exception {
      return null;
    }
    public String getSnippetsClasspath() throws Exception {
      return null;
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
    if (runAutomatically) cleanAllModules(true, true);
  }
  
  private void initModule(String moduleName) {
    modules.add(new ModuleRepresentationImpl(moduleName));
  }
  
  public synchronized boolean findNewContexts(boolean waitFor) {
    boolean result = cleanAllModules(waitFor, true);
    initContexts();
    return result;
  }
  
  /*
   * Create module contexts for each module that we can.
   */
  private synchronized void initContexts() {
    for (ModuleRepresentation module : modules) {
      if (module.hasDefaultConstructor()) {
        ModuleContextRepresentation moduleContext = new ModuleContextRepresentationImpl(module.getName());
        ModuleInstanceRepresentation moduleInstance = new ModuleInstanceRepresentation(module.getName());
        moduleContext.add(moduleInstance);
        moduleContexts.add(moduleContext);
        if (activateByDefault) activeModuleContexts.add(moduleContext);
      }
    }
  }
  
  /**
   * (non-Javadoc)
   * @see com.google.inject.tools.module.ModuleManager#addModule(com.google.inject.tools.module.ModuleRepresentation, boolean)
   */
  public synchronized void addModule(ModuleRepresentation module, boolean createContext) throws NoJavaManagerException {
    if (javaManager != null) {
      modules.add(module);
      if (module.hasDefaultConstructor()) {
        ModuleContextRepresentation moduleContext = new ModuleContextRepresentationImpl(module.getName());
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
  
  /**
   * (non-Javadoc)
   * @see com.google.inject.tools.module.ModuleManager#initModuleName(java.lang.String)
   */
  public synchronized void initModuleName(String moduleName) throws NoJavaManagerException {
    initModule(moduleName);
  }
  
  /**
   * (non-Javadoc)
   * @see com.google.inject.tools.module.ModuleManager#addModule(java.lang.String, boolean)
   */
  public synchronized void addModule(String moduleName, boolean createContext) throws NoJavaManagerException {
    if (javaManager != null) {
      for (ModuleRepresentation module : modules) {
        if (module.getName().equals(moduleName)) return;
      }
      addModule(new ModuleRepresentationImpl(moduleName), createContext);
    } else {
      throw new NoJavaManagerException(this);
    }
  }
  /**
   * (non-Javadoc)
   * @see com.google.inject.tools.module.ModuleManager#removeModule(java.lang.String)
   */
  public synchronized void removeModule(String moduleName) throws NoJavaManagerException {
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
  
  /**
   * (non-Javadoc)
   * @see com.google.inject.tools.module.ModuleManager#removeModule(com.google.inject.tools.module.ModuleRepresentation)
   */
  public synchronized void removeModule(ModuleRepresentation module) throws NoJavaManagerException {
    if (javaManager != null) {
      Set<ModuleContextRepresentation> contextsToRemove = new HashSet<ModuleContextRepresentation>();
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
  
  /**
   * (non-Javadoc)
   * @see com.google.inject.tools.module.ModuleManager#clearModules()
   */
  public synchronized void clearModules() {
    if (javaManager != null) modules.clear();
  }
  
  /**
   * (non-Javadoc)
   * @see com.google.inject.tools.module.ModuleManager#getModules()
   */
  public synchronized Set<ModuleRepresentation> getModules() {
    return modules!=null ? new HashSet<ModuleRepresentation>(modules) : null;
  }
  
  /**
   * (non-Javadoc)
   * @see com.google.inject.tools.module.ModuleManager#addModuleContext(com.google.inject.tools.module.ModuleContextRepresentation, boolean)
   */
  public synchronized void addModuleContext(ModuleContextRepresentation moduleContext, boolean active) throws NoJavaManagerException {
    if (javaManager != null) {
      moduleContexts.add(moduleContext);
      if (active) activeModuleContexts.add(moduleContext);
    } else {
      throw new NoJavaManagerException(this);
    }
  }
  
  /**
   * (non-Javadoc)
   * @see com.google.inject.tools.module.ModuleManager#clearModuleContexts()
   */
  public synchronized void clearModuleContexts() {
    if (javaManager != null) moduleContexts.clear();
  }
  
  /**
   * (non-Javadoc)
   * @see com.google.inject.tools.module.ModuleManager#getModuleContexts()
   */
  public synchronized Set<ModuleContextRepresentation> getModuleContexts() {
    return moduleContexts!=null ? new HashSet<ModuleContextRepresentation>(moduleContexts) : null;
  }
  
  public synchronized Set<ModuleContextRepresentation> getActiveModuleContexts() {
    return activeModuleContexts!=null ? new HashSet<ModuleContextRepresentation>(activeModuleContexts) : null;
  }

  public synchronized void moduleChanged(String moduleName) {
    for (ModuleContextRepresentation moduleContext : moduleContexts) {
      if (moduleContext.contains(moduleName)) {
        moduleContext.markDirty();
      }
    }
    if (runAutomatically) {
      cleanModules(false, true);
      cleanModuleContexts(false, true);
    }
  }
  
  public synchronized void removeModuleContext(ModuleContextRepresentation moduleContext) throws NoJavaManagerException {
    if (javaManager != null) {
      moduleContexts.remove(moduleContext);
      activeModuleContexts.remove(moduleContext);
    } else {
      throw new NoJavaManagerException(this);
    }
  }
  
  public synchronized boolean updateModules(boolean waitFor) {
    if (javaManager != null) {
      cleanAllModules(true, true);
      return cleanModuleContexts(waitFor, false);
    } else {
      return true;
    }
  }
  
  public synchronized boolean rerunModules(boolean waitFor) {
    for (ModuleContextRepresentation context : activeModuleContexts) {
      context.markDirty();
    }
    return updateModules(waitFor);
  }
  
  public boolean updateModules() {
    return updateModules(true);
  }
  
  /*
   * Tells the contexts to run themselves anew.  Uses the progress handler.
   */
  protected synchronized boolean cleanModuleContexts(boolean waitFor, boolean backgroundAutomatically) {
   CodeRunner codeRunner = codeRunnerFactory.create(javaManager);
    for (ModuleContextRepresentation moduleContext : activeModuleContexts) {
      if (moduleContext.isDirty()) {
        moduleContext.clean(codeRunner);
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
  
  protected synchronized boolean cleanAllModules(boolean waitFor, boolean backgroundAutomatically) {
    return cleanModules(waitFor, backgroundAutomatically, modules);
  }
  
  protected synchronized boolean cleanModules(boolean waitFor, boolean backgroundAutomatically) {
    return cleanModules(waitFor, backgroundAutomatically, getModulesInActiveContexts());
  }
    
  protected synchronized boolean cleanModules(boolean waitFor, boolean backgroundAutomatically, Set<ModuleRepresentation> modulesToClean) {
    CodeRunner codeRunner = codeRunnerFactory.create(javaManager);
    for (ModuleRepresentation module : modulesToClean) {
      if (module.isDirty()) {
        module.clean(codeRunner);
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
    Set<ModuleRepresentation> activeModules = new HashSet<ModuleRepresentation>();
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
      if (module.getName().equals(name)) return module;
    }
    return null;
  }
  
  /**
   * (non-Javadoc)
   * @see com.google.inject.tools.code.CodeRunner.CodeRunListener#acceptCodeRunResult(com.google.inject.tools.snippets.CodeSnippetResult)
   */
  public void acceptCodeRunResult(CodeSnippetResult result) {
    problemsHandler.foundProblems(result.getProblems());
  }
  
  /**
   * (non-Javadoc)
   * @see com.google.inject.tools.code.CodeRunner.CodeRunListener#acceptUserCancelled()
   */
  public void acceptUserCancelled() {
    //do nothing
  }
  
  /**
   * (non-Javadoc)
   * @see com.google.inject.tools.code.CodeRunner.CodeRunListener#acceptDone()
   */
  public void acceptDone() {
    //do nothing
  }
  
  /**
   * (non-Javadoc)
   * @see com.google.inject.tools.module.ModuleManager#setRunAutomatically(boolean)
   */
  public void setRunAutomatically(boolean run) {
    this.runAutomatically = run;
  }
  
  /**
   * (non-Javadoc)
   * @see com.google.inject.tools.module.ModuleManager#activateModulesByDefault()
   */
  public boolean activateModulesByDefault() {
    return activateByDefault;
  }
  
  /**
   * (non-Javadoc)
   * @see com.google.inject.tools.module.ModuleManager#setActivateModulesByDefault(boolean)
   */
  public void setActivateModulesByDefault(boolean activateByDefault) {
    this.activateByDefault = activateByDefault;
  }
  
  public synchronized void activateModuleContext(ModuleContextRepresentation moduleContext) {
    if (javaManager!=null) {
      activeModuleContexts.add(moduleContext);
    }
  }
  
  public synchronized void deactivateModuleContext(ModuleContextRepresentation moduleContext) {
    if (javaManager!=null) {
      activeModuleContexts.remove(moduleContext);
    }
  }
}
