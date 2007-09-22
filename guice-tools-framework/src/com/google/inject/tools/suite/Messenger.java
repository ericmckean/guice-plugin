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

/**
 * Responsible for displaying messages to the user in dialog box format.
 * 
 * @author Darren Creutz (dcreutz@gmail.com)
 */
public interface Messenger {
  /**
   * Display a message to the user in a dialog box.
   * 
   * Any thread may call this, the implementation should insure that the actual
   * execution happens in a UI thread.
   */
  public void display(String message);

  /**
   * Log a message to the guice error output view.
   */
  public void logMessage(String message);

  /**
   * Log an exception to the guice error output view.
   */
  public void logException(String label, Throwable throwable);
  
  /**
   * Log stdout and stderr output from the snippets.
   */
  public void logCodeRunnerMessage(String message);
  
  /**
   * Log an exception running the snippets.
   */
  public void logCodeRunnerException(String label, Throwable throwable);
}
