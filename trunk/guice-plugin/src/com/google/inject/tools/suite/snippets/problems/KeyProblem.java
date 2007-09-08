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

package com.google.inject.tools.suite.snippets.problems;

/**
 * Represents a problem with a Guice key.
 * 
 * @author Darren Creutz (dcreutz@gmail.com)
 */
public class KeyProblem extends CodeProblem {
  private final String bindWhat;
  
  public KeyProblem(Throwable throwable) {
    this(null, throwable);
  }
  
  public KeyProblem(String bindWhat, Throwable throwable) {
    super(throwable);
    this.bindWhat = bindWhat;
  }
  
  public String bindWhat() {
    return bindWhat;
  }
  
  @Override
  public String toString() {
    return "Guice Key Problem: " + bindWhat + " " + getMessage();
  }
}
