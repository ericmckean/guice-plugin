/**
 * Copyright (C) 2007 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.inject.tools.ideplugin;

import junit.framework.TestCase;
import java.util.Collections;
import java.util.Set;

import com.google.inject.tools.ideplugin.results.ActionStringBuilder;
import com.google.inject.tools.ideplugin.results.CodeLocationsResults;
import com.google.inject.tools.ideplugin.results.Results;
import com.google.inject.tools.ideplugin.results.Results.Node;
import com.google.inject.tools.suite.Fakes.FakeCreationException;
import com.google.inject.tools.suite.snippets.BindingCodeLocation;
import com.google.inject.tools.suite.snippets.CodeLocation;
import com.google.inject.tools.suite.snippets.problems.CodeProblem;
import com.google.inject.tools.suite.snippets.problems.CreationProblem;

/**
 * Unit test the {@link CodeLocationsResults} object.
 * 
 * @author Darren Creutz (dcreutz@gmail.com)
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
        new BindingCodeLocation(null,
            "com.google.inject.tools.ideplugin.JavaElement", null,
            "com.google.inject.tools.ideplugin.test.MockJavaElement",
            null, null,
            "Valid Module Context", "MockGuicePlugin.java", 145, null, null,
            Collections.<CodeProblem> emptySet());
    CodeLocationsResults results =
        new CodeLocationsResults("Test Results", null);
    results.put("Valid Module Context", Collections.singleton(validLocation), 
        Collections.<CodeProblem>emptySet());
    return results;
  }

  private CodeLocationsResults createBrokenCodeLocationsResults() {
    CodeLocation validLocation =
        new BindingCodeLocation(null,
            "com.google.inject.tools.ideplugin.JavaElement", (String) null,
            "com.google.inject.tools.ideplugin.test.MockJavaElement",
            null, null,
            "Valid Module Context", "MockGuicePlugin.java", 145, null, null,
            Collections.<CodeProblem> emptySet());

    CodeLocation problemsLocation =
        new BindingCodeLocation(null,
            "com.google.inject.tools.ideplugin.JavaElement", (String) null,
            (String) null, null, null,
            "Broken Module Context", (String) null, 0, null, null,
            Collections.<CodeProblem> emptySet());

    CodeLocationsResults results =
        new CodeLocationsResults("Test Results", null);
    results.put("Valid Module Context", Collections.singleton(validLocation),
        Collections.<CodeProblem>emptySet());
    results.put("Broken Module Context", Collections.singleton(problemsLocation),
        makeProblemSet("BrokenModule"));
    return results;
  }

  private Set<? extends CodeProblem> makeProblemSet(String module) {
    return Collections.singleton(new CreationProblem(new FakeCreationException()));
  }

  private Results.Node expectedResultForValidLocation() {
    Results.Node root = new Node("Test Results", null);
    Results.Node module = new Node("in Valid Module Context", null);
    module.addChild(new Node(
        "JavaElement bound to MockJavaElement at MockGuicePlugin.java:145",
        null));
    root.addChild(module);
    return root;
  }

  private Results.Node expectedResultForBothLocations() {
    Results.Node root = new Node("Test Results", null);
    Results.Node module = new Node("in Valid Module Context", null);
    module.addChild(new Node(
        "JavaElement bound to MockJavaElement at MockGuicePlugin.java:145",
        null));
    Results.Node module2 = new Node("in Broken Module Context", null);
    module2.addChild(new Node("JavaElement has an unresolvable binding", null));
    Results.Node problems = new Node("Problems", null);
    module2.addChild(problems);
    problems.addChild(new Node(new ActionStringBuilder(new CreationProblem(
        new FakeCreationException())).getActionString()));
    root.addChild(module);
    root.addChild(module2);
    return root;
  }
}
