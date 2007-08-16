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
import com.google.inject.tools.module.ModulesNotifier;
import com.google.inject.tools.module.ModuleContextRepresentation.ModuleInstanceRepresentation;
import com.google.inject.tools.snippets.CodeSnippetResult;
import com.google.inject.Singleton;
import com.google.inject.Inject;
import java.util.Set;
import java.util.HashSet;
import java.util.HashMap;

/** 
 * The standard implementation of the ModuleManager.
 * 
 * @author Darren Creutz <dcreutz@gmail.com>
 */
@Singleton
public class ModuleManagerImpl implements ModuleManager, CodeRunner.CodeRunListener {
  private final ModulesNotifier modulesListener;
  private final ProblemsHandler problemsHandler;
  private final CodeRunnerFactory codeRunnerFactory;
  private final Messenger messenger;
  private final HashMap<JavaManager,HashSet<ModuleRepresentation>> projectModules;
  private final HashMap<JavaManager,HashSet<ModuleContextRepresentation>> projectModuleContexts;
  private HashSet<ModuleRepresentation> modules;
  private HashSet<ModuleContextRepresentation> moduleContexts;
  private JavaManager currentProject;
  private boolean runAutomatically;
  
  /** 
   * Create a ModuleManagerImpl.  This should be done by injection as a singleton.
   */
  @Inject
  public ModuleManagerImpl(ModulesNotifier modulesListener,
      ProblemsHandler problemsHandler,
      Messenger messenger,
      CodeRunnerFactory codeRunnerFactory) {
    this.modulesListener = modulesListener;
    this.problemsHandler = problemsHandler;
    this.codeRunnerFactory = codeRunnerFactory;
    this.messenger = messenger;
    projectModules = new HashMap<JavaManager,HashSet<ModuleRepresentation>>();
    projectModuleContexts = new HashMap<JavaManager,HashSet<ModuleContextRepresentation>>();
    modules = null;
    moduleContexts = null;
    currentProject = null;
    runAutomatically = false;
  }
  
  /*
   * Ask the ModulesListener for all the modules in the user's code.
   */
  private void initModules() {
    if (currentProject != null) {
      Set<String> moduleNames = modulesListener.findModules();
      for (String moduleName : moduleNames) {
        initModule(moduleName);
      }
    }
    cleanModules(true, true);
  }
  
  private void initModule(String moduleName) {
    modules.add(new ModuleRepresentationImpl(moduleName));
  }
  
  /*
   * Create module contexts for each module that we can.
   */
  private void initContexts() {
    for (ModuleRepresentation module : modules) {
      if (module.hasDefaultConstructor()) {
        ModuleContextRepresentation moduleContext = new ModuleContextRepresentationImpl(module.getName());
        ModuleInstanceRepresentation moduleInstance = new ModuleInstanceRepresentation(module.getName());
        moduleContext.add(moduleInstance);
        moduleContexts.add(moduleContext);
      }
    }
  }
  
  /**
   * (non-Javadoc)
   * @see com.google.inject.tools.module.ModuleManager#addModule(com.google.inject.tools.module.ModuleRepresentation)
   */
  public synchronized void addModule(ModuleRepresentation module) throws NoProjectException {
    if (currentProject != null) {
      modules.add(module);
      if (module.hasDefaultConstructor()) {
        ModuleContextRepresentation moduleContext = new ModuleContextRepresentationImpl(module.getName());
        moduleContext.add(new ModuleInstanceRepresentation(module.getName()));
        moduleContexts.add(moduleContext);
      }
    } else {
      throw new NoProjectException(this);
    }
  }
  
  /**
   * (non-Javadoc)
   * @see com.google.inject.tools.module.ModuleManager#addModule(java.lang.String)
   */
  public synchronized void addModule(String moduleName) throws NoProjectException {
    if (currentProject != null) {
      for (ModuleRepresentation module : modules) {
        if (module.getName().equals(moduleName)) return;
      }
      initModule(moduleName);
    } else {
      throw new NoProjectException(this);
    }
  }
  /**
   * (non-Javadoc)
   * @see com.google.inject.tools.module.ModuleManager#removeModule(java.lang.String)
   */
  public synchronized void removeModule(String moduleName) throws NoProjectException {
    if (currentProject != null) {
      for (ModuleRepresentation module : modules) {
        if (module.getName().equals(moduleName)) {
          removeModule(module);
        }
      }
    } else {
      throw new NoProjectException(this);
    }
  }
  
  /**
   * (non-Javadoc)
   * @see com.google.inject.tools.module.ModuleManager#removeModule(com.google.inject.tools.module.ModuleRepresentation)
   */
  public synchronized void removeModule(ModuleRepresentation module) throws NoProjectException {
    if (currentProject != null) {
      modules.remove(module);
      for (ModuleContextRepresentation moduleContext : moduleContexts) {
        if (moduleContext.contains(module.getName())) {
          moduleContexts.remove(moduleContext);
        }
      }
    } else {
      throw new NoProjectException(this);
    }
  }
  
  /**
   * (non-Javadoc)
   * @see com.google.inject.tools.module.ModuleManager#clearModules()
   */
  public synchronized void clearModules() {
    if (currentProject != null) modules.clear();
  }
  
  /**
   * (non-Javadoc)
   * @see com.google.inject.tools.module.ModuleManager#clearModules(com.google.inject.tools.JavaManager)
   */
  public synchronized void clearModules(JavaManager whichProject) {
    if (projectModules.get(whichProject) != null)
      projectModules.get(whichProject).clear();
  }
  
  /**
   * (non-Javadoc)
   * @see com.google.inject.tools.module.ModuleManager#getModules()
   */
  public Set<ModuleRepresentation> getModules() {
    return modules!=null ? new HashSet<ModuleRepresentation>(modules) : null;
  }
  
  /**
   * (non-Javadoc)
   * @see com.google.inject.tools.module.ModuleManager#getModules(com.google.inject.tools.JavaManager)
   */
  public Set<ModuleRepresentation> getModules(JavaManager whichProject) {
    return projectModules.get(whichProject)!=null ?
        new HashSet<ModuleRepresentation>(projectModules.get(whichProject)) : null;
  }
  
  /**
   * (non-Javadoc)
   * @see com.google.inject.tools.module.ModuleManager#addModuleContext(com.google.inject.tools.module.ModuleContextRepresentation)
   */
  public synchronized void addModuleContext(ModuleContextRepresentation moduleContext) throws NoProjectException {
    if (currentProject != null) {
      moduleContexts.add(moduleContext);
    } else {
      throw new NoProjectException(this);
    }
  }
  
  /**
   * (non-Javadoc)
   * @see com.google.inject.tools.module.ModuleManager#clearModuleContexts()
   */
  public synchronized void clearModuleContexts() {
    if (currentProject != null) moduleContexts.clear();
  }
  
  /**
   * (non-Javadoc)
   * @see com.google.inject.tools.module.ModuleManager#clearModuleContexts(com.google.inject.tools.JavaManager)
   */
  public synchronized void clearModuleContexts(JavaManager whichProject) {
    if (projectModuleContexts.get(whichProject) != null) 
      projectModuleContexts.get(whichProject).clear();
  }
  
  /**
   * (non-Javadoc)
   * @see com.google.inject.tools.module.ModuleManager#getModuleContexts()
   */
  public synchronized Set<ModuleContextRepresentation> getModuleContexts() {
    return moduleContexts!=null ? new HashSet<ModuleContextRepresentation>(moduleContexts) : null;
  }
  
  /**
   * (non-Javadoc)
   * @see com.google.inject.tools.module.ModuleManager#getModuleContexts(com.google.inject.tools.JavaManager)
   */
  public synchronized Set<ModuleContextRepresentation> getModuleContexts(JavaManager whichProject) {
    return projectModuleContexts.get(whichProject)!=null ?
        new HashSet<ModuleContextRepresentation>(projectModuleContexts.get(whichProject)) : null;
  }
  
  /**
   * (non-Javadoc)
   * @see com.google.inject.tools.module.ModuleManager#moduleChanged(java.lang.String)
   */
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
  
  /**
   * (non-Javadoc)
   * @see com.google.inject.tools.module.ModuleManager#removeModuleContext(com.google.inject.tools.module.ModuleContextRepresentation)
   */
  public synchronized void removeModuleContext(ModuleContextRepresentation moduleContext) throws NoProjectException {
    if (currentProject != null) {
      moduleContexts.remove(moduleContext);
    } else {
      throw new NoProjectException(this);
    }
  }
  
  /**
   * (non-Javadoc)
   * @see com.google.inject.tools.module.ModuleManager#updateModules(com.google.inject.tools.JavaManager, boolean)
   */
  public synchronized boolean updateModules(JavaManager javaProject, boolean waitFor) {
    if (!javaProject.equals(currentProject)) {
      currentProject = javaProject;
      if (projectModules.get(currentProject) == null) {
        projectModules.put(currentProject,new HashSet<ModuleRepresentation>());
        projectModuleContexts.put(currentProject,new HashSet<ModuleContextRepresentation>());
        modules = projectModules.get(currentProject);
        moduleContexts = projectModuleContexts.get(currentProject);
        modulesListener.projectChanged(currentProject);
        initModules();
        initContexts();
      } else {
        modulesListener.projectChanged(currentProject);
        modulesListener.findChanges();
      }
    } else {
      modulesListener.findChanges();
    }
    if (currentProject != null) {
      return cleanModuleContexts(waitFor, false);
    } else {
      return true;
    }
  }
  
  /*
   * Tells the contexts to run themselves anew.  Uses the progress handler.
   */
  protected synchronized boolean cleanModuleContexts(boolean waitFor, boolean backgroundAutomatically) {
    CodeRunner codeRunner = codeRunnerFactory.create(currentProject);
    for (ModuleContextRepresentation moduleContext : moduleContexts) {
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
  
  protected synchronized boolean cleanModules(boolean waitFor, boolean backgroundAutomatically) {
    CodeRunner codeRunner = codeRunnerFactory.create(currentProject);
    for (ModuleRepresentation module : modules) {
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
  
  /**
   * (non-Javadoc)
   * @see com.google.inject.tools.module.ModuleManager#getCurrentProject()
   */
  public JavaManager getCurrentProject() {
    return currentProject;
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
}
