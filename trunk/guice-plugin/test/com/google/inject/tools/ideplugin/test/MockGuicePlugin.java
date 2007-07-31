package com.google.inject.tools.ideplugin.test;

import com.google.inject.tools.ideplugin.GuicePlugin;
import com.google.inject.tools.ideplugin.results.ResultsView;
import com.google.inject.tools.ideplugin.module.ModuleSelectionView;
import org.easymock.EasyMock;

public class MockGuicePlugin extends GuicePlugin {
  public MockGuicePlugin() {
    super(new MockGuicePluginModule());
  }
  
  @Override
  public ResultsView getResultsView() {
    return EasyMock.createMock(ResultsView.class);
  }
  
  @Override
  public ModuleSelectionView getModuleSelectionView() {
    return EasyMock.createMock(ModuleSelectionView.class);
  }
}
