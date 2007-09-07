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

import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeParameter;
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
  private Type type;
  private final String name;
  private String className;
  private final EclipseJavaProject javaProject;
  private final ICompilationUnit compilationUnit;
  private boolean isConcrete;

  /**
   * Create a JavaElement.
   * 
   * @param element the IJavaElement to wrap around
   */
  public EclipseJavaElement(IJavaElement element,
      ICompilationUnit compilationUnit) {
    this.compilationUnit = compilationUnit;
    this.element = element;
    this.name = findName();
    this.javaProject = new EclipseJavaProject(element.getJavaProject());
    findClassNameAndType(element);
  }
    
  private void findClassNameAndType(IJavaElement element) {
    try {
      if (element instanceof IMethod) {
        className = getClassName(element, ((IMethod)element).getSignature());
        type = Type.PARAMETER;
        isConcrete = findIsConcreteClass(((IMethod)element).getSignature());
      } else if (element instanceof IField) {
        className = getClassName(element, ((IField)element).getTypeSignature());
        type = Type.FIELD;
        isConcrete = findIsConcreteClass(((IField)element).getTypeSignature());
      } else if (element instanceof ILocalVariable) {
        className = getClassName(element, ((ILocalVariable)element).getTypeSignature());
        type = Type.FIELD;
        isConcrete = findIsConcreteClass(((ILocalVariable)element).getTypeSignature());
      } else if (element instanceof IType) {
        type = Type.PARAMETER;
        if (((IType)element).isResolved()) {
          className = ((IType)element).getFullyQualifiedName();
          isConcrete = findIsConcreteClass((IType)element);
        } else {
          IType resolvedType = TypeUtil.resolveType(element.getJavaProject(), 
              Signature.createTypeSignature(((IType)element).getFullyQualifiedName(), false));
          className = resolvedType.getFullyQualifiedName();
          isConcrete = findIsConcreteClass(resolvedType);
        }
      } else if (element instanceof ITypeParameter) {
        IMember member = ((ITypeParameter)element).getDeclaringMember();
        findClassNameAndType(member);
      } else {
        className = null;
        type = null;
        isConcrete = false;
      }
    } catch (Throwable e) {
      className = null;
      type = null;
      isConcrete = false;
    }
  }
  
  private String getClassName(IJavaElement element, String signature) {
    String type = getType(element, signature);
    if (type != null) {
      return type;
    } else {
      return getClassNameFromSignature(signature);
    }
  }

  private String getType(IJavaElement element, String signature) {
    try {
      IType type = compilationUnit.getAllTypes()[0];
      String resolvedSignature = 
        TypeUtil.resolveTypeSignature(type, signature, false);
      String className = getClassNameFromResolvedSignature(resolvedSignature);
      return className;
    } catch (Exception e) {
      return null;
    }
  }
  
  private String getClassNameFromResolvedSignature(String resolvedSignature) {
    int start = resolvedSignature.indexOf('<');
    int end = resolvedSignature.lastIndexOf('>');
    if (start == -1) {
      return getClassNameHelper(resolvedSignature);
    } else {
      String sign = resolvedSignature.substring(0, start+1) +
        getClassNameFromResolvedSignature(resolvedSignature.substring(start+2,end-1)) +
        resolvedSignature.substring(end,resolvedSignature.length());
      return getClassNameFromResolvedSignature(sign);
    }
  }
  
  private String getClassNameHelper(String sign) {
    int start = sign.indexOf('L');
    int end = sign.lastIndexOf(';');
    return sign.substring(start+1, end);
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
  
  public boolean isConcreteClass() {
    return isConcrete;
  }
  
  private boolean findIsConcreteClass(String signature) {
    try {
      IType owningType = compilationUnit.getAllTypes()[0];
      IType resolvedType = TypeUtil.resolveType(owningType, signature);
      return findIsConcreteClass(resolvedType);
    } catch (Throwable throwable) {
      return false;
    }
  }
  
  private boolean findIsConcreteClass(IType resolvedType) {
    try {
      boolean isInterface = Flags.isInterface(resolvedType.getFlags());
      boolean isAbstract = Flags.isAbstract(resolvedType.getFlags());
      return (!isInterface) && (!isAbstract);
    } catch (Throwable throwable) {
      return false;
    }
  }

  @Override
  public int hashCode() {
    return element.hashCode();
  }
}
