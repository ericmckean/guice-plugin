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

import com.google.inject.tools.ideplugin.problem.ProblemsHandler;
import com.google.inject.tools.ideplugin.module.ModulesListener;
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
public class ModuleManagerImpl implements ModuleManager {
	private final ProblemsHandler problemsHandler;
	private final ModulesListener modulesListener;
	private final HashSet<ModuleRepresentation> modules;
	private final HashSet<ModuleContextRepresentation> moduleContexts;
	
	/** 
	 * Create a ModuleManagerImpl.  This should be done by injection as a singleton.
	 */
	@Inject
	public ModuleManagerImpl(ProblemsHandler problemsHandler,ModulesListener modulesListener) {
		this.problemsHandler = problemsHandler;
		this.modulesListener = modulesListener;
		modules = new HashSet<ModuleRepresentation>();
		initModules();
		moduleContexts = new HashSet<ModuleContextRepresentation>();
		initContexts();
	}
	
	/*
	 * Ask the ModulesListener for all the modules in the user's code.
	 */
	private void initModules() {
		Set<String> moduleNames = modulesListener.findModules();
		for (String moduleName : moduleNames) {
			initModule(moduleName);
		}
	}
	
	private boolean initModule(String moduleName) {
		ModuleRepresentation module = null;
		try {
			module = new ModuleRepresentationImpl(moduleName);
		} catch (ClassNotFoundException exception) {
			//TODO: error message ?
			return false;
		} catch (ModuleRepresentation.ClassNotModuleException exception) {
			//TODO: error message ?
			return false;
		}
		if (module != null) {
			modules.add(module);
			return true;
		} else return false;
	}
	
	/*
	 * Create module contexts for each module that we can.
	 */
	private void initContexts() {
		for (ModuleRepresentation module : modules) {
			ModuleContextRepresentation moduleContext = new ModuleContextRepresentationImpl(module.getName());
			moduleContext.add(module);
			if (!moduleContext.hasProblem()) moduleContexts.add(moduleContext);
			//else this module cannot be instantiated without user input
		}
	}
	
	/**
	 * (non-Javadoc)
	 * @see com.google.inject.tools.ideplugin.module.ModuleManager#addModule(com.google.inject.tools.ideplugin.module.ModuleRepresentation)
	 */
	public synchronized void addModule(ModuleRepresentation module) {
		modules.add(module);
	}
	
	/**
	 * (non-Javadoc)
	 * @see com.google.inject.tools.ideplugin.module.ModuleManager#addModule(java.lang.String)
	 */
	public synchronized void addModule(String moduleName) {
		for (ModuleRepresentation module : modules) {
			if (module.getName().equals(moduleName)) return;
		}
		initModule(moduleName);
	}
	
	/**
	 * (non-Javadoc)
	 * @see com.google.inject.tools.ideplugin.module.ModuleManager#removeModule(java.lang.String)
	 */
	public synchronized void removeModule(String moduleName) {
		for (ModuleRepresentation module : modules) {
			if (module.getName().equals(moduleName)) {
				removeModule(module);
			}
		}
	}
	
	/**
	 * (non-Javadoc)
	 * @see com.google.inject.tools.ideplugin.module.ModuleManager#removeModule(com.google.inject.tools.ideplugin.module.ModuleRepresentation)
	 */
	public synchronized void removeModule(ModuleRepresentation module) {
		modules.remove(module);
	}
	
	/**
	 * (non-Javadoc)
	 * @see com.google.inject.tools.ideplugin.module.ModuleManager#clearModules()
	 */
	public synchronized void clearModules() {
		modules.clear();
	}
	
	/**
	 * (non-Javadoc)
	 * @see com.google.inject.tools.ideplugin.module.ModuleManager#getModules()
	 */
	public Set<ModuleRepresentation> getModules() {
		return new HashSet<ModuleRepresentation>(modules);
	}

	/**
	 * (non-Javadoc)
	 * @see com.google.inject.tools.ideplugin.module.ModuleManager#addModuleContext(com.google.inject.tools.ideplugin.module.ModuleContextRepresentation)
	 */
	public synchronized void addModuleContext(ModuleContextRepresentation moduleContext) {
		moduleContexts.add(moduleContext);
		if (moduleContext.hasProblem()) {
			problemsHandler.foundProblem(moduleContext.getProblem());
		}
	}

	/**
	 * (non-Javadoc)
	 * @see com.google.inject.tools.ideplugin.module.ModuleManager#clearModuleContexts()
	 */
	public synchronized void clearModuleContexts() {
		moduleContexts.clear();
	}

	/**
	 * (non-Javadoc)
	 * @see com.google.inject.tools.ideplugin.module.ModuleManager#getModuleContexts()
	 */
	public synchronized Set<ModuleContextRepresentation> getModuleContexts() {
		return new HashSet<ModuleContextRepresentation>(moduleContexts);
	}

	/**
	 * (non-Javadoc)
	 * @see com.google.inject.tools.ideplugin.module.ModuleManager#moduleChanged(java.lang.String)
	 */
	public synchronized void moduleChanged(String moduleName) {
		for (ModuleContextRepresentation moduleContext : moduleContexts) {
			if (moduleContext.contains(moduleName)) {
				moduleContext.update();
				if (moduleContext.hasProblem()) {
					problemsHandler.foundProblem(moduleContext.getProblem());
				}
			}
		}
	}

	/**
	 * (non-Javadoc)
	 * @see com.google.inject.tools.ideplugin.module.ModuleManager#removeModuleContext(com.google.inject.tools.ideplugin.module.ModuleContextRepresentation)
	 */
	public synchronized void removeModuleContext(ModuleContextRepresentation moduleContext) {
		moduleContexts.remove(moduleContext);
	}
}
