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

import junit.framework.TestCase;
import org.easymock.EasyMock;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;

import com.google.inject.tools.ideplugin.JavaElement;

/** 
 * Test the Eclipse implementation of JavaElement.
 * 
 * @author Darren Creutz <dcreutz@gmail.com>
 */
public class EclipseJavaElementTest extends TestCase {
  private EclipseJavaElement element;
  
  private void setUpVariable() {
    ILocalVariable variable = EasyMock.createMock(ILocalVariable.class);
    EasyMock.expect(variable.getTypeSignature()).andReturn("Lcom.google.inject.tools.ideplugin.sample.eclipse.TestVariableClass;").anyTimes();
    EasyMock.expect(variable.getElementName()).andReturn("TestVariable").anyTimes();
    EasyMock.expect(variable.getJavaProject()).andReturn((IJavaProject)null).anyTimes();
    EasyMock.replay(variable);
    IType type = EasyMock.createMock(IType.class);
    EasyMock.expect(type.getFullyQualifiedName()).andReturn("com.google.inject.tools.ideplugin.sample.eclipse.TestVariableClass");
    EasyMock.replay(type);
    ICompilationUnit cu = EasyMock.createMock(ICompilationUnit.class);
    EasyMock.expect(cu.getType((String)EasyMock.anyObject())).andReturn(type);
    EasyMock.replay(cu);
    element = new EclipseJavaElement(variable,cu);
  }
  
  /**
   * Test that the element properly finds its type, name and class name.
   */
  public void testElementFields() {
    setUpVariable();
    System.out.println(element.getClassName());
    assertTrue(element.getClassName().equals("com.google.inject.tools.ideplugin.sample.eclipse.TestVariableClass"));
    assertTrue(element.getName().equals("TestVariable"));
    assertTrue(element.getType().equals(JavaElement.Type.FIELD));
  }
}
