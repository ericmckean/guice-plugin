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

package com.google.inject.tools.ideplugin.eclipse;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import com.google.inject.tools.ideplugin.JavaElement;
import com.google.inject.tools.ideplugin.JavaProject;

/** 
 * Eclipse implementation of {@link JavaElement}.  Basically a wrapper around {@link
 * org.eclipse.jdt.core.IJavaElement}.
 * 
 * @author Darren Creutz <dcreutz@gmail.com>
 */
public class EclipseJavaElement implements JavaElement {
	private final IJavaElement element;
	private final Type type;
	private final String name;
	private final String signature;
	private final String className;
	private final EclipseJavaProject javaProject;
	
	/** 
	 * Create a JavaElement.
	 * 
	 * @param element the IJavaElement to wrap around
	 */
	public EclipseJavaElement(IJavaElement element) {
		this.element = element;
		this.type = findType();
		this.name = findName();
		this.signature = findSignature();
		this.className = getClassNameFromSignature(signature);
		this.javaProject = new EclipseJavaProject(element.getJavaProject());
	}
	
	private Type findType() {
		if (element instanceof IMethod) {
			return Type.PARAMETER;
		} else
		if (element instanceof IField) {
			return Type.FIELD;
		} else
		if (element instanceof ILocalVariable) {
			return Type.FIELD;
		} else {
			return null;
		}
	}
	
	private String findSignature() {
		try {
			switch (getType()) {
			case PARAMETER:
				return ((IMethod)element).getSignature();
			case FIELD:
        if (element instanceof IField) {
          return ((IField)element).getTypeSignature();
        }
        if (element instanceof ILocalVariable) {
          return ((ILocalVariable)element).getTypeSignature();
        }
			}
		} catch (JavaModelException e) {}
		return null;
	}
	
	private String findName() {
		return element.getElementName();
	}
	
	/** 
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return element.toString();
	}
	
	private String getClassNameFromSignature(String signature) {
		return signature!=null ? Signature.toString(signature) : null;
	}
	
	/**
	 * Return the {@link IJavaElement} underlying this element.
	 * 
	 * @return the IJavaElement
	 */
	public IJavaElement getIJavaElement() {
		return element;
	}
	
	/**
	 * (non-Javadoc)
	 * @see com.google.inject.tools.ideplugin.JavaElement#getName()
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * (non-Javadoc)
	 * @see com.google.inject.tools.ideplugin.JavaElement#getClassName()
	 */
	public String getClassName() {
		return className;
	}
	
	/**
	 * (non-Javadoc)
	 * @see com.google.inject.tools.ideplugin.JavaElement#getJavaProject()
	 */
	public JavaProject getJavaProject() {
		return javaProject;
	}
	
	/**
	 * (non-Javadoc)
	 * @see com.google.inject.tools.ideplugin.JavaElement#getType()
	 */
	public Type getType() {
		return type;
	}
	
	/**
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object object) {
		if (object instanceof JavaElement) {
			JavaElement element = (JavaElement)object;
			return (getClassName()!=null ? getClassName().equals(element.getClassName()) : element.getClassName()==null)
				&& (getName()!=null ? getName().equals(element.getName()) : element.getName()==null)
				&& getType().equals(element.getType());
		} else return false;
	}
  
  @Override
  public int hashCode() {
    return element.hashCode();
  }
}
