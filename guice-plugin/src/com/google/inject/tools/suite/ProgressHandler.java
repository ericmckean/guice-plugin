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
 * The ProgressHandler is responsible for displaying a progress bar or other
 * indicator to the user during long operations. It should allow the user to
 * cancel the operation.
 * 
 * @author Darren Creutz (dcreutz@gmail.com)
 */
public interface ProgressHandler {
  /**
   * Represents a single step in a task managed by a progress bar.
   * 
   * @author Darren Creutz (dcreutz@gmail.com)
   */
  public interface ProgressStep {
    /**
     * Return the label to display during this step.
     */
    public String label();

    /**
     * Called by progress handler to run this step.
     * 
     * @param monitor the progress monitor to post status to, it does not need to be used
     * but if it is then it must follow the contract of {@link ProgressMonitor}
     */
    public void run(ProgressMonitor monitor);

    /**
     * Called by progress handler to cancel this step.
     */
    public void cancel();

    /**
     * Called by progress handler when this step is done.
     */
    public void complete();

    /**
     * Return true if this step is done.
     */
    public boolean isDone();
  }
  
  /**
   * Allows for progress updates from the steps.
   */
  public interface ProgressMonitor {
    /**
     * Tell the monitor that we are beginning work; done() must be called if this is.
     * 
     * @param label the label to display
     * @param units the total number of work units we will do
     */
    public void begin(String label, int units);
    
    /**
     * Create a submonitor using the given number of units of this monitor.
     */
    public ProgressMonitor getSubMonitor(int parentunits);
    
    /**
     * State that we have worked the given number of units.
     */
    public void worked(int workedunits);
    
    /**
     * State that the work is done.
     */
    public void done();
  }

  /**
   * Add a step to the progress. Do not run.
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
   * @param backgroundAutomatically true if the progress should be backgrounded
   *        initially
   */
  public void go(String label, boolean backgroundAutomatically);
  
  /**
   * Actually execute the steps.
   * 
   * @param label the display label for the handler initially
   * @param backgroundAutomatically true if the progress should be backgrounded
   *        initially
   * @param cancelThread true if a thread to check cancellation should be spawned
   */
  public void go(String label, boolean backgroundAutomatically, boolean cancelThread);
  
  /**
   * Tell the calling thread to wait until the progress handler tasks complete.
   */
  public void waitFor() throws InterruptedException;
  
  /**
   * Return true if the progress handler is finished.
   */
  public boolean isDone();
  
  /**
   * Set the code to be executed after the progress handler completes all the steps.
   */
  public void executeAfter(Runnable executeAfter);
}
