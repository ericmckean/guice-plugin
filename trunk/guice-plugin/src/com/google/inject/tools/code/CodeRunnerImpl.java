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

package com.google.inject.tools.code;

import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import com.google.inject.Inject;
import com.google.inject.tools.snippets.CodeSnippetResult;
import com.google.inject.tools.suite.JavaManager;
import com.google.inject.tools.suite.Messenger;
import com.google.inject.tools.suite.ProgressHandler;

/**
 * {@inheritDoc CodeRunner}
 * 
 * @author Darren Creutz <dcreutz@gmail.com>
 */
class CodeRunnerImpl implements CodeRunner {
  private final ProgressHandler progressHandler;
  private final Messenger messenger;
  private final Set<CodeRunListener> listeners;
  private final JavaManager project;
  private final Map<Runnable, RunnableProgressStep> progressSteps;
  private boolean cancelled;

  public CodeRunnerImpl(JavaManager project) {
    this.progressHandler = new NullProgressHandler();
    this.messenger = new NullMessenger();
    this.project = project;
    listeners = new HashSet<CodeRunListener>();
    progressSteps = new HashMap<Runnable, RunnableProgressStep>();
    cancelled = false;
  }

  @Inject
  public CodeRunnerImpl(JavaManager project, ProgressHandler progressHandler,
      Messenger messenger) {
    if (messenger != null) {
      this.messenger = messenger;
    } else {
      this.messenger = new NullMessenger();
    }
    if (progressHandler != null) {
      this.progressHandler = progressHandler;
    } else {
      this.progressHandler = new NullProgressHandler();
    }
    this.project = project;
    listeners = new HashSet<CodeRunListener>();
    progressSteps = new HashMap<Runnable, RunnableProgressStep>();
    cancelled = false;
  }

  /**
   * An implementation of {@link ProgressHandler} that does nothing to display
   * the progress.
   */
  protected class NullProgressHandler implements ProgressHandler {
    private final List<ProgressStep> steps = new ArrayList<ProgressStep>();

    public void go(String label, boolean backgroundAutomatically) {
      for (ProgressStep step : steps) {
        step.run();
      }
    }

    public void waitForStart() {
    }

    public boolean isCancelled() {
      return false;
    }

    public void step(ProgressStep step) {
      steps.add(step);
    }
  }

  /**
   * An implementation of {@link Messenger} that does nothing.
   */
  protected class NullMessenger implements Messenger {
    public void display(String message) {
    }

    public void logException(String label, Throwable throwable) {
    }

    public void logMessage(String message) {
    }
  }

  public void addListener(CodeRunListener listener) {
    listeners.add(listener);
  }

  public void queue(Runnable runnable) {
    RunnableProgressStep step = new RunnableProgressStep(runnable);
    progressSteps.put(runnable, step);
    progressHandler.step(step);
  }

  private void notifyDone() {
    for (CodeRunListener listener : listeners) {
      listener.acceptDone();
    }
  }

  protected void notifyCancelled() {
    for (CodeRunListener listener : listeners) {
      listener.acceptUserCancelled();
    }
  }

  public synchronized void notifyResult(Runnable runnable,
      CodeSnippetResult result) {
    for (CodeRunListener listener : listeners) {
      listener.acceptCodeRunResult(result);
    }
    boolean done = true;
    for (RunnableProgressStep step : progressSteps.values()) {
      if (!step.isDone()) {
        done = false;
      }
    }
    if (done) {
      notifyDone();
    }
  }

  private class RunnableProgressStep implements ProgressHandler.ProgressStep {
    private final Runnable runnable;
    private final CodeRunThread runThread;
    private volatile boolean done;

    public RunnableProgressStep(Runnable runnable) {
      this.runnable = runnable;
      String classpath = "";
      List<String> cmd = new ArrayList<String>();
      try {
        classpath =
            project.getSnippetsClasspath() + project.getClasspathDelimiter()
                + project.getProjectClasspath();
        cmd.add(project.getJavaCommand());
        cmd.add("-classpath");
        cmd.add(classpath);
        cmd.add(runnable.getClassToRun());
        cmd.addAll(runnable.getArgsToRun());
      } catch (Exception e) {
        runnable.caughtException(e);
      }
      runThread = new CodeRunThread(runnable, cmd);
    }

    public String label() {
      return runnable.label();
    }

    public void run() {
      done = false;
      runThread.start();
    }

    public void kill() {
      runThread.destroyProcess();
    }

    public void cancel() {
      kill();
      CodeRunnerImpl.this.cancelled = true;
      done = true;
    }

    public void complete() {
      done = true;
    }

    public void waitFor() throws InterruptedException {
      runThread.join();
    }

    public boolean isDone() {
      return done;
    }
  }

  public void run(String label, boolean backgroundAutomatically) {
    cancelled = false;
    progressHandler.go(label, backgroundAutomatically);
  }

  public void run(String label) {
    run(label, true);
  }

  protected class CodeRunThread extends Thread {
    private final List<String> cmd;
    private final Runnable runnable;
    private Process process;
    private boolean killed;

    public CodeRunThread(Runnable runnable, List<String> cmd) {
      this.cmd = cmd;
      this.runnable = runnable;
      killed = false;
    }

    public void destroyProcess() {
      killed = true;
      process.destroy();
      process = null;
    }

    @Override
    public void run() {
      synchronized (this) {
        try {
          process = new ProcessBuilder(cmd).start();
          process.waitFor();
          if (!killed) {
            runnable.gotErrorOutput(process.getErrorStream());
            runnable.gotOutput(new ObjectInputStream(process.getInputStream()));
          }
        } catch (Exception exception) {
          runnable.caughtException(exception);
        }
      }
    }
  }

  public void notifyDone(Runnable runnable) {
    progressSteps.get(runnable).complete();
  }

  public void kill() {
    for (RunnableProgressStep step : progressSteps.values()) {
      step.kill();
    }
    progressSteps.clear();
  }

  public void kill(Runnable runnable) {
    progressSteps.get(runnable).kill();
  }

  public void waitFor() throws InterruptedException {
    progressHandler.waitForStart();
    for (RunnableProgressStep step : getProgressSteps()) {
      if (!step.isDone()) {
        step.waitFor();
      }
    }
  }

  private synchronized Set<RunnableProgressStep> getProgressSteps() {
    return new HashSet<RunnableProgressStep>(progressSteps.values());
  }

  public void waitFor(Runnable runnable) throws InterruptedException {
    RunnableProgressStep step = null;
    synchronized (this) {
      step = progressSteps.get(runnable);
    }
    if (step != null) {
      step.waitFor();
    }
  }

  public boolean isDone() {
    for (RunnableProgressStep step : progressSteps.values()) {
      if (!step.isDone()) {
        return false;
      }
    }
    return true;
  }

  public boolean isDone(Runnable runnable) {
    return progressSteps.get(runnable).isDone();
  }

  public boolean isCancelled() {
    return cancelled;
  }

  public Messenger getMessenger() {
    return messenger;
  }
}
