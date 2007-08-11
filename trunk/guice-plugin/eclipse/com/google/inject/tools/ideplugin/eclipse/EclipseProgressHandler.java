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

import com.google.inject.tools.ideplugin.ProgressHandler;
import com.google.inject.tools.ideplugin.code.CodeRunner;

/**
 * Eclipse implementation of the {@link ProgressHandler}.
 * 
 * @author Darren Creutz <dcreutz@gmail.com>
 */
public class EclipseProgressHandler implements ProgressHandler {
  /**
   * (non-Javadoc)
   * @see com.google.inject.tools.ideplugin.ProgressHandler#initialize(int)
   */
  public void initialize(int numSteps) {
    //TODO: do this
  }
  
  /**
   * (non-Javadoc)
   * @see com.google.inject.tools.ideplugin.ProgressHandler#isCancelled()
   */
  public boolean isCancelled() {
    return false;
  }
  
  /**
   * (non-Javadoc)
   * @see com.google.inject.tools.ideplugin.ProgressHandler#step(java.lang.String, com.google.inject.tools.ideplugin.code.CodeRunner)
   */
  public void step(String label, CodeRunner codeRunner) {

  }
}
