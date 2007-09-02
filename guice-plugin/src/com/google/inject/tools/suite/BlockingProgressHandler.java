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

  public void go(String label, boolean backgroundAutomatically) {
    for (ProgressStep step : steps) {
      step.run();
      step.complete();
    }
  }

  public void waitFor() {
  }

  public boolean isCancelled() {
    return false;
  }

  public void step(ProgressStep step) {
    steps.add(step);
  }
}