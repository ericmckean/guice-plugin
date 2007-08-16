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

package com.google.inject.tools;

/**
 * The ProgressHandler is responsible for displaying a progress bar or other indicator to
 * the user during long operations.  It should allow the user to cancel the operation.
 * 
 * @author Darren Creutz <dcreutz@gmail.com>
 */
public interface ProgressHandler {
  public interface ProgressStep {
    public String label();
    public void run();
    public void cancel();
    public void complete();
    public boolean isDone();
  }
  
  /**
   * Add a step to the progress.  Do not run.
   * 
   * @param step the {@link ProgressStep} to add
   */
  public void step(ProgressStep step);
  
  /**
   * Return true if the user cancelled the operation.
   */
  public boolean isCancelled();
  
  /**
   * Actually execute the steps.
   * 
   * @param label the display label for the handler initially
   * @param backgroundAutomatically true if the progress should be backgrounded initially
   */
  public void go(String label, boolean backgroundAutomatically);
}
