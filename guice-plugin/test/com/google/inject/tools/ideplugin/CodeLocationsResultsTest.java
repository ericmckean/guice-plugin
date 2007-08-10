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

package com.google.inject.tools.ideplugin ;

import junit.framework.TestCase;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import com.google.inject.tools.ideplugin.test.MockCreationException;
import com.google.inject.tools.ideplugin.snippets.BindingCodeLocation;
import com.google.inject.tools.ideplugin.snippets.CodeProblem;
import com.google.inject.tools.ideplugin.snippets.CodeLocation;
import com.google.inject.tools.ideplugin.results.CodeLocationsResults;
import com.google.inject.tools.ideplugin.results.Results;
import com.google.inject.tools.ideplugin.results.CodeLocationsResults.ProblemNode;
import com.google.inject.tools.ideplugin.results.Results.Node;

/**
 * Unit test the {@link CodeLocationsResults} object.
 *
 * @author Darren Creutz <dcreutz@gmail.com>
 */
public class CodeLocationsResultsTest extends TestCase {
  
  /**
   * Test that the CodeResults object correctly builds node trees.
   */
  public void testValidLocationNodeTree() {
    final CodeLocationsResults results = createValidCodeLocationsResults();
    assertTrue(results.getRoot().equals(expectedResultForValidLocation()));
  }
  
  /**
   * Creating a {@link CodeLocationsResults} with one valid module and code
   * location and one broken module and corresponding problems.
   */
  public void testBrokenAndValidLocationsNodeTree() {
    final CodeLocationsResults results = createBrokenCodeLocationsResults();
    assertTrue(results.getRoot().equals(expectedResultForBothLocations()));
    assertFalse(results.getRoot().equals(expectedResultForValidLocation()));
  }
  
  private CodeLocationsResults createValidCodeLocationsResults() {
    CodeLocation validLocation =
      new BindingCodeLocation("com.google.inject.tools.ideplugin.JavaElement",
          "com.google.inject.tools.ideplugin.test.MockJavaElement",
          "Valid Module Context",
          "MockGuicePlugin.java", 145,
          new HashSet<CodeProblem>());
    CodeLocationsResults results = new CodeLocationsResults("Test Results");
    results.put("Valid Module Context", validLocation);
    return results;
  }
  
  private CodeLocationsResults createBrokenCodeLocationsResults() {
    CodeLocation validLocation =
      new BindingCodeLocation("com.google.inject.tools.ideplugin.JavaElement",
          "com.google.inject.tools.ideplugin.test.MockJavaElement",
          "Valid Module Context",
          "MockGuicePlugin.java", 145,
          new HashSet<CodeProblem>());
    
    CodeLocation problemsLocation =
      new BindingCodeLocation("com.google.inject.tools.ideplugin.JavaElement",
          (String)null,
          "Broken Module Context",
          (String)null, 0,
          makeProblemSet("BrokenModule"));
    
    CodeLocationsResults results = new CodeLocationsResults("Test Results");
    results.put("Valid Module Context", validLocation);
    results.put("Broken Module Context", problemsLocation);
    return results;
  }
  
  private Set<? extends CodeProblem> makeProblemSet(String module) {
    return Collections.singleton(new CodeProblem.CreationProblem(module,new MockCreationException()));
  }
  
  private Results.Node expectedResultForValidLocation() {
    Results.Node root = new Node("Test Results");
    Results.Node module = new Node("Valid Module Context");
    module.addChild(new Node("com.google.inject.tools.ideplugin.JavaElement is bound to com.google.inject.tools.ideplugin.test.MockJavaElement at MockGuicePlugin.java:145"));
    root.addChild(module);
    return root;
  }
  private Results.Node expectedResultForBothLocations() {
    Results.Node root = new Node("Test Results");
    Results.Node module = new Node("Valid Module Context");
    module.addChild(new Node("com.google.inject.tools.ideplugin.JavaElement is bound to com.google.inject.tools.ideplugin.test.MockJavaElement at MockGuicePlugin.java:145"));
    Results.Node module2 = new Node("Broken Module Context");
    Results.Node problems = new Node("Problems");
    module2.addChild(problems);
    problems.addChild(new ProblemNode(new CodeProblem.CreationProblem("BrokenModule", new MockCreationException())));
    root.addChild(module2);
    root.addChild(module);
    return root;
  }
}