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

package com.google.inject.tools.ideplugin.eclipse;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;

import com.google.inject.tools.ideplugin.JavaElement;
import com.google.inject.tools.suite.JavaManager;

/**
 * Eclipse implementation of {@link JavaElement}. Basically a wrapper around
 * {@link org.eclipse.jdt.core.IJavaElement}.
 * 
 * {@inheritDoc JavaElement}
 * 
 * @author Darren Creutz (dcreutz@gmail.com)
 */
class EclipseJavaElement implements JavaElement {
  private final IJavaElement element;
  private final Type type;
  private final String name;
  private final String className;
  private final EclipseJavaProject javaProject;
  private final ICompilationUnit compilationUnit;

  /**
   * Create a JavaElement.
   * 
   * @param element the IJavaElement to wrap around
   */
  public EclipseJavaElement(IJavaElement element,
      ICompilationUnit compilationUnit) {
    this.compilationUnit = compilationUnit;
    this.element = element;
    this.type = findType();
    this.name = findName();
    this.className = findClassName();
    this.javaProject = new EclipseJavaProject(element.getJavaProject());
  }

  private Type findType() {
    if (element instanceof IMethod) {
      return Type.PARAMETER;
    } else if (element instanceof IField) {
      return Type.FIELD;
    } else if (element instanceof ILocalVariable) {
      return Type.FIELD;
    } else {
      return null;
    }
  }

  private String findClassName() {
    String type = getType(element);
    if (type != null) {
      return type;
    } else {
      return getClassNameFromSignature(findSignature());
    }
  }

  private String getType(IJavaElement element) {
    try {
      IType type = compilationUnit.getAllTypes()[0];
      String resolvedSignature = 
        TypeUtil.resolveTypeSignature(type, findSignature(), false);
      String className = getClassNameFromResolvedSignature(resolvedSignature);
      return className;
    } catch (Exception e) {
      return null;
    }
  }
  
  private String getClassNameFromResolvedSignature(String resolvedSignature) {
    int start = resolvedSignature.indexOf('L');
    int end = resolvedSignature.indexOf(';');
    return resolvedSignature.substring(start+1, end);
  }

  private String findSignature() {
    try {
      switch (getType()) {
        case PARAMETER:
          return ((IMethod) element).getSignature();
        case FIELD:
          if (element instanceof IField) {
            return ((IField) element).getTypeSignature();
          } else if (element instanceof ILocalVariable) {
            return ((ILocalVariable) element).getTypeSignature();
          } else {
            return null;
          }
        default:
          return null;
      }
    } catch (JavaModelException e) {
      return null;
    }
  }

  private String findName() {
    return element.getElementName();
  }

  @Override
  public String toString() {
    return element.toString();
  }

  private String getClassNameFromSignature(String signature) {
    return signature != null ? Signature.toString(signature) : null;
  }

  public IJavaElement getIJavaElement() {
    return element;
  }

  public String getName() {
    return name;
  }

  public String getClassName() {
    return className;
  }

  public JavaManager getJavaProject() {
    return javaProject;
  }

  public Type getType() {
    return type;
  }

  @Override
  public boolean equals(Object object) {
    if (object instanceof JavaElement) {
      JavaElement element = (JavaElement) object;
      return (getClassName() != null ? getClassName().equals(
          element.getClassName()) : element.getClassName() == null)
          && (getName() != null ? getName().equals(element.getName()) : element
              .getName() == null) && getType().equals(element.getType());
    } else {
      return false;
    }
  }

  public boolean isInjectionPoint() {
    try {
      //TODO: determine if is injection point and related annotations
      return false;
    } catch (Exception e) {
      return false;
    }
  }
  
  public String getAnnotations() {
    //TODO: determine what annotations (if any) this element has
    return null;
  }

  @Override
  public int hashCode() {
    return element.hashCode();
  }
}
