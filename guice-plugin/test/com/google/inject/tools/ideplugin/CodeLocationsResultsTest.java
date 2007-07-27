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

package com.google.inject.tools.ideplugin;

import junit.framework.TestCase;
import java.util.HashSet;
import com.google.inject.tools.ideplugin.test.MockCreationException;
import com.google.inject.tools.ideplugin.bindings.BindingCodeLocation;
import com.google.inject.tools.ideplugin.module.ModuleContextRepresentation;
import com.google.inject.tools.ideplugin.module.ModuleContextRepresentationImpl;
import com.google.inject.tools.ideplugin.module.ModuleRepresentationImpl;
import com.google.inject.tools.ideplugin.code.CodeLocation;
import com.google.inject.tools.ideplugin.code.CodeProblem;
import com.google.inject.tools.ideplugin.results.CodeLocationsResults;
import com.google.inject.tools.ideplugin.results.Results;
import com.google.inject.tools.ideplugin.test.MockGuicePluginModule;
import com.google.inject.tools.ideplugin.test.BrokenModule;

/**
 * Unit test the LocationsCodeResults object.
 * 
 * @author Darren Creutz <dcreutz@gmail.com>
 */
public class CodeLocationsResultsTest extends TestCase {
	private ModuleContextRepresentation validModule = 
		new ModuleContextRepresentationImpl("Valid Module Context")
			.add(new ModuleRepresentationImpl(MockGuicePluginModule.class));
	private CodeLocation validLocation = 
		new BindingCodeLocation(JavaElement.class,
				"com.google.inject.tools.ideplugin.test.MockJavaElement",
				validModule,
				"MockGuicePlugin.java", 145, 
				new HashSet<CodeProblem>());
	
	private ModuleContextRepresentation brokenModule = 
		new ModuleContextRepresentationImpl("Broken Module Context")
			.add(new ModuleRepresentationImpl(BrokenModule.class));
	private HashSet<CodeProblem> makeProblemSet() {
		HashSet<CodeProblem> theSet = new HashSet<CodeProblem>();
		theSet.add(new CodeProblem.CreationProblem(brokenModule,new MockCreationException()));
		return theSet;
	}
	private CodeLocation problemsLocation =
		new BindingCodeLocation(JavaElement.class,
				(String)null,
				brokenModule,
				(String)null, 0,
				makeProblemSet());
	
	private CodeLocationsResults results;
	private Results.Node validLocationsRoot;
	private Results.Node makeValidLocationsRoot() {
		Results.Node root = results.new Node("Test Results");
		Results.Node module = results.new Node("Valid Module Context");
		module.addChild(results.new Node("Bound to com.google.inject.tools.ideplugin.test.MockJavaElement at MockGuicePlugin.java:145"));
		root.addChild(module);
		return root;
	}
	private Results.Node bothLocationsRoot;
	private Results.Node makeBothLocationsRoot() {
		Results.Node root = results.new Node("Test Results");
		Results.Node module = results.new Node("Valid Module Context");
		module.addChild(results.new Node("Bound to com.google.inject.tools.ideplugin.test.MockJavaElement at MockGuicePlugin.java:145"));
		root.addChild(module);
		Results.Node module2 = results.new Node("Broken Module Context");
		root.addChild(module2);
		Results.Node problems = results.new Node("Problems");
		module2.addChild(problems);
		problems.addChild(results.new Node("Guice Code Problem: Mock Creation Exception."));
		return root;
	}
	
	/**
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() {
		results = new CodeLocationsResults("Test Results");
		validLocationsRoot = makeValidLocationsRoot();
		bothLocationsRoot = makeBothLocationsRoot();
	}
	
	/**
	 * Test that the CodeResults object correctly builds node trees.
	 */
	public void testNodeTree() {
		results.put(validModule, validLocation);
		assertTrue(checkNodesMatch(results.getRoot(),validLocationsRoot));
		results.put(brokenModule, problemsLocation);
		assertTrue(checkNodesMatch(results.getRoot(),bothLocationsRoot));
		assertFalse(checkNodesMatch(results.getRoot(),validLocationsRoot));
	}
	
	private boolean checkNodesMatch(Results.Node root,Results.Node desiredRoot) {
		if (root.getTitle().equals(desiredRoot.getTitle())) {
			if (root.children().size() == desiredRoot.children().size()) {
				for (Results.Node node : root.children()) {
					boolean foundMatch = false;
					for (Results.Node desiredNode : desiredRoot.children()) {
						if (checkNodesMatch(node,desiredNode)) foundMatch = true;
					}
					if (!foundMatch) return false;
				}
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
}
