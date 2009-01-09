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

package com.google.inject.tools.suite.snippets.bindings;

import com.google.inject.Key;
import com.google.inject.tools.suite.snippets.problems.KeyProblem;

/**
 * Representation of a Guice Key.
 * 
 * @author Darren Creutz (dcreutz@gmail.com)
 */
public class KeyRepresentation extends Representation {
  private static final long serialVersionUID = -1832891974235767811L;
  
  private String bindWhat;
  private String annotatedWith;
  
  public KeyRepresentation(Key<?> key) {
    try {
      this.bindWhat = key.getTypeLiteral().getType().toString();
    } catch (Throwable throwable) {
      problems.add(new KeyProblem(throwable));
      return;
    }
    try {
      String annotatedWith = null;
      if (key.getAnnotation() != null) {
        annotatedWith = key.getAnnotation().toString();
      } else if (key.getAnnotationType() != null) {
        annotatedWith = "@" + key.getAnnotationType().getName();
      }
      this.annotatedWith = annotatedWith;
    } catch (Throwable throwable) {
      problems.add(new KeyProblem(bindWhat, throwable));
    }
  }
  
  public KeyRepresentation(String bindWhat, String annotatedWith) {
    this.bindWhat = bindWhat;
    this.annotatedWith = annotatedWith;
  }
  
  public boolean binds(String theClass) {
    return bindWhat.equals("interface " + theClass) || bindWhat.equals("class " + theClass);
  }
  
  public String bindWhat() {
    return bindWhat;
  }
  
  public String annotatedWith() {
    return annotatedWith;
  }
  
  @Override
  public boolean equals(Object object) {
    if (!(object instanceof KeyRepresentation)) return false;
    KeyRepresentation key = (KeyRepresentation)object;
    if (!bindWhat.equals(key.bindWhat)) return false;
    return annotatedWith==null ? key.annotatedWith==null :
      annotatedWith.equals(key.annotatedWith);
  }
  
  @Override
  public int hashCode() {
    return bindWhat.hashCode();
  }
  
  @Override
  public String toString() {
    if (annotatedWith == null) {
      return "Key binding " + bindWhat;
    } else {
      return "Key binding " + bindWhat + " annotated with " + annotatedWith;
    }
  }
}