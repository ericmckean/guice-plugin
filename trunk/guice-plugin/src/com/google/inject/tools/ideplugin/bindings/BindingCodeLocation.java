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

package com.google.inject.tools.ideplugin.bindings;

import com.google.inject.tools.ideplugin.module.ModuleContextRepresentation;
import com.google.inject.tools.ideplugin.problem.CodeProblem;
import com.google.inject.tools.ideplugin.results.CodeLocation;

import java.util.Set;

/**
 * Represents the location in code of where a binding occurs.
 * 
 * @author Darren Creutz <dcreutz@gmail.com>
 */
public class BindingCodeLocation extends CodeLocation {
	private final ModuleContextRepresentation module;
	private final Class<?> bindWhat;
	private final String bindTo;
	
	/**
	 * Create a new BindingCodeLocation.
	 * 
	 * @param bindWhat the class to bind
	 * @param bindTo what it is bound to
	 * @param module the module this binding happens in
	 * @param file the file this happens in
	 * @param location the line number in that file where this happens
	 * @param problems any {@link CodeProblem}s that occurred during getting this binding
	 */
	public BindingCodeLocation(Class<?> bindWhat,String bindTo,ModuleContextRepresentation module,String file,int location,Set<CodeProblem> problems) {
		super(file,location,problems);
		this.bindWhat = bindWhat;
		this.bindTo = bindTo;
		this.module = module;
	}
	
	/**
	 * (non-Javadoc)
	 * @see com.google.inject.tools.ideplugin.results.CodeLocation#getDisplayName()
	 */
	@Override
	public String getDisplayName() {
		return bindTo;
	}
	
	/**
	 * Return the module this binding occurred in.
	 * 
	 * @return the {@link ModuleContextRepresentation}
	 */
	public ModuleContextRepresentation getModule() {
		return module;
	}
	
	/**
	 * Return the Class being bound.
	 * 
	 * @return the class
	 */
	public Class<?> bindWhat() {
		return bindWhat;
	}
}
