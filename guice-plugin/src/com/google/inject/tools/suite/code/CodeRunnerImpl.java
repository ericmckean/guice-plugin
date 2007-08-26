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

package com.google.inject.tools.suite.code;

import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;

import com.google.inject.Inject;
import com.google.inject.tools.suite.BlockingProgressHandler;
import com.google.inject.tools.suite.JavaManager;
import com.google.inject.tools.suite.Messenger;
import com.google.inject.tools.suite.ProgressHandler;
import com.google.inject.tools.suite.snippets.CodeSnippetResult;

/**
 * {@inheritDoc CodeRunner}
 * 
 * @author Darren Creutz (dcreutz@gmail.com)
 */
class CodeRunnerImpl implements CodeRunner {
  private final ProgressHandler progressHandler;
  private final Messenger messenger;
  private final Set<CodeRunListener> listeners;
  private final JavaManager project;
  private final Map<Runnable, RunnableProgressStep> progressSteps;
  private boolean cancelled;

  public CodeRunnerImpl(JavaManager project) {
    this(project, new BlockingProgressHandler(), new NullMessenger());
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
      this.progressHandler = new BlockingProgressHandler();
    }
    this.project = project;
    listeners = new HashSet<CodeRunListener>();
    progressSteps = new HashMap<Runnable, RunnableProgressStep>();
    cancelled = false;
  }

  /**
   * An implementation of {@link Messenger} that does nothing.
   */
  protected static class NullMessenger implements Messenger {
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
    private Process process;
    private final List<String> cmd;
    private volatile boolean done;
    private volatile boolean killed;

    public RunnableProgressStep(Runnable runnable) {
      this.runnable = runnable;
      killed = false;
      String classpath = "";
      cmd = new ArrayList<String>();
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
    }

    public void kill() {
      killed = true;
      if (process != null) {
        process.destroy();
        process = null;
      }
    }

    public String label() {
      return runnable.label();
    }

    public void run() {
      if (!killed && !done) {
        done = false;
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

    public void cancel() {
      kill();
      CodeRunnerImpl.this.cancelled = true;
      done = true;
    }

    public void complete() {
      done = true;
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
    progressHandler.waitFor();
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
