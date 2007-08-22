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
import com.google.inject.tools.ideplugin.results.Results;
import com.google.inject.tools.snippets.CodeLocation;
import com.google.inject.tools.snippets.CodeProblem;

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
  public static class CodeLocationNode extends Node {
    private final CodeLocation location;
    
    /**
     * Create a CodeLocationNode.  Its children will be the actual location and nodes for
     * each {@link CodeProblem} involved.
     * 
     * @param name the display name of the node
     * @param location the {@link CodeLocation}
     */
    public CodeLocationNode(String name,CodeLocation location) {
      super("in " + shorten(name),"Results for " + name);
      this.location = location;
      if (location.file() != null) {
        addChild(new Node(location.getDisplay()));
      }
      if (!location.getProblems().isEmpty()) {
        Node node = new Node("Problems",null);
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
  public static class ProblemNode extends Node {
    private final CodeProblem problem;
    
    /**
     * Create a ProblemNode for the given {@link CodeProblem}.
     * 
     * @param problem the problem
     */
    public ProblemNode(CodeProblem problem) {
      super(problem.getDisplay());
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
  
  private final Map<String,CodeLocation> map;
  
  /**
   * Create a new CodeLocationsResults object with the given title.
   * 
   * @param title the display title
   * @param tooltip the text (if any) to display when the mouse is over the title during display
   */
  public CodeLocationsResults(String title, String tooltip) {
    super(title, tooltip);
    map = new HashMap<String,CodeLocation>();
  }
  
  /**
   * Add a {@link com.google.inject.tools.module.ModuleContextRepresentation} to {@link CodeLocation} pairing to the results.
   * 
   * @param module the module context
   * @param location the code location
   */
  public synchronized void put(String module,CodeLocation location) {
    map.put(module,location);
    getRoot().addChild(new CodeLocationNode(module,location));
  }
  
  /**
   * Return all the {@link com.google.inject.tools.module.ModuleContextRepresentation}s in the results.
   * 
   * @return the modules
   */
  public synchronized Set<String> keySet() {
    return map.keySet();
  }
  
  /**
   * Return the {@link CodeLocation} in this results for the given {@link com.google.inject.tools.module.ModuleContextRepresentation}.
   * 
   * @param module the module context
   * @return the code location
   */
  public synchronized CodeLocation get(String module) {
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
  
  /**
   * Shorten a class name to its simple name.
   */
  public static String shorten(String label) {
    return label.substring(label.lastIndexOf(".")+1);
  }
}