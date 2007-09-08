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

import com.google.inject.Binding;
import com.google.inject.Injector;
import com.google.inject.spi.BindingVisitor;
import com.google.inject.spi.ClassBinding;
import com.google.inject.spi.ConstantBinding;
import com.google.inject.spi.ConvertedConstantBinding;
import com.google.inject.spi.InstanceBinding;
import com.google.inject.spi.LinkedBinding;
import com.google.inject.spi.LinkedProviderBinding;
import com.google.inject.spi.ProviderBinding;
import com.google.inject.spi.ProviderInstanceBinding;
import com.google.inject.tools.suite.snippets.problems.BindingProblem;
import com.google.inject.tools.suite.snippets.problems.LocationProblem;
import com.google.inject.tools.suite.snippets.problems.ScopeProblem;

/**
 * Representation of a Guice binding.
 * 
 * @author Darren Creutz (dcreutz@gmail.com)
 */
public class BindingRepresentation extends Representation {
  private String file;
  private int location;
  private String locationDescription;
  private String scope;
  
  private String boundTo;
  private String boundProvider;
  private String boundInstance;
  private String boundConstant;
  
  private BindingRepresentation linkedTo;
  
  public BindingRepresentation(Binding<?> binding) {
    try {
      if (binding.getScope() != null) {
        scope = binding.getScope().toString();
      } else {
        scope = null;
      }
    } catch (Throwable throwable) {
      problems.add(new ScopeProblem(throwable));
    }
    try {
      if (binding.getSource() instanceof StackTraceElement) {
        file = ((StackTraceElement)binding.getSource()).getFileName();
        location = ((StackTraceElement)binding.getSource()).getLineNumber();
        locationDescription = null;
      } else {
        file = null;
        location = -1;
        locationDescription = binding.getSource().toString();
      }
    } catch (Throwable throwable) {
      problems.add(new LocationProblem(throwable));
    }
    try {
      visit(binding);
    } catch (Throwable throwable) {
      problems.add(new BindingProblem(throwable));
    }
  }
  
  <T> void visit(Binding<T> binding) {
    binding.accept(new RepresentationBuildingVisitor<T>());
  }
  
  class RepresentationBuildingVisitor<T> implements BindingVisitor<T> {
    public void visit(ClassBinding<? extends T> binding) {
      boundTo = binding.getBoundClass().getName();
      boundProvider = null;
      boundInstance = null;
      boundConstant = null;
      linkedTo = null;
    }

    public void visit(ConstantBinding<? extends T> binding) {
      boundTo = binding.getValue().getClass().getName();
      boundProvider = null;
      boundInstance = null;
      boundConstant = binding.getValue().toString();
      linkedTo = null;
    }

    public void visit(ConvertedConstantBinding<? extends T> binding) {
      linkedTo(binding.getOriginal());
    }

    public void visit(InstanceBinding<? extends T> binding) {
      boundTo = binding.getInstance().getClass().getName();
      boundProvider = null;
      if (binding.getInstance() instanceof Injector) {
        boundInstance = "The Injector";
      } else {
        boundInstance = binding.getInstance().toString();
      }
      boundConstant = null;
      linkedTo = null;
    }

    public void visit(LinkedBinding<? extends T> binding) {
      linkedTo(binding.getTarget());
    }

    public void visit(LinkedProviderBinding<? extends T> binding) {
      linkedTo(binding.getTargetProvider());
    }

    public void visit(ProviderBinding<?> binding) {
      linkedTo(binding.getTarget());
      boundProvider = boundTo;
      boundTo = null;
      linkedTo = null;
    }

    public void visit(ProviderInstanceBinding<? extends T> binding) {
      boundTo = null;
      boundProvider = binding.getProviderInstance().getClass().getName();
      boundConstant = null;
      boundInstance = binding.getProviderInstance().toString();
      linkedTo = null;
    }
    
    private void linkedTo(Binding<?> binding) {
      linkedTo = new BindingRepresentation(binding);
      boundTo = linkedTo.boundTo();
      boundProvider = linkedTo.boundProvider();
      boundInstance = linkedTo.boundInstance();
      boundConstant = linkedTo.boundConstant();
    }
  }
  
  public String file() {
    return file;
  }
  
  public int location() {
    return location;
  }
  
  public String locationDescription() {
    return locationDescription;
  }
  
  public String scope() {
    return scope;
  }
  
  public String boundTo() {
    return boundTo;
  }
  
  public String boundProvider() {
    return boundProvider;
  }
  
  public String boundInstance() {
    return boundInstance;
  }
  
  public String boundConstant() {
    return boundConstant;
  }
  
  @Override
  public int hashCode() {
    return file.hashCode() + location;
  }
  
  @Override
  public boolean equals(Object object) {
    if (!(object instanceof BindingRepresentation)) return false;
    BindingRepresentation bindingRepresentation = (BindingRepresentation)object;
    if (file == null && bindingRepresentation.file() != null) return false;
    if (file != null && !file.equals(bindingRepresentation.file())) return false;
    if (location != bindingRepresentation.location()) return false;
    if (locationDescription == null && bindingRepresentation.locationDescription() != null) return false;
    if (locationDescription != null && !locationDescription.equals(bindingRepresentation.locationDescription())) return false;
    if (scope == null && bindingRepresentation.scope() != null) return false;
    if (scope != null && !scope.equals(bindingRepresentation.scope())) return false;
    return true;
  }
  
  @Override
  public String toString() {
    StringBuilder result = new StringBuilder();
    if (boundTo != null) {
      result.append("bound to " + boundTo);
    }
    if (boundProvider != null) {
      result.append("bound to provider " + boundProvider);
    }
    if (boundInstance != null) {
      result.append(" instance " + boundInstance);
    }
    if (boundConstant != null) {
      result.append(" constant " + boundConstant);
    }
    
    if (file != null) {
      result.append(" at " + file + ":" + location);
    } else {
      result.append(" " + locationDescription);
    }
    if (scope != null) {
      result.append(" in scope " + scope);
    }
    if (linkedTo != null) {
      result.append(" linked by way of (" + linkedTo.toString() + ")");
    }
    return result.toString();
  }
}
