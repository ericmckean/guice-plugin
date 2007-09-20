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
import com.google.inject.tools.suite.snippets.BindingCodeLocation;
import com.google.inject.tools.suite.snippets.CodeLocation;
import com.google.inject.tools.suite.snippets.BindingCodeLocation.ImplicitBindingLocation;
import com.google.inject.tools.suite.snippets.BindingCodeLocation.LinkedToBindingCodeLocation;
import com.google.inject.tools.suite.snippets.BindingCodeLocation.NoBindingLocation;
import com.google.inject.tools.suite.snippets.CodeLocation.CodeLocationVisitor;
import com.google.inject.tools.suite.snippets.problems.CodeProblem;

import java.util.HashSet;
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
  static class ModuleNode extends Node implements CodeLocationVisitor {
    private final String name;
    private final Set<CodeProblem> problems;
    private final Set<CodeLocation> locations;
    private ProblemsNode problemsNode;
    
    public ModuleNode(String name) {
      super("in " + ClassNameUtility.shorten(name), "Results for " + name);
      this.name = name;
      problems = new HashSet<CodeProblem>();
      locations = new HashSet<CodeLocation>();
      problemsNode = null;
    }
    
    public String module() {
      return name;
    }
    
    public void addLocations(Set<? extends CodeLocation> locations) {
      this.locations.addAll(locations);
      for (CodeLocation location : locations) {
        location.accept(this);
      }
    }
    
    public void addProblems(Set<? extends CodeProblem> problems) {
      if (this.problems.isEmpty() && !problems.isEmpty()) {
        problemsNode = new ProblemsNode();
        addChild(problemsNode);
      }
      this.problems.addAll(problems);
      for (CodeProblem problem : problems) {
        problemsNode.addChild(new ProblemNode(problem));
      }
    }
    
    public Set<? extends CodeLocation> locations() {
      return locations;
    }
    
    public Set<? extends CodeProblem> problems() {
      return problems;
    }
    
    public void visit(BindingCodeLocation location) {
      addChild(new BindingCodeLocationNode(location));
    }
    
    public void visit(NoBindingLocation location) {
      addChild(new NoBindingCodeLocationNode(location));
    }
    
    public void visit(ImplicitBindingLocation location) {
      addChild(new ImplicitBindingCodeLocationNode(location));
    }
    
    public void visit(LinkedToBindingCodeLocation location) {
      //do nothing
    }
  }
  
  /**
   * A node in the tree that holds a {@link CodeLocation}.
   */
  static class CodeLocationNode extends Node {
    private final CodeLocation location;
    
    /**
     * Create a CodeLocationNode. Its children will be the actual location and
     * nodes for each {@link CodeProblem} involved.
     * 
     * @param location the {@link CodeLocation}
     */
    public CodeLocationNode(CodeLocation location) {
      super(new ActionStringBuilder(location).getActionString());
      this.location = location;
    }
    
    protected void createProblemsNode() {
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
  
  static class BindingCodeLocationNode extends CodeLocationNode {
    public BindingCodeLocationNode(BindingCodeLocation location) {
      super(location);
      if (location.linkedTo() != null && 
          !(location.linkedTo().file().equals(location.file())
              && location.linkedTo().location() == location.location())) {
        addChild(new LinkedToBindingCodeLocationNode(location.linkedTo()));
      }
    }
  }
  
  static class LinkedToBindingCodeLocationNode extends Node {
    public LinkedToBindingCodeLocationNode(BindingCodeLocation location) {
      super(new ActionStringBuilder(location).getActionString());
    }
  }
  
  static class NoBindingCodeLocationNode extends CodeLocationNode {
    public NoBindingCodeLocationNode(NoBindingLocation location) {
      super(location);
      createProblemsNode();
    }
  }
  
  static class ImplicitBindingCodeLocationNode extends CodeLocationNode {
    public ImplicitBindingCodeLocationNode(ImplicitBindingLocation location) {
      super(location);
      createProblemsNode();
    }
  }
  
  /**
   * A node that says Problems.
   */
  static class ProblemsNode extends Node {
    public ProblemsNode() {
      super("Problems", null);
    }
  }

  /**
   * A Node representing a {@link CodeProblem}.
   */
  static class ProblemNode extends Node {
    private final CodeProblem problem;

    /**
     * Create a ProblemNode for the given {@link CodeProblem}.
     * 
     * @param problem the problem
     */
    public ProblemNode(CodeProblem problem) {
      super(new ActionStringBuilder(problem).getActionString());
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

  /**
   * Create a new CodeLocationsResults object with the given title.
   * 
   * @param title the display title
   * @param tooltip the text (if any) to display when the mouse is over the
   *        title during display
   */
  public CodeLocationsResults(String title, String tooltip) {
    super(title, tooltip);
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
    ModuleNode node = null;
    for (Node child : getRoot().children()) {
      if (child instanceof ModuleNode) {
        if (((ModuleNode)child).module().equals(module)) {
          node = (ModuleNode)child;
        }
      }
    }
    if (node == null) {
      node = new ModuleNode(module);
      getRoot().addChild(node);
    }
    node.addLocations(locations);
    node.addProblems(problems);
  }

  @Override
  public String toString() {
    StringBuilder result = new StringBuilder();
    result.append("Code Locations: {");
    for (Node node : getRoot().children()) {
      ModuleNode moduleNode = (ModuleNode)node;
      result.append(moduleNode.module() + " ==> " + moduleNode.locations().toString());
    }
    result.append("}");
    return result.toString();
  }
}
