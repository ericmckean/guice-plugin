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

import com.google.inject.binder.AnnotatedBindingBuilder;
import com.google.inject.tools.ideplugin.CustomContextDefinitionSource;
import com.google.inject.tools.ideplugin.GotoCodeLocationHandler;
import com.google.inject.tools.ideplugin.GotoFileHandler;
import com.google.inject.tools.ideplugin.GuicePluginModule;
import com.google.inject.tools.ideplugin.GuiceToolsModuleImpl;
import com.google.inject.tools.ideplugin.results.ResultsView;
import com.google.inject.tools.ideplugin.module.ModuleSelectionView;
import com.google.inject.tools.suite.Messenger;
import com.google.inject.tools.suite.ProgressHandler;
import com.google.inject.tools.suite.module.ModulesSource;

/**
 * The module binding Eclipse implementations to interfaces.
 * 
 * {@inheritDoc GuicePluginModule}
 * 
 * @author Darren Creutz (dcreutz@gmail.com)
 */
class EclipsePluginModule extends GuicePluginModule {
  static class EclipseGuiceToolsModule extends GuiceToolsModuleImpl {
    @Override
    protected void bindMessenger(
        AnnotatedBindingBuilder<Messenger> bindMessenger) {
      bindMessenger.to(EclipseMessenger.class).asEagerSingleton();
    }

    @Override
    protected void bindModulesListener(
        AnnotatedBindingBuilder<ModulesSource> bindModulesListener) {
      bindModulesListener.to(EclipseModulesListener.class).asEagerSingleton();
    }
  }

  /**
   * Create an Eclipse Plugin Module for injection.
   */
  public EclipsePluginModule() {
    super();
  }

  @Override
  protected void bindResultsView(
      AnnotatedBindingBuilder<ResultsView> bindResultsView) {
    bindResultsView.to(EclipseGuicePlugin.ResultsViewImpl.class)
        .asEagerSingleton();
  }

  @Override
  protected void bindModuleSelectionView(
      AnnotatedBindingBuilder<ModuleSelectionView> bindModuleSelectionView) {
    bindModuleSelectionView
        .to(EclipseGuicePlugin.ModuleSelectionViewImpl.class)
        .asEagerSingleton();
  }

  @Override
  protected void bindGotoCodeLocationHandler(
      AnnotatedBindingBuilder<GotoCodeLocationHandler> bindGotoCodeLocationHandler) {
    bindGotoCodeLocationHandler.to(EclipseGotoCodeLocationHandler.class)
        .asEagerSingleton();
  }

  @Override
  protected void bindGotoFileHandler(
      AnnotatedBindingBuilder<GotoFileHandler> bindGotoFileHandler) {
    bindGotoFileHandler.to(EclipseGotoFileHandler.class).asEagerSingleton();
  }

  @Override
  protected void bindProgressHandler(
      AnnotatedBindingBuilder<ProgressHandler> bindProgressHandler) {
    bindProgressHandler.to(EclipseProgressHandler.class);
  }

  @Override
  protected void bindCustomContextDefinitionSource(
      AnnotatedBindingBuilder<CustomContextDefinitionSource> bindCustomContextDefinitionSource) {
    bindCustomContextDefinitionSource.to(EclipseContextDefinitionListener.class)
        .asEagerSingleton();
  }
}
