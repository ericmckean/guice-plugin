package com.google.inject.tools.ideplugin;

import com.google.inject.tools.JavaManager;

import java.util.Set;

public interface CustomContextDefinitionSource {
  public interface CustomContextDefinitionListener {
    public void contextDefinitionAdded(CustomContextDefinitionSource source,
        JavaManager javaManager, String contextDefinitionName);

    public void contextDefinitionRemoved(CustomContextDefinitionSource source,
        JavaManager javaManager, String contextDefinitionName);

    public void contextDefinitionChanged(CustomContextDefinitionSource source,
        JavaManager javaManager, String contextDefinitionName);
  }

  public void addListener(CustomContextDefinitionListener listener);

  public void removeListener(CustomContextDefinitionListener listener);

  public Set<String> getContexts(JavaManager javaManager);
}
