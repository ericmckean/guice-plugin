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

import com.google.inject.binder.AnnotatedBindingBuilder;
import com.google.inject.tools.Messenger;
import com.google.inject.tools.ProgressHandler;
import com.google.inject.tools.ideplugin.GuicePluginModule;
import com.google.inject.tools.ideplugin.ActionsHandler;
import com.google.inject.tools.ideplugin.GuiceToolsModuleImpl;
import com.google.inject.tools.ideplugin.results.ResultsView;
import com.google.inject.tools.ideplugin.module.ModuleSelectionView;
import com.google.inject.tools.module.ModulesNotifier;

/** 
 * The module binding Eclipse implementations to interfaces.
 * 
 * @author Darren Creutz <dcreutz@gmail.com>
 */
public class EclipsePluginModule extends GuicePluginModule {
  public static class EclipseGuiceToolsModule extends GuiceToolsModuleImpl {
    /**
     * (non-Javadoc)
     * @see com.google.inject.tools.GuiceToolsModule#bindMessenger(com.google.inject.binder.AnnotatedBindingBuilder)
     */
    @Override
    protected void bindMessenger(AnnotatedBindingBuilder<Messenger> builder) {
      builder.to(EclipseMessenger.class).asEagerSingleton();
    }
    
    /**
     * (non-Javadoc)
     * @see com.google.inject.tools.GuiceToolsModule#bindModulesListener(com.google.inject.binder.AnnotatedBindingBuilder)
     */
    @Override
    protected void bindModulesListener(AnnotatedBindingBuilder<ModulesNotifier> builder) {
      builder.to(EclipseModulesListener.class).asEagerSingleton();
    }
  }
  
  /**
   * Create an Eclipse Plugin Module for injection.
   */
  public EclipsePluginModule() {
    super();
  }
  
  /**
   * (non-Javadoc)
   * @see com.google.inject.tools.ideplugin.GuicePluginModule#bindResultsView(com.google.inject.binder.AnnotatedBindingBuilder)
   */
  @Override
  protected void bindResultsView(AnnotatedBindingBuilder<ResultsView> builder) {
    builder.to(EclipseGuicePlugin.ResultsViewImpl.class).asEagerSingleton();
  }
  
  /**
   * (non-Javadoc)
   * @see com.google.inject.tools.ideplugin.GuicePluginModule#bindModuleSelectionView(com.google.inject.binder.AnnotatedBindingBuilder)
   */
  @Override
  protected void bindModuleSelectionView(AnnotatedBindingBuilder<ModuleSelectionView> builder) {
    builder.to(EclipseGuicePlugin.ModuleSelectionViewImpl.class).asEagerSingleton();
  }
  
  /**
   * Bind the {@link ActionsHandler} implementation.
   */
  @Override
  protected void bindActionsHandler(AnnotatedBindingBuilder<ActionsHandler> builder) {
    builder.to(EclipseActionsHandler.class).asEagerSingleton();
  }
  
  @Override
  protected void bindProgressHandler(AnnotatedBindingBuilder<ProgressHandler> builder) {
    builder.to(EclipseProgressHandler.class);
  }
}
