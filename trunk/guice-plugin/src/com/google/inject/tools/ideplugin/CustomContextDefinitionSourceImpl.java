package com.google.inject.tools.ideplugin;

import com.google.inject.Inject;
import com.google.inject.tools.JavaManager;
import com.google.inject.tools.Messenger;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class CustomContextDefinitionSourceImpl implements CustomContextDefinitionSource {
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

  public synchronized void removeListener(CustomContextDefinitionListener listener) {
    listeners.remove(listener);
  }
  
  protected void hadProblem(Throwable exception) {
    messenger.logException("Modules Listener error", exception);
  }
  
  protected void contextDefinitionChanged(JavaManager javaManager, String contextDefinitionName) {
    for (CustomContextDefinitionListener listener : listeners) {
      listener.contextDefinitionChanged(this, javaManager, contextDefinitionName);
    }
  }
  
  protected void contextDefinitionRemoved(JavaManager javaManager, String contextDefinitionName) {
    for (CustomContextDefinitionListener listener : listeners) {
      listener.contextDefinitionRemoved(this, javaManager, contextDefinitionName);
    }
  }
  
  protected void contextDefinitionAdded(JavaManager javaManager, String contextDefinitionName) {
    for (CustomContextDefinitionListener listener : listeners) {
      listener.contextDefinitionAdded(this, javaManager, contextDefinitionName);
    }
  }
  
  public Set<String> getContexts(JavaManager javaManager) {
    return Collections.<String>emptySet();
  }
}
