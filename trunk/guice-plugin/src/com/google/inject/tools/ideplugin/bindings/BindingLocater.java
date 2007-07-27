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

import com.google.inject.tools.ideplugin.code.CodeProblem;
import com.google.inject.tools.ideplugin.module.ModuleContextRepresentation;
import com.google.inject.Binding;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.Injector;
//import com.google.inject.OutOfScopeException;
//NOTE: this is not in guice 1.0 but is in the svn head
import java.util.Set;
import java.util.HashSet;

/**
 * The BindingLocater performs the actual determination of what an interface is bound to
 * in a given module and where in the source code that happens.
 * 
 * @author Darren Creutz <dcreutz@gmail.com>
 */
public class BindingLocater {
	private final Class<?> theClass;
	private final ModuleContextRepresentation module;
	private final BindingCodeLocation location;
	private final HashSet<CodeProblem> problems;
	
	/**
	 * Create a BindingLocater for locating the binding of the given class when using the given module.
	 * 
	 * @param theClass the class to find bindings of
	 * @param module the module context to find bindings in
	 */
	public BindingLocater(Class<?> theClass,ModuleContextRepresentation module) {
		this.theClass = theClass;
		this.module = module;
		problems = new HashSet<CodeProblem>();
		if (module.isValid()) {
			String bindTo = null;
			StackTraceElement source = null;
			Binding binding = null;
			Injector injector = null;
			try {
				injector = module.getInjector();
				binding = injector.getBinding(Key.get(theClass));
				if (binding == null) {
					problems.add(new CodeProblem.NoBindingProblem(module,theClass));
				} else {
					source = (StackTraceElement)binding.getSource();
					Provider provider = binding.getProvider();
					bindTo = provider.get().getClass().getName();
				}
			//} catch (OutOfScopeException exception) {
			//	problems.add(new CodeProblem.OutOfScopeProblem(module,theClass,exception));
			} catch (Exception exception) {
				problems.add(new CodeProblem.BindingProblem(module,theClass,exception));
			}
			location = (source==null || bindTo==null) ? null :
				new BindingCodeLocation(theClass,bindTo,module,source.getFileName(),source.getLineNumber(),problems);
		} else {
			location = null;
			problems.add(new CodeProblem.InvalidModuleContextProblem(module));
		}
	}
	
	/**
	 * Return the class we are finding bindings for.
	 * 
	 * @return the class
	 */
	public Class getTheClass() {
		return theClass;
	}
	
	/**
	 * Return the module context we are running in.
	 * 
	 * @return the module context
	 */
	public ModuleContextRepresentation getModule() {
		return module;
	}
	
	/**
	 * Return the {@link BindingCodeLocation} encapsulating the results.
	 * 
	 * @return the result
	 */
	public BindingCodeLocation getLocation() {
		return location;
	}
	
	/**
	 * Return the set of {@link CodeProblem}s encountered while finding bindings.
	 * 
	 * @return the problems
	 */
	public Set<CodeProblem> getProblems() {
		return new HashSet<CodeProblem>(problems);
	}
}
