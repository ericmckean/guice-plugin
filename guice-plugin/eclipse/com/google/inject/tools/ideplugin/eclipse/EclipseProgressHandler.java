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
import org.eclipse.core.runtime.SubProgressMonitor;
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
  private volatile IProgressMonitor monitor;
  private List<Integer> submonitorworkunits;
  private List<Integer> submonitorworkedsofar;
  private List<Integer> submonitorfakeunits;
  private List<Integer> submonitorworkedfakesofar;

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
    done = false;
    job = new ProgressHandlerJob(label);
    job.setUser(!backgroundAutomatically);
    job.schedule();
  }

  public void waitFor() throws InterruptedException {
    job.join();
  }
  
  public void setSubMonitors(int num) {
    submonitorworkunits = new ArrayList<Integer>(num);
    submonitorworkedsofar = new ArrayList<Integer>(num);
    submonitorfakeunits = new ArrayList<Integer>(num);
    submonitorworkedfakesofar = new ArrayList<Integer>(num);
    for (int i=0; i<num-1; i++) {
      submonitorworkunits.set(i, 100 / num);
    }
    submonitorworkunits.set(num-1, 100 - ((100 / num) * (num - 1)));
    for (int i=0; i<num; i++) {
      submonitorworkedsofar.set(i, 0);
      submonitorworkedfakesofar.set(i, 0);
    }
  }
  
  public int getExpectedWorkForSubmonitor(int num) {
    return submonitorworkunits.get(num);
  }
  
  public int workedSubmonitor(int num) {
    int oldfakeunits = submonitorworkedfakesofar.get(num);
    int newfakeunits = oldfakeunits + 1;
    int oldrealunits = submonitorworkedsofar.get(num);
    int newrealunits = (submonitorworkunits.get(num) * newfakeunits) / submonitorfakeunits.get(num);
    int addedrealunits = newrealunits = oldrealunits;
    submonitorworkedfakesofar.set(num, newfakeunits);
    submonitorworkedsofar.set(num, newrealunits);
    return addedrealunits;
  }
  
  public IProgressMonitor getSubMonitor(String name, int num, int fakeUnits) {
    submonitorfakeunits.set(num, fakeUnits);
    if (monitor!=null && !isDone()) {
      return new SubProgressMonitor(monitor, submonitorworkunits.get(num));
    } else {
      return null;
    }
  }

  private class ProgressHandlerJob extends Job {
    private final String label;
    private volatile ProgressStep currentStep;
    
    public ProgressHandlerJob(String label) {
      super(label);
      this.label = label;
    }

    @Override
    protected IStatus run(IProgressMonitor monitor) {
      monitor.beginTask(label, steps.size() * 100);
      spinOffCancelThread(monitor);
      for (ProgressStep step : steps) {
        if (!monitor.isCanceled()) {
          monitor.setTaskName(step.label());
          currentStep = step;
          step.run();
          currentStep = null;
          monitor.worked(100);
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
