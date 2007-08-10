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

package com.google.inject.tools.ideplugin.code;

import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import com.google.inject.tools.ideplugin.JavaProject;
import com.google.inject.tools.ideplugin.snippets.CodeSnippetResult;

/**
 * Standard implementation of the {@link CodeRunner}.
 * 
 * @author Darren Creutz <dcreutz@gmail.com>
 */
public class CodeRunnerImpl implements CodeRunner {
  private final Set<CodeRunListener> listeners;
	private final Set<Runnable> runnables;
	private final Set<Runnable> runnablesLeft;
  private final JavaProject project;
  private final Map<Runnable,CodeRunThread> runThreads;
	
	public CodeRunnerImpl(JavaProject project) {
    this.project = project;
		listeners = new HashSet<CodeRunListener>();
		runnables = new HashSet<Runnable>();
		runnablesLeft = new HashSet<Runnable>();
    runThreads = new HashMap<Runnable,CodeRunThread>();
	}
	
  /**
   * (non-Javadoc)
   * @see com.google.inject.tools.ideplugin.code.CodeRunner#addListener(com.google.inject.tools.ideplugin.code.CodeRunner.CodeRunListener)
   */
	public void addListener(CodeRunListener listener) {
		listeners.add(listener);
	}
	
  /**
   * (non-Javadoc)
   * @see com.google.inject.tools.ideplugin.code.CodeRunner#queue(com.google.inject.tools.ideplugin.code.CodeRunner.Runnable)
   */
	public void queue(Runnable runnable) {
		runnables.add(runnable);
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
	
  /**
   * (non-Javadoc)
   * @see com.google.inject.tools.ideplugin.code.CodeRunner#notifyResult(com.google.inject.tools.ideplugin.code.CodeRunner.Runnable, com.google.inject.tools.ideplugin.snippets.CodeSnippetResult)
   */
	public void notifyResult(Runnable runnable,CodeSnippetResult result) {
		runnablesLeft.remove(runnable);
    runThreads.remove(runnable);
		for (CodeRunListener listener : listeners) {
			listener.acceptCodeRunResult(result);
		}
		if (runnablesLeft.isEmpty()) notifyDone();
	}
	
  /**
   * (non-Javadoc)
   * @see com.google.inject.tools.ideplugin.code.CodeRunner#run()
   */
	public void run() {
		for (Runnable runnable : runnables) {
			runnablesLeft.add(runnable);
			run(runnable);
		}
	}
  
  protected class CodeRunThread extends Thread {
    private final List<String> cmd;
    private final Runnable runnable;
    private Process process;
    private boolean killed;
    
    public CodeRunThread(Runnable runnable,List<String> cmd) {
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
	
	protected void run(Runnable runnable) {
		try {
			final String classpath = project.getSnippetsClasspath() + ":" + project.getProjectClasspath();
			final List<String> cmd = new ArrayList<String>();
			cmd.add(project.getJavaCommand());
			cmd.add("-classpath");
			cmd.add(classpath);
			cmd.add(runnable.getClassToRun());
			cmd.addAll(runnable.getArgsToRun());
			CodeRunThread runThread = new CodeRunThread(runnable,cmd);
      runThreads.put(runnable,runThread);
      runThread.run();
		} catch (Exception e) {
			runnable.caughtException(e);
		}
	}
  
  /**
   * (non-Javadoc)
   * @see com.google.inject.tools.ideplugin.code.CodeRunner#kill()
   */
  public void kill() {
    for (CodeRunThread runThread : runThreads.values()) {
      runThread.destroyProcess();
    }
  }
  
  /**
   * (non-Javadoc)
   * @see com.google.inject.tools.ideplugin.code.CodeRunner#kill(com.google.inject.tools.ideplugin.code.CodeRunner.Runnable)
   */
  public void kill(Runnable runnable) {
    runThreads.get(runnable).destroyProcess();
  }
  
  /**
   * (non-Javadoc)
   * @see com.google.inject.tools.ideplugin.code.CodeRunner#waitFor()
   */
  public void waitFor() throws InterruptedException {
    if (runThreads.keySet().size() == 0) return;
    else {
      waitFor(runThreads.keySet().iterator().next());
      waitFor();
    }
  }
  
  /**
   * (non-Javadoc)
   * @see com.google.inject.tools.ideplugin.code.CodeRunner#waitFor(com.google.inject.tools.ideplugin.code.CodeRunner.Runnable)
   */
  public void waitFor(Runnable runnable) throws InterruptedException {
    if (runThreads.get(runnable) == null) return;
    runThreads.get(runnable).join();
  }
}
