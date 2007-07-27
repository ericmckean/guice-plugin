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

package com.google.inject.tools.ideplugin.code;

import com.google.inject.tools.ideplugin.module.ModuleContextRepresentation;
import com.google.inject.CreationException;

/**
 * Represents a problem found involving the user's guice code, such as a CreationException.
 * These are passed to the {@link com.google.inject.tools.ideplugin.problem.ProblemsHandler} for realtime notification to the user
 * as well as passed along with {@link CodeLocation}s for display as results.
 * 
 * @author Darren Creutz <dcreutz@gmail.com>
 */
public class CodeProblem {
	/**
	 * Represents a CreationException problem.
	 */
	public static class CreationProblem extends CodeProblem {
		/**
		 * Create a CreationException problem.
		 * 
		 * @param module the module context involved
		 * @param exception the underlying exception
		 */
		public CreationProblem(ModuleContextRepresentation module,CreationException exception) {
			super(module,exception);
		}
		
		/**
		 * (non-Javadoc)
		 * @see com.google.inject.tools.ideplugin.code.CodeProblem#toString()
		 */
		@Override
		public String toString() {
			return super.toString();
		}
	}
	
	/**
	 * Represents a problem with the binding of the class being injected.
	 */
	public static class BindingProblem extends CodeProblem {
		protected final Class<?> theClass;
		
		/**
		 * Create a BindingProblem.
		 * 
		 * @param module the module context
		 * @param exception the underlyng exception
		 * @param theClass the class being injected
		 */
		public BindingProblem(ModuleContextRepresentation module,Class<?> theClass,Exception exception) {
			super(module,exception);
			this.theClass = theClass;
		}
		
		/**
		 * Return the class being injected when the problem occurred.
		 * 
		 * @return the class
		 */
		public Class<?> getTheClass() {
			return theClass;
		}
		
		/**
		 * (non-Javadoc)
		 * @see com.google.inject.tools.ideplugin.code.CodeProblem#toString()
		 */
		@Override
		public String toString() {
			return "Guice Code Problem: " + theClass.toString() + " has no binding in Module " + module.getName();
		}
	}
	
	/**
	 * Represents an OutOfScopeException during injection.
	 */
	public static class OutOfScopeProblem extends BindingProblem {
		/**
		 * Create an OutOfScopeProblem.
		 * 
		 * @param module the module context
		 * @param theClass the class
		 * @param exception the underlying exception
		 */
		public OutOfScopeProblem(ModuleContextRepresentation module,Class<?> theClass,Exception exception) {
			super(module,theClass,exception);
		}
		
		/**
		 * (non-Javadoc)
		 * @see com.google.inject.tools.ideplugin.code.CodeProblem#toString()
		 */
		@Override
		public String toString() {
			return super.toString();
		}
	}
	
	/**
	 * Represents that no binding is defined for the class being injected.
	 */
	public static class NoBindingProblem extends BindingProblem {
		/**
		 * Create a NoBindingProblem.
		 * 
		 * @param module the module context
		 * @param theClass the class being injected
		 */
		public NoBindingProblem(ModuleContextRepresentation module,Class<?> theClass) {
			super(module,theClass,null);
		}
		
		/**
		 * (non-Javadoc)
		 * @see com.google.inject.tools.ideplugin.code.CodeProblem#toString()
		 */
		@Override
		public String toString() {
			return "Guice Code Problem: " + theClass.toString() + " has no binding in Module " + module.getName();
		}
	}
	
	/**
	 * Represents a problem that the module context is invalid.
	 */
	public static class InvalidModuleContextProblem extends CodeProblem {
		/**
		 * Create an InvalidModuleContextProblem.
		 * 
		 * @param module the invalid module
		 */
		public InvalidModuleContextProblem(ModuleContextRepresentation module) {
			super(module,null);
		}
		
		/**
		 * (non-Javadoc)
		 * @see com.google.inject.tools.ideplugin.code.CodeProblem#toString()
		 */
		@Override
		public String toString() {
			return "Guice Module Context is invalid: " + module;
		}
	}

	protected final Exception exception;
	protected final ModuleContextRepresentation module;
	
	/**
	 * Create a CodeProblem representation.
	 * 
	 * @param module the module context the problem occurred in
	 * @param exception the underlying exception
	 */
	public CodeProblem(ModuleContextRepresentation module,Exception exception) {
		this.module = module;
		this.exception = exception;
	}
	
	/**
	 * Return the problem as a String.
	 * 
	 * @return the problem string
	 */
	public String toString() {
		return "Guice Code Problem: " + exception.toString();
	}
	
	/**
	 * Return the module context the problem occurred in.
	 * 
	 * @return the {@link ModuleContextRepresentation}
	 */
	public ModuleContextRepresentation getModule() {
		return module;
	}
	
	/**
	 * Return the underlying exception.
	 * 
	 * @return the exception
	 */
	public Exception getException() {
		return exception;
	}
}
