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
	/* NOTE: it need not be the case that the moduleContexts contain only modules that are
	    in the modules set.  moduleContexts will have module with arguments setup for running.
	    modules will have all of the available modules for when the user configures what
	    to run */
	private final ProblemsHandler problemsHandler;
	private final HashSet<ModuleRepresentation> modules;
	private final HashSet<ModuleContextRepresentation> moduleContexts;
	
	/** 
	 * Create a ModuleManagerImpl.  This should be done by injection as a singleton.
	 */
	@Inject
	public ModuleManagerImpl(ProblemsHandler problemsHandler) {
		this.problemsHandler = problemsHandler;
		modules = new HashSet<ModuleRepresentation>();
		moduleContexts = new HashSet<ModuleContextRepresentation>();
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
	 * @see com.google.inject.tools.ideplugin.module.ModuleManager#moduleChanged(com.google.inject.tools.ideplugin.module.ModuleRepresentation)
	 */
	public synchronized void moduleChanged(ModuleRepresentation module) {
		for (ModuleContextRepresentation moduleContext : moduleContexts) {
			if (moduleContext.contains(module)) {
				moduleContext.moduleChanged(module);
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
