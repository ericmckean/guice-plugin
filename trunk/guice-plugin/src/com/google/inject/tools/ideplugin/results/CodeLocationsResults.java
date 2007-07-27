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

package com.google.inject.tools.ideplugin.results;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import com.google.inject.tools.ideplugin.code.CodeLocation;
import com.google.inject.tools.ideplugin.code.CodeProblem;
import com.google.inject.tools.ideplugin.module.ModuleContextRepresentation;
import com.google.inject.tools.ideplugin.ActionsHandler;

/**
 * Represents the results of a code location search, such as finding the bindings of something.
 * Builds a tree structure of the results for display purposes.
 * 
 * @author Darren Creutz <dcreutz@gmail.com>
 */
public class CodeLocationsResults extends Results {
	/**
	 * A node in the tree that holds a {@link CodeLocation}.
	 */
	public class CodeLocationNode extends Node {
		private final CodeLocation location;
		
		/**
		 * Create a CodeLocationNode.  Its children will be the actual location and nodes for
		 * each {@link CodeProblem} involved.
		 * 
		 * @param name the display name of the node
		 * @param location the {@link CodeLocation}
		 */
		public CodeLocationNode(String name,CodeLocation location) {
			super(name);
			this.location = location;
			if (location.file() != null) {
				addChild(new ClickableNode("Bound to " + location.getDisplayName() + " at " + location.file() + ":" + location.location(),new ActionsHandler.GotoCodeLocation(location.file(),location.location())));
			}
			if (!location.getProblems().isEmpty()) {
				Node node = new Node("Problems");
				for (CodeProblem problem : location.getProblems()) {
					node.addChild(new ProblemNode(problem));
				}
				addChild(node);
			}
		}
		
		/**
		 * Return the {@link CodeLocation}.
		 * 
		 * @return the code location
		 */
		public CodeLocation getLocation() {
			return location;
		}
	}
	
	/**
	 * A Node representing a {@link CodeProblem}.
	 */
	public class ProblemNode extends Node {
		private final CodeProblem problem;
		
		/**
		 * Create a ProblemNode for the given {@link CodeProblem}.
		 * 
		 * @param problem the problem
		 */
		public ProblemNode(CodeProblem problem) {
			super(problem.toString());
			this.problem = problem;
		}
		
		/**
		 * Return the {@link CodeProblem}.
		 * 
		 * @return the problem
		 */
		public CodeProblem getProblem() {
			return problem;
		}
	}
	
	private final Map<ModuleContextRepresentation,CodeLocation> map;
	
	/**
	 * Create a new CodeLocationsResults object with the given title.
	 * 
	 * @param title the display title
	 */
	public CodeLocationsResults(String title) {
		super(title);
		map = new HashMap<ModuleContextRepresentation,CodeLocation>();
	}
	
	/**
	 * Add a {@link ModuleContextRepresentation} to {@link CodeLocation} pairing to the results.
	 * 
	 * @param module the module context
	 * @param location the code location
	 */
	public synchronized void put(ModuleContextRepresentation module,CodeLocation location) {
		map.put(module,location);
		getRoot().addChild(new CodeLocationNode(module.getName(),location));
	}
	
	/**
	 * Return all the {@link ModuleContextRepresentation}s in the results.
	 * 
	 * @return the modules
	 */
	public synchronized Set<ModuleContextRepresentation> keySet() {
		return map.keySet();
	}
	
	/**
	 * Return the {@link CodeLocation} in this results for the given {@link ModuleContextRepresentation}.
	 * 
	 * @param module the module context
	 * @return the code location
	 */
	public synchronized CodeLocation get(ModuleContextRepresentation module) {
		return map.get(module);
	}
}
