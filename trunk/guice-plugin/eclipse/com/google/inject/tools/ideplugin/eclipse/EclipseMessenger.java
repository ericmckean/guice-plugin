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

import com.google.inject.Singleton;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import com.google.inject.tools.suite.Messenger;

/**
 * Eclipse implementation of the Messenger object.
 * 
 * {@inheritDoc Messenger}
 * 
 * @author Darren Creutz (dcreutz@gmail.com)
 */
@Singleton
public class EclipseMessenger implements Messenger {
  private Shell shell;

  private class MessageDisplayer implements Runnable {
    private final String message;

    public MessageDisplayer(String message) {
      this.message = message;
    }

    public void run() {
      if (shell == null || shell.isDisposed()) {
        shell = new Shell();
      }
      MessageDialog.openInformation(shell, "Guice", message);
    }
  }

  private class ErrorLogDisplayer implements Runnable {
    private final String message;

    public ErrorLogDisplayer(String message) {
      this.message = message;
    }

    public void run() {
      try {
        IWorkbenchPage activePage = PlatformUI.getWorkbench()
            .getWorkbenchWindows()[0].getActivePage();
        IViewPart viewPart = activePage.showView(
            "com.google.inject.tools.ideplugin.eclipse.EclipseErrorView",
            null, IWorkbenchPage.VIEW_CREATE);
        ((EclipseErrorView) viewPart).displayError(message);
      } catch (java.lang.IllegalStateException e) {
        // means we are running in testing mode
      } catch (Exception e) {
        System.out.println("Problem displaying error messages..... "
            + e.toString());
      }
    }
  }

  public void display(String message) {
    try {
      Display.getDefault().syncExec(new MessageDisplayer(message));
    } catch (java.lang.UnsatisfiedLinkError error) {
      // means we are running in testing mode
    } catch (Throwable t) {
      System.out.println("Problem displaying dialog messages..... "
          + t.toString());
      System.out.println("  meant to diplay: " + message);
    }
  }

  private void log(String message) {
    try {
      Display.getDefault().syncExec(new ErrorLogDisplayer(message));
    } catch (java.lang.UnsatisfiedLinkError error) {
      // means we are running in testing mode
    } catch (Throwable t) {
      System.out.println("Problem displaying error messages..... "
          + t.toString());
      System.out.println("  meant to diplay: " + message);
    }
  }

  public void logMessage(String message) {
    log(message);
  }

  public void logException(String label, Throwable exception) {
    StringBuilder message = new StringBuilder();
    message.append(label + ": " + exception.toString() + "\n");
    if (exception.getStackTrace() != null) {
      for (StackTraceElement element : exception.getStackTrace()) {
        message.append("  " + element + "\n");
      }
    }
    log(message.toString());
  }
}
