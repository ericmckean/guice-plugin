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

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.Set;

import com.google.inject.Binding;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.Scope;
import com.google.inject.spi.BindingScopingVisitor;
import com.google.inject.spi.BindingTargetVisitor;
import com.google.inject.spi.InjectionPoint;
import com.google.inject.tools.suite.snippets.problems.BindingProblem;
import com.google.inject.tools.suite.snippets.problems.KeyProblem;
import com.google.inject.tools.suite.snippets.problems.LocationProblem;
import com.google.inject.tools.suite.snippets.problems.ScopeProblem;

/**
 * Representation of a Guice binding.
 * 
 * @author Darren Creutz (dcreutz@gmail.com)
 */
public class BindingRepresentation extends Representation {
  private static final long serialVersionUID = 3660213668665829591L;
  private String file;
  private int location;
  private StackTraceElement[] stackTrace;
  private String locationDescription;
  private String scope;

  private KeyRepresentation key;
  private String boundTo;
  private String boundProvider;
  private String boundInstance;
  private String boundConstant;

  private BindingRepresentation linkedTo;

  public BindingRepresentation(Binding<?> binding, Injector injector) {
    try {
      binding.acceptScopingVisitor(new BindingScopingVisitor<Void>() {
        public Void visitEagerSingleton() {
          scope = "Eager Singleton";
          return null;
        }
        public Void visitNoScoping() {
          scope = "No Scope";
          return null;
        }
        public Void visitScope(Scope scope) {
          BindingRepresentation.this.scope = scope.toString();
          return null;
        }
        public Void visitScopeAnnotation(Class<? extends Annotation> annotation) {
          scope = annotation.getName();
          return null;
        }
      });
    } catch (Throwable throwable) {
      problems.add(new ScopeProblem(throwable));
    }
    try {
      key = new KeyRepresentation(binding.getKey());
    } catch (Throwable throwable) {
      problems.add(new KeyProblem(throwable));
    }
    try {
      if (binding.getSource() instanceof StackTraceElement) {
        stackTrace = new StackTraceElement[1];
        stackTrace[0] = (StackTraceElement)binding.getSource();
        file = ((StackTraceElement)binding.getSource()).getFileName();
        location = ((StackTraceElement)binding.getSource()).getLineNumber();
        locationDescription = null;
      } else {
        stackTrace = null;
        file = null;
        location = -1;
        locationDescription = binding.getSource().toString();
      }
    } catch (Throwable throwable) {
      problems.add(new LocationProblem(throwable));
    }
    try {
      visit(binding, injector);
    } catch (Throwable throwable) {
      problems.add(new BindingProblem(throwable));
    }
  }

  <T> void visit(Binding<T> binding, Injector injector) {
    binding.acceptTargetVisitor(new RepresentationBuildingVisitor<T>());
  }

  class RepresentationBuildingVisitor<T> implements BindingTargetVisitor<T, Void> {
    public Void visitConstructor(Constructor<? extends T> arg0, Set<InjectionPoint> arg1) {
      return null;
    }

    public Void visitConvertedConstant(T arg0) {
      boundTo = arg0.getClass().getName();
      boundProvider = null;
      boundInstance = null;
      boundConstant = arg0.toString();
      linkedTo = null;
      return null;
    }

    public Void visitInstance(T arg0, Set<InjectionPoint> arg1) {
      boundTo = arg0.getClass().getName();
      boundProvider = null;
      boundInstance = arg0.toString();
      boundConstant = null;
      linkedTo = null;
      return null;
    }

    public Void visitKey(Key<? extends T> arg0) {
      boundTo = arg0.getTypeLiteral().getType().toString();
      boundProvider = null;
      boundInstance = null;
      boundConstant = null;
      linkedTo = null;
      return null;
    }

    public Void visitProvider(Provider<? extends T> arg0, Set<InjectionPoint> arg1) {
      boundTo = null;
      boundProvider = arg0.getClass().getName();
      boundConstant = null;
      boundInstance = arg0.toString();
      linkedTo = null;
      return null;
    }

    public Void visitProviderBinding(Key<?> arg0) {
      boundTo = null;
      boundProvider = arg0.getTypeLiteral().getType().toString();
      boundInstance = null;
      boundConstant = null;
      linkedTo = null;
      return null;
    }

    public Void visitProviderKey(Key<? extends Provider<? extends T>> arg0) {
      boundTo = null;
      boundProvider = arg0.getTypeLiteral().getType().toString();
      boundInstance = null;
      boundConstant = null;
      linkedTo = null;
      return null;
    }

    public Void visitUntargetted() {
      boundTo = null;
      boundProvider = null;
      boundInstance = null;
      boundConstant = null;
      linkedTo = null;
      return null;
    }
  }

  public KeyRepresentation key() {
    return key;
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

  public BindingRepresentation linkedTo() {
    return linkedTo;
  }

  public StackTraceElement[] stackTrace() {
    return stackTrace;
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
