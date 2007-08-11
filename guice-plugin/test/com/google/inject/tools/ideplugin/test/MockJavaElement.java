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

package com.google.inject.tools.ideplugin.test;

import com.google.inject.tools.ideplugin.JavaElement;
import com.google.inject.tools.ideplugin.JavaProject;

/**
 * Mock the JavaElement object.
 * 
 * @author Darren Creutz <dcreutz@gmail.com>
 */
public class MockJavaElement implements JavaElement {
	private Type type;
	private String name;
	private String className;
	
	/**
	 * Create a mock Java element of the given type.
	 *
	 * @param type the type
	 */
	public MockJavaElement(Type type) {
		this.type = type;
		switch (type) {
		case PARAMETER:
			name = new String("TestMethod");
			className = new String("TestClass");
			break;
		case FIELD:
			name = new String("TestField");
			className = new String("TestClass");
			break;
		default:
			name = new String("TestField");
			className = new String("TestClass");
			break;
		}
	}
	
	/**
	 * (non-Javadoc)
	 * @see com.google.inject.tools.ideplugin.JavaElement#getClassName()
	 */
	public String getClassName() {
		return className;
	}
	
	public JavaProject getJavaProject() {
		return null;
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
			return className.equals(element.getClassName()) && name.equals(element.getName()) && type.equals(element.getType());
		} else return false;
	}
  
  @Override
  public int hashCode() {
    return 1;
  }
  
  /**
   * (non-Javadoc)
   * @see com.google.inject.tools.ideplugin.JavaElement#isInjectionPoint()
   */
  public boolean isInjectionPoint() {
    return false;
  }
}
