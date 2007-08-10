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

package com.google.inject.tools.ideplugin;

import com.google.inject.tools.ideplugin.code.CodeRunner;

/**
 * The ProgressHandler is responsible for displaying a progress bar or other indicator to
 * the user during long operations.  It should allow the user to cancel the operation.
 * 
 * @author Darren Creutz <dcreutz@gmail.com>
 */
public interface ProgressHandler {
  /**
   * Begin displaying the progress meter for the given number of steps.
   * 
   * @param numSteps the number of steps this meter will have
   */
  public void initialize(int numSteps);
  
  /**
   * Start the next step of the ProgressHandler.
   * If the user cancels the operation, tell the CodeRunner to die.
   * 
   * @param label the label to display for this step
   * @param codeRunner the CodeRunner to kill on cancel
   */
  public void step(String label,CodeRunner codeRunner);
  
  /**
   * Return true if the user cancelled the operation.
   */
  public boolean isCancelled();
}
