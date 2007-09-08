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

package com.google.inject.tools.ideplugin.results;

import com.google.inject.tools.suite.module.ClassNameUtility;
import com.google.inject.tools.suite.snippets.CodeLocation;
import com.google.inject.tools.suite.snippets.problems.CodeProblem;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Represents the results of a code location search, such as finding the
 * bindings of something. Builds a tree structure of the results for display
 * purposes.
 * 
 * @author Darren Creutz (dcreutz@gmail.com)
 */
public class CodeLocationsResults extends Results {
  /**
   * A node that denotes a new module.
   */
  public static class ModuleNode extends Node {
    public ModuleNode(String name) {
      super("in " + ClassNameUtility.shorten(name), "Results for " + name);
    }
  }
  
  /**
   * A node in the tree that holds a {@link CodeLocation}.
   */
  public static class CodeLocationNode extends Node {
    private final CodeLocation location;
    
    /**
     * Create a CodeLocationNode. Its children will be the actual location and
     * nodes for each {@link CodeProblem} involved.
     * 
     * @param location the {@link CodeLocation}
     */
    public CodeLocationNode(CodeLocation location) {
      super(ActionStringBuilder.getDisplayString(location));
      this.location = location;
      if (!location.getProblems().isEmpty()) {
        Node node = new ProblemsNode();
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
   * A node that says Problems.
   */
  public static class ProblemsNode extends Node {
    public ProblemsNode() {
      super("Problems", null);
    }
  }

  /**
   * A Node representing a {@link CodeProblem}.
   */
  public static class ProblemNode extends Node {
    private final CodeProblem problem;

    /**
     * Create a ProblemNode for the given {@link CodeProblem}.
     * 
     * @param problem the problem
     */
    public ProblemNode(CodeProblem problem) {
      super(ActionStringBuilder.getDisplayString(problem));
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

  private final Map<String, Set<CodeLocation>> map;

  /**
   * Create a new CodeLocationsResults object with the given title.
   * 
   * @param title the display title
   * @param tooltip the text (if any) to display when the mouse is over the
   *        title during display
   */
  public CodeLocationsResults(String title, String tooltip) {
    super(title, tooltip);
    map = new HashMap<String, Set<CodeLocation>>();
  }

  /**
   * Add a {@link com.google.inject.tools.suite.module.ModuleContextRepresentation} to
   * {@link CodeLocation} pairing to the results.
   * 
   * @param module the module context
   * @param locations the code locations
   */
  public synchronized void put(String module, Set<CodeLocation> locations, 
      Set<? extends CodeProblem> problems) {
    if (map.get(module) != null) {
      map.get(module).addAll(locations);
    } else {
      map.put(module, locations);
    }
    ModuleNode node = new ModuleNode(module);
    getRoot().addChild(node);
    for (CodeLocation location : locations) {
      node.addChild(new CodeLocationNode(location));
    }
    if (!problems.isEmpty()) {
      ProblemsNode problemsNode = new ProblemsNode();
      node.addChild(problemsNode);
      for (CodeProblem problem : problems) {
        problemsNode.addChild(new ProblemNode(problem));
      }
    }
  }

  /**
   * Return all the
   * {@link com.google.inject.tools.suite.module.ModuleContextRepresentation}s in the
   * results.
   * 
   * @return the modules
   */
  public synchronized Set<String> keySet() {
    return map.keySet();
  }

  /**
   * Return the {@link CodeLocation} in this results for the given
   * {@link com.google.inject.tools.suite.module.ModuleContextRepresentation}.
   * 
   * @param module the module context
   * @return the code location
   */
  public synchronized Set<? extends CodeLocation> get(String module) {
    return map.get(module);
  }

  @Override
  public String toString() {
    StringBuilder result = new StringBuilder();
    result.append("Code Locations: {");
    for (String module : map.keySet()) {
      result.append(module + " ==> " + map.get(module).toString());
    }
    result.append("}");
    return result.toString();
  }
}
