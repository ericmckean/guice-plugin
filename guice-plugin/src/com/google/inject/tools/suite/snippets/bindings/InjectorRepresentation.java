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

import java.util.HashMap;
import java.util.Map;

import com.google.inject.Binding;
import com.google.inject.CreationException;
import com.google.inject.Guice;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.OutOfScopeException;
import com.google.inject.Stage;
import com.google.inject.tools.suite.snippets.problems.CreationProblem;
import com.google.inject.tools.suite.snippets.problems.InjectorProblem;
import com.google.inject.tools.suite.snippets.problems.OutOfScopeProblem;

/**
 * Representation of a Guice Injector.
 * 
 * @author Darren Creutz (dcreutz@gmail.com)
 */
public class InjectorRepresentation extends Representation {
  private Map<KeyRepresentation, BindingRepresentation> bindings;
  
  public InjectorRepresentation(Iterable<Module> modules) {
    bindings = new HashMap<KeyRepresentation, BindingRepresentation>();
    Map<Key<?>, Binding<?>> guicebindings = null;
    try {
      guicebindings = Guice.createInjector(Stage.TOOL, modules).getBindings();
    } catch (CreationException creationException) {
      problems.add(new CreationProblem(creationException));
      return;
    } catch (OutOfScopeException outOfScopeException) {
      problems.add(new OutOfScopeProblem(outOfScopeException));
      return;
    } catch (Throwable throwable) {
      problems.add(new InjectorProblem(throwable));
      return;
    }
    for (Key<?> key : guicebindings.keySet()) {
      KeyRepresentation keyRepresentation = new KeyRepresentation(key);
      if (guicebindings.get(key) == null) {
        this.bindings.put(keyRepresentation, null);
      } else {
        this.bindings.put(keyRepresentation, new BindingRepresentation(guicebindings.get(key)));
      }
    }
  }
  
  public Map<KeyRepresentation, BindingRepresentation> bindings() {
    return bindings;
  }
}
