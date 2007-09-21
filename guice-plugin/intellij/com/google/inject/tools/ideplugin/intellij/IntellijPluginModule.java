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

package com.google.inject.tools.ideplugin.intellij;

import com.google.inject.binder.AnnotatedBindingBuilder;
import com.google.inject.tools.ideplugin.CustomContextDefinitionSource;
import com.google.inject.tools.ideplugin.GotoCodeLocationHandler;
import com.google.inject.tools.ideplugin.GotoFileHandler;
import com.google.inject.tools.ideplugin.GuicePluginModule;
import com.google.inject.tools.ideplugin.AbstractGuiceToolsModuleImpl;
import com.google.inject.tools.ideplugin.ModuleSelectionView;
import com.google.inject.tools.ideplugin.ModulesSource;
import com.google.inject.tools.ideplugin.ProjectSource;
import com.google.inject.tools.ideplugin.results.ResultsView;
import com.google.inject.tools.suite.Messenger;
import com.google.inject.tools.suite.ProgressHandler;

/**
 * The module binding Intellij implementations to interfaces.
 * 
 * {@inheritDoc GuicePluginModule}
 * 
 * @author Darren Creutz (dcreutz@gmail.com)
 */
class IntellijPluginModule extends GuicePluginModule {
  static class IntellijGuiceToolsModule extends AbstractGuiceToolsModuleImpl {
    @Override
    protected void bindMessenger(
        AnnotatedBindingBuilder<Messenger> bindMessenger) {
      bindMessenger.to(IntellijMessenger.class).asEagerSingleton();
    }
    
    @Override
    protected void bindProgressHandler(
        AnnotatedBindingBuilder<ProgressHandler> bindProgressHandler) {
      bindProgressHandler.to(IntellijProgressHandler.class);
    }
  }
  
  @Override
  protected void bindModulesSource(
      AnnotatedBindingBuilder<ModulesSource> bindModulesListener) {
    bindModulesListener.to(IntellijModulesListener.class).asEagerSingleton();
  }

  /**
   * Create an IntelliJ Plugin Module for injection.
   */
  public IntellijPluginModule() {
    super();
  }

  @Override
  protected void bindResultsView(
      AnnotatedBindingBuilder<ResultsView> bindResultsView) {
    bindResultsView.to(IntellijGuicePlugin.ResultsViewImpl.class)
        .asEagerSingleton();
  }

  @Override
  protected void bindModuleSelectionView(
      AnnotatedBindingBuilder<ModuleSelectionView> bindModuleSelectionView) {
    bindModuleSelectionView
        .to(IntellijGuicePlugin.ModuleSelectionViewImpl.class)
        .asEagerSingleton();
  }

  @Override
  protected void bindGotoCodeLocationHandler(
      AnnotatedBindingBuilder<GotoCodeLocationHandler> bindGotoCodeLocationHandler) {
    bindGotoCodeLocationHandler.to(IntellijGotoCodeLocationHandler.class)
        .asEagerSingleton();
  }

  @Override
  protected void bindGotoFileHandler(
      AnnotatedBindingBuilder<GotoFileHandler> bindGotoFileHandler) {
    bindGotoFileHandler.to(IntellijGotoFileHandler.class).asEagerSingleton();
  }

  @Override
  protected void bindCustomContextDefinitionSource(
      AnnotatedBindingBuilder<CustomContextDefinitionSource> bindCustomContextDefinitionSource) {
    bindCustomContextDefinitionSource.to(IntellijCustomContextListener.class)
        .asEagerSingleton();
  }
  
  @Override
  protected void bindProjectSource(
      AnnotatedBindingBuilder<ProjectSource> bindProjectSource) {
    bindProjectSource.to(IntellijProjectSource.class).asEagerSingleton();
  }
}
