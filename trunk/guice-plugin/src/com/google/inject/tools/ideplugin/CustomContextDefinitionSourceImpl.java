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

package com.google.inject.tools.ideplugin;

import com.google.inject.Inject;
import com.google.inject.tools.suite.JavaManager;
import com.google.inject.tools.suite.Messenger;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * {@inheritDoc CustomContextDefinitionSource}
 * 
 * @author Darren Creutz (dcreutz@gmail.com)
 */
class CustomContextDefinitionSourceImpl implements
    CustomContextDefinitionSource {
  private final Set<CustomContextDefinitionListener> listeners;
  private final Messenger messenger;

  @Inject
  public CustomContextDefinitionSourceImpl(Messenger messenger) {
    this.messenger = messenger;
    this.listeners = new HashSet<CustomContextDefinitionListener>();
  }

  public synchronized void addListener(CustomContextDefinitionListener listener) {
    listeners.add(listener);
  }

  public synchronized void removeListener(
      CustomContextDefinitionListener listener) {
    listeners.remove(listener);
  }

  protected void hadProblem(Throwable exception) {
    messenger.logException("Modules Listener error", exception);
  }

  protected void contextDefinitionChanged(JavaManager javaManager,
      String contextDefinitionName) {
    for (CustomContextDefinitionListener listener : listeners) {
      listener.contextDefinitionChanged(this, javaManager,
          contextDefinitionName);
    }
  }

  protected void contextDefinitionRemoved(JavaManager javaManager,
      String contextDefinitionName) {
    for (CustomContextDefinitionListener listener : listeners) {
      listener.contextDefinitionRemoved(this, javaManager,
          contextDefinitionName);
    }
  }

  protected void contextDefinitionAdded(JavaManager javaManager,
      String contextDefinitionName) {
    for (CustomContextDefinitionListener listener : listeners) {
      listener.contextDefinitionAdded(this, javaManager, contextDefinitionName);
    }
  }

  public Set<String> getContexts(JavaManager javaManager) {
    return Collections.<String> emptySet();
  }
  
  public void refresh(JavaManager javaManager) {
    
  }
}
