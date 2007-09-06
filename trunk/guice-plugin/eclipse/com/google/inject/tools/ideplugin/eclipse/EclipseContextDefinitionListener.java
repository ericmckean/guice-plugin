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

import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

import com.google.inject.Module;
import com.google.inject.Singleton;
import com.google.inject.Inject;
import com.google.inject.tools.ideplugin.CustomContextDefinitionSource;
import com.google.inject.tools.suite.Messenger;

/**
 * Eclipse implementation of the {@link CustomContextDefinitionSource}.
 * 
 * {@inheritDoc CustomContextDefinitionSource}
 * 
 * @author Darren Creutz (dcreutz@gmail.com)
 */
@Singleton
class EclipseContextDefinitionListener extends EclipseSourceImpl
    implements CustomContextDefinitionSource {
  @Inject
  public EclipseContextDefinitionListener(Messenger messenger) {
    super(messenger);
  }
  
  @Override
  protected String getTypeName() {
    return Iterable.class.getName();
  }
  
  private static final String iterableLongModuleLong = 
    Iterable.class.getName() + "<" + Module.class.getName() + ">";
  private static final String iterableLongModule = 
    Iterable.class.getName() + "<" + Module.class.getSimpleName() + ">";
  private static final String iterableModuleLong = 
    Iterable.class.getSimpleName() + "<" + Module.class.getName() + ">";
  private static final String iterableModule = 
    Iterable.class.getSimpleName() + "<" + Module.class.getSimpleName() + ">";
  
  @Override
  protected boolean isTypeWeCareAbout(IType type) throws JavaModelException {
    for (String s : type.getSuperInterfaceNames()) {
      if (s.equals(iterableLongModuleLong)
          || s.equals(iterableLongModule)
          || s.equals(iterableModuleLong)
          || s.equals(iterableModule)) {
        return true;
      }
    }
    return false;
  }
}
