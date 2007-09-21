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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;
import com.google.inject.tools.suite.Messenger;
import com.google.inject.tools.suite.snippets.CodeSnippet;
import com.google.inject.tools.suite.snippets.CodeSnippetResult;

/**
 * The CodeRunner runs {@link CodeSnippet} objects in a separate virtual machine
 * that also has access to the user's class files.
 * 
 * Every {@link Runnable} will be run its in own thread.
 * 
 * @author Darren Creutz (dcreutz@gmail.com)
 */
public interface CodeRunner {
  /**
   * Interface that listeners for the CodeRunner must implement.
   */
  public interface CodeRunListener {
    public void acceptCodeRunResult(CodeSnippetResult result);

    public void acceptUserCancelled();

    public void acceptDone();
  }

  /**
   * An exception thrown if the CodeRunner is asked to run a non
   * {@link CodeSnippet} object.
   */
  public static class NotCodeSnippetException extends RuntimeException {
    /**
     * 
     */
    private static final long serialVersionUID = 2225616111462610645L;
    private final Exception rootException;

    public NotCodeSnippetException(Exception exception) {
      rootException = exception;
    }

    @Override
    public String toString() {
      return "Not a code snippet: " + rootException;
    }
  }

  /**
   * An exception thrown if the CodeRunner received notification of a result
   * that is not the result of a {@link CodeSnippet}.
   */
  public static class NotCodeSnippetResultException extends RuntimeException {
    /**
     * 
     */
    private static final long serialVersionUID = 1913387199873163864L;
    private final Runnable runnable;
    private final Object result;

    public NotCodeSnippetResultException(Runnable runnable, Object result) {
      this.runnable = runnable;
      this.result = result;
    }

    public Runnable getRunnable() {
      return runnable;
    }

    public Object getResult() {
      return result;
    }
  }

  /**
   * A Runnable is something the CodeRunner can run.
   * 
   * It must implement the static main(String[] args) method and create a result
   * that extends {@link CodeSnippetResult} which should be written to
   * System.out as an object.
   */
  public static abstract class Runnable {
    private final CodeRunner codeRunner;

    public Runnable(CodeRunner codeRunner) throws NotCodeSnippetException {
      this.codeRunner = codeRunner;
      try {
        Class<?> snippetClass = Class.forName(getFullyQualifiedSnippetClass());
        snippetClass.asSubclass(CodeSnippet.class);
      } catch (Exception exception) {
        throw new NotCodeSnippetException(exception);
      }
    }

    public abstract String label();

    public void kill() {
      codeRunner.kill(this);
    }

    public boolean isDone() {
      return codeRunner.isDone(this);
    }

    /**
     * Return the class name of the {@link CodeSnippet} to run. This class must
     * be in the snippets.jar file. NOTE: This is called by the constructor so
     * must not access any fields.
     */
    protected abstract String getFullyQualifiedSnippetClass();

    /**
     * Return the arguments to pass to the code snippet we are running. NOTE:
     * This is called by the constructor so must not access any fields.
     */
    protected abstract List<? extends Object> getSnippetArguments();

    /**
     * Return the (fully qualified) name of the {@link CodeSnippet} class to
     * run.
     */
    public String getClassToRun() {
      return getFullyQualifiedSnippetClass();
    }

    /**
     * Return the arguments to pass to the snippet when running it.
     */
    public List<String> getArgsToRun() {
      final List<String> args = new ArrayList<String>();
      for (Object arg : getSnippetArguments()) {
        args.add(arg.toString());
      }
      return args;
    }

    /**
     * Notify the Runnable that an exception occurred trying to run it.
     * 
     * @param exception the exception that occurred during runtime
     */
    public void caughtException(Throwable exception) {
      codeRunner.getMessenger().logException("Runnable exception", exception);
    }

    /**
     * Pass the error stream from the run back to the Runnable.
     * 
     * @param stream the data stream corresponding to stderr
     */
    public void gotErrorOutput(InputStream stream) {
      InputStreamReader ir = new InputStreamReader(stream);
      BufferedReader r = new BufferedReader(ir);
      String line;
      try {
        while (r.ready()) {
          line = r.readLine();
          codeRunner.getMessenger().logCodeRunnerMessage(line);
        }
      } catch (Exception e) {
        codeRunner.getMessenger().logCodeRunnerException(
            "Exception getting error output", e);
      }
    }

    /**
     * Pass the output from the run back to the Runnable in object form.
     * 
     * @param output the result from the run
     */
    public void gotOutput(Object output) {
      if (output instanceof CodeSnippetResult) {
        codeRunner.notifyDone(this);
        codeRunner.notifyResult(this, (CodeSnippetResult) output);
      } else {
        throw new NotCodeSnippetResultException(this, output);
      }
    }
  }

  public void notifyDone(Runnable runnable);

  /**
   * Add a listener to the CodeRunner.
   */
  public void addListener(CodeRunListener listener);

  /**
   * Queue a Runnable to be run by the runner.
   */
  public void queue(Runnable runnable);

  /**
   * Run the queued Runnables.
   * 
   * @param label the display label for this code run
   * @param backgroundAutomatically true if the code run should be backgrounded
   *        initially
   */
  public void run(String label, boolean backgroundAutomatically);

  /**
   * Run the queued Runnables.
   * 
   * @param label the display label for this code run
   */
  public void run(String label);

  /**
   * Notify the runner that a result from a run is ready.
   * 
   * @param runnable the runnable that gave the result
   * @param result the result from the code snippet
   */
  public void notifyResult(Runnable runnable, CodeSnippetResult result);

  /**
   * Tell the runner to kill all its currently running snippets.
   */
  public void kill();

  /**
   * Tell the runner to kill a running snippet.
   * 
   * @param runnable the runnable to kill
   */
  public void kill(Runnable runnable);

  /**
   * Wait for the CodeRunner to finish running all Runnables.
   */
  public void waitFor() throws InterruptedException;

  /**
   * Return true if all the runnables have completed.
   */
  public boolean isDone();

  /**
   * Return true if the given runnable has completed.
   */
  public boolean isDone(Runnable runnable);

  /**
   * Return true if the code run was cancelled.
   */
  public boolean isCancelled();

  /**
   * Return the messenger to the runnable.
   */
  public Messenger getMessenger();
}
