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

package com.google.inject.tools.suite.module;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.google.inject.tools.suite.snippets.CodeLocation;
import com.google.inject.tools.suite.snippets.problems.CodeProblem;

/**
 * Representation of a module context in the user's code.
 * 
 * @author Darren Creutz (dcreutz@gmail.com)
 */
public interface ModuleContextRepresentation {
  /**
   * Representation of an instance of a {@link com.google.inject.Module} in the
   * user's code, including which constructor to use and how to use it.
   * 
   * @author Darren Creutz (dcreutz@gmail.com)
   */
  public class ModuleInstanceRepresentation {
    private static class Argument {
      private final String type;
      private final String value;

      public Argument(String type, String value) {
        this.type = type;
        this.value = value;
      }

      public String type() {
        return type;
      }

      public String value() {
        return value;
      }
    }

    private final String className;
    private final List<Argument> arguments;

    /**
     * Create a representation of a module instance using its default
     * constructor.
     * 
     * @param className the class of the module
     */
    public ModuleInstanceRepresentation(String className) {
      this.className = className;
      this.arguments = new ArrayList<Argument>();
    }

    /**
     * Create a representation of a module instance using the constructor with
     * the given argument set.
     * 
     * @param className the class of the module
     * @param argumentTypes the classes/types of the arguments
     */
    public ModuleInstanceRepresentation(String className,
        List<String> argumentTypes) {
      this.className = className;
      this.arguments = new ArrayList<Argument>();
      for (String argumentType : argumentTypes) {
        this.arguments.add(new Argument(argumentType, "null"));
      }
    }

    /**
     * Create a representation of a module instance using the given constructor
     * arguments.
     * 
     * @param className the class of the module
     * @param argumentTypes the classes/types of the arguments
     * @param argumentValues the values of the arguments
     */
    public ModuleInstanceRepresentation(String className,
        List<String> argumentTypes, List<String> argumentValues) {
      this.className = className;
      this.arguments = new ArrayList<Argument>();
      for (int i = 0; i < argumentTypes.size(); i++) {
        this.arguments.add(new Argument(argumentTypes.get(i), argumentValues
            .get(i)));
      }
    }

    /**
     * Return the class of the module.
     */
    public String getClassName() {
      return className;
    }

    /**
     * Return the module instance as a string list in preparation for passing it
     * to a {@link com.google.inject.tools.suite.snippets.CodeSnippet}.
     */
    public List<String> toStringList() {
      List<String> result = new ArrayList<String>();
      result.add(className);
      result.add(String.valueOf(arguments.size()));
      for (int i = 0; i < arguments.size(); i++) {
        result.add(arguments.get(i).type());
        result.add(arguments.get(i).value());
      }
      return result;
    }

    /**
     * Return a string of how the module instance is created.
     */
    public String getCreationString() {
      StringBuilder text = new StringBuilder();
      text.append("new " + className + "(");
      int count = 0;
      for (Argument argument : arguments) {
        text.append(argument.value());
        count++;
        if (count < arguments.size()) {
          text.append(", ");
        }
      }
      text.append(")");
      return text.toString();
    }

    @Override
    public boolean equals(Object object) {
      if (!(object instanceof ModuleInstanceRepresentation)) {
        return false;
      }
      ModuleInstanceRepresentation otherModule =
          (ModuleInstanceRepresentation) object;
      return otherModule.getClassName().equals(getClassName());
    }

    @Override
    public int hashCode() {
      return getClassName().hashCode();
    }
  }

  /**
   * Find the location in code where a binding occurs in this module context.
   * 
   * @param theClass the class to find the binding for
   * @param annotatedWith the annotations on this class to find bindings for
   * @return the location in code and/or problems finding the binding
   */
  public CodeLocation findLocation(String theClass, String annotatedWith);
  
  /**
   * Find the locations in code where bindings occur in this context.
   * 
   * @param theClass the class to find bindings for
   * @return the locations in code and/or problems in finding bindings
   */
  public Set<CodeLocation> findLocations(String theClass);

  /**
   * Add the module with the given name to this context.
   */
  public ModuleContextRepresentation add(ModuleInstanceRepresentation module);

  /**
   * Return the modules in this context.
   */
  public Set<ModuleInstanceRepresentation> getModules();

  /**
   * Remove the given module from this context.
   */
  public void removeModule(ModuleInstanceRepresentation module);

  /**
   * Return the fully qualified name of this representation.
   */
  public String getName();

  /**
   * Return the long name of this representation (with a description)
   */
  public String getLongName();

  /**
   * Return the short name of this representation.
   */
  public String getShortName();

  /**
   * Return true if this context has the given module in it.
   */
  public boolean contains(ModuleInstanceRepresentation module);

  /**
   * Return true if this context has the given module in it.
   */
  public boolean contains(String moduleName);

  /**
   * Return the set of {@link CodeProblem}s occurring with this context.
   */
  public Set<? extends CodeProblem> getProblems();
  
  /**
   * Add a module to the context.
   */
  public ModuleContextRepresentation addModule(String moduleName);
}
