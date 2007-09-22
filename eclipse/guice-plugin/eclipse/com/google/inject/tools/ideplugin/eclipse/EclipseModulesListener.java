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

import com.google.inject.Module;
import com.google.inject.Singleton;
import com.google.inject.Inject;
import com.google.inject.tools.ideplugin.ModulesSource;
import com.google.inject.tools.suite.Messenger;

/**
 * Eclipse implementation of the {@link ModulesSource}.
 * 
 * {@inheritDoc ModulesSource}
 * 
 * @author Darren Creutz (dcreutz@gmail.com)
 */
@Singleton
class EclipseModulesListener extends EclipseSourceImpl implements ModulesSource {
  @Inject
  public EclipseModulesListener(Messenger messenger) {
    super(messenger);
  }
  
  @Override
  protected String getTypeName() {
    return Module.class.getName();
  }
  
  @Override
  protected boolean isTypeWeCareAbout(IType type) {
    return true;
  }
}
