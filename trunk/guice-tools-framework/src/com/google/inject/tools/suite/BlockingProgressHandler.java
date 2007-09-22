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

package com.google.inject.tools.suite;

import java.util.ArrayList;
import java.util.List;

/**
 * An implementation of {@link ProgressHandler} that does nothing to display
 * the progress and blocks the calling thread while it runs.
 * 
 * @author Darren Creutz (dcreutz@gmail.com)
 */
public class BlockingProgressHandler implements ProgressHandler {
  private final List<ProgressStep> steps = new ArrayList<ProgressStep>();
  private volatile boolean done;
  private Runnable executeAfter;
  
  static class BlockingProgressMonitor implements ProgressMonitor {
    public void begin(String label, int units) {
    }

    public void done() {
    }

    public ProgressMonitor getSubMonitor(int parentunits) {
      return this;
    }

    public void worked(int workedunits) {
    }
  }

  public void go(String label, boolean backgroundAutomatically) {
    done = false;
    for (ProgressStep step : steps) {
      step.run(new BlockingProgressMonitor());
      step.complete();
    }
    done = true;
    if (executeAfter != null) executeAfter.run();
  }
  
  public void go(String label, boolean backgroundAutomatically, boolean cancelThread) {
    go(label, backgroundAutomatically);
  }

  public void waitFor() {
  }

  public boolean isCancelled() {
    return false;
  }
  
  public boolean isDone() {
    return done;
  }

  public void step(ProgressStep step) {
    steps.add(step);
  }
  
  public void executeAfter(Runnable executeAfter) {
    this.executeAfter = executeAfter;
  }
}