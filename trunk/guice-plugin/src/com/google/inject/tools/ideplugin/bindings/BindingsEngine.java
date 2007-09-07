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

import java.util.Collections;

import com.google.inject.tools.ideplugin.JavaElement;
import com.google.inject.tools.ideplugin.JavaProject;
import com.google.inject.tools.ideplugin.ProjectManager;
import com.google.inject.tools.ideplugin.results.CodeLocationsResults;
import com.google.inject.tools.ideplugin.results.ResultsHandler;
import com.google.inject.tools.suite.Messenger;
import com.google.inject.tools.suite.ProblemsHandler;
import com.google.inject.tools.suite.ProgressHandler;
import com.google.inject.tools.suite.module.ClassNameUtility;
import com.google.inject.tools.suite.module.ModuleContextRepresentation;
import com.google.inject.tools.suite.module.ModuleManager;
import com.google.inject.tools.suite.snippets.CodeLocation;
import com.google.inject.tools.suite.snippets.CodeProblem;
import com.google.inject.tools.suite.snippets.BindingCodeLocation.ImplicitBindingLocation;
import com.google.inject.tools.suite.snippets.BindingCodeLocation.NoBindingLocation;

/**
 * The BindingsEngine is the glue between the other objects; it is responsible
 * for the top-level logic of the user asking the plugin to locate bindings of a
 * java expression.
 * 
 * @author Darren Creutz (dcreutz@gmail.com)
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
  public BindingsEngine(ProjectManager projectManager, JavaProject project,
      ProblemsHandler problemsHandler, ResultsHandler resultsHandler,
      ProgressHandler progressHandler, Messenger messenger, JavaElement element) {
    ProgressHandler.ProgressStep engineThread =
      new BindingsEngineThread(projectManager, project, problemsHandler,
          resultsHandler, messenger, element);
    progressHandler.step(engineThread);
    progressHandler.go("Finding Guice Bindings", true);
  }

  private class BindingsEngineThread implements ProgressHandler.ProgressStep {
    private final ProjectManager projectManager;
    private final JavaProject project;
    private final ProblemsHandler problemsHandler;
    private final ResultsHandler resultsHandler;
    private final Messenger messenger;
    private final JavaElement element;
    private volatile boolean done;

    public BindingsEngineThread(ProjectManager projectManager, JavaProject project,
        ProblemsHandler problemsHandler, ResultsHandler resultsHandler,
        Messenger messenger, JavaElement element) {
      this.projectManager = projectManager;
      this.project = project;
      this.problemsHandler = problemsHandler;
      this.resultsHandler = resultsHandler;
      this.messenger = messenger;
      this.element = element;
      done = false;
    }
    
    public void cancel() {
      done = true;
    }
    
    public void complete() {
      done = true;
    }
    
    public boolean isDone() {
      return done;
    }
    
    public String label() {
      return "Finding Guice Bindings for " + ClassNameUtility.shorten(element.getClassName());
    }

    public void run() {
      done = false;
      ModuleManager moduleManager = projectManager.getModuleManager(project);
      final String theClass = element.getClassName();
      final CodeLocationsResults results =
        new CodeLocationsResults("Bindings for "
            + ClassNameUtility.shorten(theClass) + "  (" + theClass + ")",
            theClass);
      if (!moduleManager.update(true, false)) {
        results.userCancelled();
      } else {
        if ((moduleManager.getActiveModuleContexts() != null)
            && (moduleManager.getActiveModuleContexts().size() > 0)) {
          for (ModuleContextRepresentation moduleContext : moduleManager
              .getActiveModuleContexts()) {
            BindingLocator locater;
            if (element.isInjectionPoint()) {
              locater = new BindingLocator(theClass, element.getAnnotations(),
                  moduleContext);
            } else {
              locater = new BindingLocator(theClass, moduleContext);
            }
            if ((locater.getCodeLocations().isEmpty() ||
                locater.getCodeLocations().iterator().next() instanceof NoBindingLocation)
                && element.isConcreteClass()) {
              results.put(moduleContext.getName(), 
                  Collections.singleton((CodeLocation)new ImplicitBindingLocation(theClass)), 
                  Collections.<CodeProblem>emptySet());
            } else {
              for (CodeLocation codeLocation : locater.getCodeLocations()) {
                problemsHandler.foundProblems(codeLocation.getProblems());
              }
              results.put(locater.getModuleContext().getName(),
                  locater.getCodeLocations(), locater.getProblems());
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
