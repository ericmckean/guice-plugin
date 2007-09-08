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

package com.google.inject.tools.suite;

import junit.framework.TestCase;

import com.google.inject.Binder;
import com.google.inject.Binding;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.Stage;

/**
 * Test that BindingRepresentations can be built (really a Guice test).
 * 
 * @author Darren Creutz (dcreutz@gmail.com)
 */
public class BindingRepresentationTest extends TestCase {
  public void testInjectorRepresentation() {
    Module module = new Module() {
      public void configure(Binder binder) {}
    };
    Injector injector = Guice.createInjector(Stage.TOOL, module);
    Binding<Injector> binding = injector.getBinding(Key.get(Injector.class));
    String string = binding.toString();
  }
}
