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

package com.google.inject.tools.ideplugin.module;

import java.util.ArrayList;
import java.util.List;

/**
 * Representation of an instance of a {@link com.google.inject.Module} in the user's code, including which
 * constructor to use and how to use it.
 * 
 * @author Darren Creutz <dcreutz@gmail.com>
 */
public class ModuleInstanceRepresentation {
  private static class Argument {
    private final String type;
    private final String value;
    public Argument(String type,String value) {
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
   * Create a representation of a module instance using its default constructor.
   * 
   * @param className the class of the module
   */
  public ModuleInstanceRepresentation(String className) {
    this.className = className;
    this.arguments = new ArrayList<Argument>();
  }
  
  /**
   * Create a representation of a module instance using the constructor with the given argument set.
   * 
   * @param className the class of the module
   * @param argumentTypes the classes/types of the arguments
   */
  public ModuleInstanceRepresentation(String className,List<String> argumentTypes) {
    this.className = className;
    this.arguments = new ArrayList<Argument>();
    for (String argumentType : argumentTypes) {
      this.arguments.add(new Argument(argumentType,"null"));
    }
  }
  
  /**
   * Create a representation of a module instance using the given constructor arguments.
   * 
   * @param className the class of the module
   * @param argumentTypes the classes/types of the arguments
   * @param argumentValues the values of the arguments
   */
  public ModuleInstanceRepresentation(String className,List<String> argumentTypes,List<String> argumentValues) {
    this.className = className;
    this.arguments = new ArrayList<Argument>();
    for (int i=0;i<argumentTypes.size();i++) {
      this.arguments.add(new Argument(argumentTypes.get(i),argumentValues.get(i)));
    }
  }
  
  /**
   * Return the class of the module.
   */
  public String getClassName() {
    return className;
  }
  
  /**
   * Return the module instance as a string list in preparation for passing it to a {@link com.google.inject.tools.ideplugin.snippets.CodeSnippet}.
   */
  public List<String> toStringList() {
    List<String> result = new ArrayList<String>();
    result.add(className);
    result.add(String.valueOf(arguments.size()));
    for (int i=0;i<arguments.size();i++) {
      result.add(arguments.get(i).type());
      result.add(arguments.get(i).value());
    }
    return result;
  }
}
