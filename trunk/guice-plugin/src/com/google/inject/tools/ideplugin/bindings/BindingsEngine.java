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

package com.google.inject.tools.ideplugin.bindings;

import com.google.inject.tools.ideplugin.JavaElement;
import com.google.inject.tools.ideplugin.results.CodeLocationsResults;
import com.google.inject.tools.ideplugin.results.ResultsHandler;
import com.google.inject.tools.module.ClassNameUtility;
import com.google.inject.tools.module.ModuleContextRepresentation;
import com.google.inject.tools.module.ModuleManager;
import com.google.inject.tools.snippets.CodeLocation;
import com.google.inject.tools.suite.Messenger;
import com.google.inject.tools.suite.ProblemsHandler;

/**
 * The BindingsEngine is the glue between the other objects; it is responsible
 * for the top-level logic of the user asking the plugin to locate bindings of a
 * java expression.
 * 
 * @author Darren Creutz <dcreutz@gmail.com>
 */
public final class BindingsEngine {
  /**
   * Create a BindingsEngineImpl. This should be created by the
   * {@link com.google.inject.tools.ideplugin.GuicePlugin}.
   * 
   * @param resultsHandler the ResultsHandler to send results to (injected)
   * @param problemsHandler the ProblemsHandler to notify with problems
   *        (injected)
   * @param moduleManager the ModuleManager to ask for what context to run in
   *        (injected)
   * @param messenger the Messenger to display notifications with (injected)
   * @param element the JavaElement to find bindings for (not injected)
   */
  // @AssistedInject replaced by factory in GuicePlugin
  public BindingsEngine(ModuleManager moduleManager,
      ProblemsHandler problemsHandler, ResultsHandler resultsHandler,
      Messenger messenger, JavaElement element) {
    Thread engineThread =
      new BindingsEngineThread(moduleManager, problemsHandler,
          resultsHandler, messenger, element);
    engineThread.start();
  }

  private class BindingsEngineThread extends Thread {
    private final ModuleManager moduleManager;
    private final ProblemsHandler problemsHandler;
    private final ResultsHandler resultsHandler;
    private final Messenger messenger;
    private final JavaElement element;

    public BindingsEngineThread(ModuleManager moduleManager,
        ProblemsHandler problemsHandler, ResultsHandler resultsHandler,
        Messenger messenger, JavaElement element) {
      this.moduleManager = moduleManager;
      this.problemsHandler = problemsHandler;
      this.resultsHandler = resultsHandler;
      this.messenger = messenger;
      this.element = element;
    }

    @Override
    public void run() {
      final String theClass = element.getClassName();
      final CodeLocationsResults results =
        new CodeLocationsResults("Bindings for "
            + ClassNameUtility.shorten(theClass), theClass);
      if (!moduleManager.updateModules(true, false)) {
        results.userCancelled();
      } else {
        // TODO: if element.isInjectionPoint() ...
        if ((moduleManager.getActiveModuleContexts() != null)
            && (moduleManager.getActiveModuleContexts().size() > 0)) {
          for (ModuleContextRepresentation moduleContext : moduleManager
              .getActiveModuleContexts()) {
            BindingLocator locater =
              new BindingLocator(theClass, moduleContext);
            if (locater.getCodeLocations() != null) {
              for (CodeLocation codeLocation : locater.getCodeLocations()) {
                problemsHandler.foundProblems(codeLocation.getProblems());
              }
              results.put(locater.getModuleContext().getName(),
                  locater.getCodeLocations());
            }
          }
          if (!results.keySet().isEmpty()) {
            resultsHandler.displayLocationsResults(results);
          }
        } else {
          messenger.display("No active module contexts configured.");
        }
      }
    }
  }
}
