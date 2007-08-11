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

package com.google.inject.tools.ideplugin.module;

import com.google.inject.tools.ideplugin.code.CodeRunner;
import com.google.inject.tools.ideplugin.eclipse.EclipseMessenger;
import com.google.inject.tools.ideplugin.module.ModulesListener;
import com.google.inject.tools.ideplugin.problem.ProblemsHandler;
import com.google.inject.tools.ideplugin.JavaProject;
import com.google.inject.tools.ideplugin.GuicePluginModule.CodeRunnerFactory;
import com.google.inject.tools.ideplugin.ProgressHandler;
import com.google.inject.Provider;
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
public class ModuleManagerImpl implements ModuleManager {
	private final ModulesListener modulesListener;
  private final ProblemsHandler problemsHandler;
  private final CodeRunnerFactory codeRunnerFactory;
  private final Provider<ProgressHandler> progressHandlerProvider;
	private final HashMap<JavaProject,HashSet<ModuleRepresentation>> projectModules;
	private final HashMap<JavaProject,HashSet<ModuleContextRepresentation>> projectModuleContexts;
	private HashSet<ModuleRepresentation> modules;
	private HashSet<ModuleContextRepresentation> moduleContexts;
	private JavaProject currentProject;
	
	/** 
	 * Create a ModuleManagerImpl.  This should be done by injection as a singleton.
	 */
	@Inject
	public ModuleManagerImpl(ModulesListener modulesListener,
      ProblemsHandler problemsHandler,
      CodeRunnerFactory codeRunnerFactory, 
      Provider<ProgressHandler> progressHandlerProvider) {
		this.modulesListener = modulesListener;
    this.problemsHandler = problemsHandler;
    this.codeRunnerFactory = codeRunnerFactory;
    this.progressHandlerProvider = progressHandlerProvider;
		projectModules = new HashMap<JavaProject,HashSet<ModuleRepresentation>>();
		projectModuleContexts = new HashMap<JavaProject,HashSet<ModuleContextRepresentation>>();
		modules = null;
		moduleContexts = null;
		currentProject = null;
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
    cleanModules();
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
	 * @see com.google.inject.tools.ideplugin.module.ModuleManager#addModule(com.google.inject.tools.ideplugin.module.ModuleRepresentation)
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
	 * @see com.google.inject.tools.ideplugin.module.ModuleManager#addModule(java.lang.String)
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
	 * @see com.google.inject.tools.ideplugin.module.ModuleManager#removeModule(java.lang.String)
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
	 * @see com.google.inject.tools.ideplugin.module.ModuleManager#removeModule(com.google.inject.tools.ideplugin.module.ModuleRepresentation)
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
	 * @see com.google.inject.tools.ideplugin.module.ModuleManager#clearModules()
	 */
	public synchronized void clearModules() {
		if (currentProject != null) modules.clear();
	}
	
  /**
   * (non-Javadoc)
   * @see com.google.inject.tools.ideplugin.module.ModuleManager#clearModules(com.google.inject.tools.ideplugin.JavaProject)
   */
	public synchronized void clearModules(JavaProject whichProject) {
		if (projectModules.get(whichProject) != null)
			projectModules.get(whichProject).clear();
	}
	
	/**
	 * (non-Javadoc)
	 * @see com.google.inject.tools.ideplugin.module.ModuleManager#getModules()
	 */
	public Set<ModuleRepresentation> getModules() {
		return modules!=null ? new HashSet<ModuleRepresentation>(modules) : null;
	}
	
  /**
   * (non-Javadoc)
   * @see com.google.inject.tools.ideplugin.module.ModuleManager#getModules(com.google.inject.tools.ideplugin.JavaProject)
   */
	public Set<ModuleRepresentation> getModules(JavaProject whichProject) {
		return projectModules.get(whichProject)!=null ?
				new HashSet<ModuleRepresentation>(projectModules.get(whichProject)) : null;
	}

	/**
	 * (non-Javadoc)
	 * @see com.google.inject.tools.ideplugin.module.ModuleManager#addModuleContext(com.google.inject.tools.ideplugin.module.ModuleContextRepresentation)
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
	 * @see com.google.inject.tools.ideplugin.module.ModuleManager#clearModuleContexts()
	 */
	public synchronized void clearModuleContexts() {
		if (currentProject != null) moduleContexts.clear();
	}
	
  /**
   * (non-Javadoc)
   * @see com.google.inject.tools.ideplugin.module.ModuleManager#clearModuleContexts(com.google.inject.tools.ideplugin.JavaProject)
   */
	public synchronized void clearModuleContexts(JavaProject whichProject) {
		if (projectModuleContexts.get(whichProject) != null) 
			projectModuleContexts.get(whichProject).clear();
	}

	/**
	 * (non-Javadoc)
	 * @see com.google.inject.tools.ideplugin.module.ModuleManager#getModuleContexts()
	 */
	public synchronized Set<ModuleContextRepresentation> getModuleContexts() {
		return moduleContexts!=null ? new HashSet<ModuleContextRepresentation>(moduleContexts) : null;
	}
	
  /**
   * (non-Javadoc)
   * @see com.google.inject.tools.ideplugin.module.ModuleManager#getModuleContexts(com.google.inject.tools.ideplugin.JavaProject)
   */
	public synchronized Set<ModuleContextRepresentation> getModuleContexts(JavaProject whichProject) {
		return projectModuleContexts.get(whichProject)!=null ?
				new HashSet<ModuleContextRepresentation>(projectModuleContexts.get(whichProject)) : null;
	}

	/**
	 * (non-Javadoc)
	 * @see com.google.inject.tools.ideplugin.module.ModuleManager#moduleChanged(java.lang.String)
	 */
	public synchronized void moduleChanged(String moduleName) {
		for (ModuleContextRepresentation moduleContext : moduleContexts) {
			if (moduleContext.contains(moduleName)) {
				moduleContext.markDirty();
			}
		}
	}

	/**
	 * (non-Javadoc)
	 * @see com.google.inject.tools.ideplugin.module.ModuleManager#removeModuleContext(com.google.inject.tools.ideplugin.module.ModuleContextRepresentation)
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
   * @see com.google.inject.tools.ideplugin.module.ModuleManager#updateModules(com.google.inject.tools.ideplugin.JavaProject)
   */
	public synchronized boolean updateModules(JavaProject javaProject) {
		if (currentProject != javaProject) {
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
    return cleanModuleContexts();
	}
  
  /*
   * Tells the contexts to run themselves anew.  Uses the progress handler.
   */
  protected synchronized boolean cleanModuleContexts() {
    ProgressHandler progressHandler = progressHandlerProvider.get();
    int numDirty = 0;
    for (ModuleContextRepresentation moduleContext : moduleContexts) {
      if (moduleContext.isDirty()) numDirty++;
    }
    progressHandler.initialize(numDirty);
    for (ModuleContextRepresentation moduleContext : moduleContexts) {
      if (moduleContext.isDirty()) {
        CodeRunner codeRunner = codeRunnerFactory.create(currentProject);
        progressHandler.step("Running module context '" + moduleContext.getName() + "'",codeRunner);
        moduleContext.clean(codeRunner);
        problemsHandler.foundProblems(moduleContext.getProblems());
        if (progressHandler.isCancelled()) {
          System.out.println("wtf");
          return false;
        }
      }
    }
    return true;
  }
  
  protected synchronized boolean cleanModules() {
    ProgressHandler progressHandler = progressHandlerProvider.get();
    int numDirty = 0;
    for (ModuleRepresentation module : modules) {
      if (module.isDirty()) numDirty++;
    }
    progressHandler.initialize(numDirty);
    for (ModuleRepresentation module : modules) {
      if (module.isDirty()) {
        CodeRunner codeRunner = codeRunnerFactory.create(currentProject);
        progressHandler.step("Running module '" + module.getName() + "'",codeRunner);
        module.clean(codeRunner);
        if (progressHandler.isCancelled()) return false;
      }
    }
    return true;
  }
}
