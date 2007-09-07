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

package com.google.inject.tools.ideplugin.eclipse;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.google.inject.Inject;
import com.google.inject.tools.suite.Messenger;
import com.google.inject.tools.suite.ProgressHandler;

/**
 * Eclipse implementation of the {@link ProgressHandler}.
 * 
 * {@inheritDoc ProgressHandler}
 * 
 * @author Darren Creutz (dcreutz@gmail.com)
 */
class EclipseProgressHandler implements ProgressHandler {
  private final Messenger messenger;
  private final List<ProgressStep> steps;
  private ProgressHandlerJob job;
  private volatile boolean done;
  private volatile boolean gotSubmonitor;
  private volatile IProgressMonitor monitor;

  @Inject
  public EclipseProgressHandler(Messenger messenger) {
    this.messenger = messenger;
    this.steps = new ArrayList<ProgressStep>();
    done = false;
  }

  public boolean isDone() {
    return done;
  }
  
  public boolean isCancelled() {
    return false;
  }

  public void step(ProgressStep step) {
    steps.add(step);
  }
  
  public void go(String label, boolean backgroundAutomatically) {
    go(label, backgroundAutomatically, false);
  }

  public void go(String label, boolean backgroundAutomatically, boolean cancelThread) {
    done = false;
    job = new ProgressHandlerJob(label, cancelThread);
    job.setUser(!backgroundAutomatically);
    job.schedule();
  }

  public void waitFor() throws InterruptedException {
    job.join();
  }
  
  public IProgressMonitor getSubMonitor() {
    gotSubmonitor = true;
    return monitor;
  }

  private class ProgressHandlerJob extends Job {
    private final String label;
    private final boolean cancelThread;
    private volatile ProgressStep currentStep;
    
    public ProgressHandlerJob(String label, boolean cancelThread) {
      super(label);
      this.label = label;
      this.cancelThread = cancelThread;
    }

    @Override
    protected IStatus run(IProgressMonitor monitor) {
      EclipseProgressHandler.this.monitor = monitor;
      monitor.beginTask(label, steps.size() * 100);
      if (cancelThread) spinOffCancelThread(monitor);
      for (ProgressStep step : steps) {
        if (!monitor.isCanceled()) {
          monitor.setTaskName(step.label());
          currentStep = step;
          gotSubmonitor = false;
          step.run();
          currentStep = null;
          if (!gotSubmonitor) {
            monitor.worked(100);
          }
        }
        if (monitor.isCanceled()) {
          step.cancel();
        }
        step.complete();
      }
      monitor.done();
      EclipseProgressHandler.this.done = true;
      if (monitor.isCanceled()) {
        return Status.CANCEL_STATUS;
      } else {
        return Status.OK_STATUS;
      }
    }

    private void spinOffCancelThread(IProgressMonitor monitor) {
      new CancelListener().start();
    }
    
    private class CancelListener extends Thread {
      @Override
      public void run() {
        while (!monitor.isCanceled()) {
          try {
            //Thread.sleep should not be necessary
            //but we need to check for cancel presses in a timely manner
            //and our runnables launch user code so it may while(true)
            //
            //Eclipse should support sending interrupts to jobs but it doesn't
            //so we have to fake it ourselves by polling
            Thread.sleep(100);
          } catch (InterruptedException exception) {
            EclipseProgressHandler.this.messenger.logException(
                "Job interrupted", exception);
          }
        }
        if (monitor.isCanceled()) {
          if (currentStep != null) {
            currentStep.cancel();
          }
        }
      }
    }
  }
}
