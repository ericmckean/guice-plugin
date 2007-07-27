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

import com.google.inject.tools.ideplugin.JavaElement;

/** 
 * Eclipse implementation of {@link JavaElement}.  Basically a wrapper around {@link
 * org.eclipse.jdt.core.IJavaElement}.
 * 
 * @author Darren Creutz <dcreutz@gmail.com>
 */
public class EclipseJavaElement implements JavaElement {
	private final IJavaElement element;
	private final TYPE type;
	private final String name;
	private final Class<?> theClass;
	private final String className;
	
	/** 
	 * Create a JavaElement.
	 * 
	 * @param element the IJavaElement to wrap around
	 */
	public EclipseJavaElement(IJavaElement element) {
		this.element = element;
		this.type = findType();
		this.name = findName();
		this.className = findClassName();
		this.theClass = findClass();
	}
	
	private TYPE findType() {
		if (element instanceof IMethod) {
			return TYPE.METHOD;
		} else
		if (element instanceof IField) {
			return TYPE.FIELD;
		} else
		if (element instanceof ILocalVariable) {
			return TYPE.VARIABLE;
		} else {
			return TYPE.UNSUPPORTED;
		}
	}
	
	private String findClassName() {
		switch (getType()) {
		case METHOD:
			//TODO: finish this
			return null;
		case FIELD:
			//TODO: finish this
			return null;
		case VARIABLE:
			return getClassNameFromSignature(((ILocalVariable)element).getTypeSignature());
		default:
			return null;
		}
	}
	
	private String findName() {
		return element.getElementName();
	}
	
	private Class<?> findClass() {
		try {
			return Class.forName(className);
		} catch (ClassNotFoundException exception) {
			//TODO: what happened?
			return null;
		}
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
		int i = signature.indexOf('L');
		int j = signature.indexOf(";");
		return signature.substring(i+1,j);
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
	 * @see com.google.inject.tools.ideplugin.JavaElement#getTheClass()
	 */
	public Class<?> getTheClass() {
		return theClass;
	}
	
	/**
	 * (non-Javadoc)
	 * @see com.google.inject.tools.ideplugin.JavaElement#getType()
	 */
	public TYPE getType() {
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
			return getClassName().equals(element.getClass()) && getName().equals(element.getName()) && getType().equals(element.getType());
		} else return false;
	}
}
