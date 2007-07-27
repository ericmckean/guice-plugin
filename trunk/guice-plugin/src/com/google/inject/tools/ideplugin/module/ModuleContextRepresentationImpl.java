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

import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Guice;
import com.google.inject.CreationException;
import com.google.inject.tools.ideplugin.code.CodeProblem;
import java.util.HashSet;
import java.util.Set;

/**
 * Standard implementation of the {@link ModuleContextRepresentation}.
 * 
 * @author Darren Creutz <dcreutz@gmail.com>
 */
public class ModuleContextRepresentationImpl implements ModuleContextRepresentation {
	private final String title;
	private final HashSet<ModuleRepresentation> modules;
	private Injector injector;
	private CodeProblem.CreationProblem problem;
	private boolean isValid = false;
	
	/**
	 * Create a ModuleContextRepresentation with the given title.
	 * 
	 * @param title the name of the context
	 */
	public ModuleContextRepresentationImpl(String title) {
		this.title = title;
		modules = new HashSet<ModuleRepresentation>();
		makeInjector();
	}
	
	private void makeInjector() throws CreationException {
		isValid = false;
		if (!modules.isEmpty()) {
			Set<Module> instances = new HashSet<Module>();
			for (ModuleRepresentation module : modules) {
				instances.add(module.getInstance());
			}
			try {
				this.injector = Guice.createInjector(instances);
				isValid = true;
				problem = null;
			} catch (CreationException exception) {
				problem = new CodeProblem.CreationProblem(this,exception);
			}
		}
	}

	/**
	 * (non-Javadoc)
	 * @see com.google.inject.tools.ideplugin.module.ModuleContextRepresentation#getName()
	 */
	public String getName() {
		return title;
	}

	/**
	 * (non-Javadoc)
	 * @see com.google.inject.tools.ideplugin.module.ModuleContextRepresentation#getInjector()
	 */
	public Injector getInjector() {
		return injector;
	}

	/**
	 * (non-Javadoc)
	 * @see com.google.inject.tools.ideplugin.module.ModuleContextRepresentation#getModules()
	 */
	public synchronized Set<ModuleRepresentation> getModules() {
		return new HashSet<ModuleRepresentation>(modules);
	}

	/**
	 * (non-Javadoc)
	 * @see com.google.inject.tools.ideplugin.module.ModuleContextRepresentation#moduleChanged(com.google.inject.tools.ideplugin.module.ModuleRepresentation)
	 */
	public synchronized void moduleChanged(ModuleRepresentation module) {
		if (modules.contains(module)) {
			makeInjector();
		}
	}

	/**
	 * (non-Javadoc)
	 * @see com.google.inject.tools.ideplugin.module.ModuleContextRepresentation#add(com.google.inject.tools.ideplugin.module.ModuleRepresentation)
	 */
	public synchronized ModuleContextRepresentation add(ModuleRepresentation module) throws InvalidModuleException {
		if (module.isValid()) {
			modules.add(module);
			makeInjector();
			return this;
		} else throw new InvalidModuleException(module);
	}

	/**
	 * (non-Javadoc)
	 * @see com.google.inject.tools.ideplugin.module.ModuleContextRepresentation#removeModule(com.google.inject.tools.ideplugin.module.ModuleRepresentation)
	 */
	public synchronized void removeModule(ModuleRepresentation module) {
		modules.remove(module);
		makeInjector();
	}
	
	/**
	 * (non-Javadoc)
	 * @see com.google.inject.tools.ideplugin.module.ModuleContextRepresentation#hasProblem()
	 */
	public boolean hasProblem() {
		return (problem!=null);
	}
	
	/**
	 * (non-Javadoc)
	 * @see com.google.inject.tools.ideplugin.module.ModuleContextRepresentation#getProblem()
	 */
	public CodeProblem.CreationProblem getProblem() {
		return problem;
	}
	
	/**
	 * (non-Javadoc)
	 * @see com.google.inject.tools.ideplugin.module.ModuleContextRepresentation#contains(com.google.inject.tools.ideplugin.module.ModuleRepresentation)
	 */
	public synchronized boolean contains(ModuleRepresentation module) {
		return modules.contains(module);
	}
	
	/**
	 * (non-Javadoc)
	 * @see com.google.inject.tools.ideplugin.module.ModuleContextRepresentation#isValid()
	 */
	public boolean isValid() {
		return isValid;
	}
}
