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

package com.google.inject.tools.ideplugin.snippets;

import java.io.Serializable;
import com.google.inject.CreationException;
import com.google.inject.tools.ideplugin.ActionsHandler;
import com.google.inject.tools.ideplugin.results.Results.Node.ActionString;

/**
 * Represents a problem found involving the user's guice code, such as a CreationException.
 * These are passed to the {@link com.google.inject.tools.ideplugin.problem.ProblemsHandler} for realtime notification to the user
 * as well as passed along with {@link CodeLocation}s for display as results.
 * 
 * @author Darren Creutz <dcreutz@gmail.com>
 */
public class CodeProblem implements Serializable {
  /**
   * Represents a problem in code that a class referenced is not useable.
   */
  public static class BadClassProblem extends CodeProblem {
    public BadClassProblem(String className,Throwable exception) {
      super(className,exception);
    }
  }
  
  /**
   * Represents a CreationException problem.
   */
  public static class CreationProblem extends CodeProblem {
    /**
     * Create a CreationException problem.
     * 
     * @param module the module context involved
     * @param exception the underlying exception
     */
    public CreationProblem(String module,CreationException exception) {
      super(module,exception);
    }
  }
  
  /**
   * Represents a problem with the binding of the class being injected.
   */
  public static class BindingProblem extends CodeProblem {
    protected final String theClass;
    
    /**
     * Create a BindingProblem.
     * 
     * @param module the module context
     * @param exception the underlyng exception
     * @param theClass the class being injected
     */
    public BindingProblem(String module,String theClass,Throwable exception) {
      super(module,exception);
      this.theClass = theClass;
    }
    
    /**
     * Return the class being injected when the problem occurred.
     */
    public String getTheClass() {
      return theClass;
    }
    
    /**
     * (non-Javadoc)
     * @see com.google.inject.tools.ideplugin.snippets.CodeProblem#toString()
     */
    @Override
    public String toString() {
      return "Guice Code Problem: " + theClass + " has a binding problem in Module " + moduleContext;
    }
    
    @Override
    public ActionString getDisplay() {
      ActionString text = new ActionString();
      text.addText("Guice Code Problem:");
      text.addTextWithAction(theClass,new ActionsHandler.GotoFile(theClass));
      text.addText("has a binding problem in Module");
      text.addText(moduleContext);
      return text;
    }
  }
  
  /**
   * Represents an OutOfScopeException during injection.
   */
  public static class OutOfScopeProblem extends BindingProblem {
    /**
     * Create an OutOfScopeProblem.
     * 
     * @param module the module context
     * @param theClass the class
     * @param exception the underlying exception
     */
    public OutOfScopeProblem(String module,String theClass,Exception exception) {
      super(module,theClass,exception);
    }
  }
  
  /**
   * Represents that no binding is defined for the class being injected.
   */
  public static class NoBindingProblem extends BindingProblem {
    /**
     * Create a NoBindingProblem.
     * 
     * @param module the module context
     * @param theClass the class being injected
     */
    public NoBindingProblem(String module,String theClass) {
      super(module,theClass,null);
    }
    
    /**
     * (non-Javadoc)
     * @see com.google.inject.tools.ideplugin.snippets.CodeProblem#toString()
     */
    @Override
    public String toString() {
      return "Guice Code Problem: " + theClass + " has no binding in Module " + moduleContext;
    }
    
    @Override
    public ActionString getDisplay() {
      ActionString text = new ActionString();
      text.addText("Guice Code Problem:");
      text.addTextWithAction(theClass,new ActionsHandler.GotoFile(theClass));
      text.addText("has no binding in Module");
      text.addText(moduleContext);
      return text;
    }
  }
  
  /**
   * A code problem that a module specified is invalid.
   */
  public static class InvalidModuleProblem extends CodeProblem {
    public InvalidModuleProblem(String moduleName) {
      super(moduleName,null);
    }
    
    @Override
    public String toString() {
      return "Invalid Module: " + moduleContext;
    }
    
    @Override
    public ActionString getDisplay() {
      ActionString text = new ActionString();
      text.addText("Invalid Module:");
      text.addTextWithAction(moduleContext,new ActionsHandler.GotoFile(moduleContext));
      return text;
    }
  }
  
  /**
   * Represents a problem that the module context is invalid.
   */
  public static class InvalidModuleContextProblem extends CodeProblem {
    /**
     * Create an InvalidModuleContextProblem.
     * 
     * @param module the invalid module
     */
    public InvalidModuleContextProblem(String module) {
      super(module,null);
    }
    
    /**
     * (non-Javadoc)
     * @see com.google.inject.tools.ideplugin.snippets.CodeProblem#toString()
     */
    @Override
    public String toString() {
      return "Guice Module Context is invalid: " + moduleContext;
    }
    
    @Override
    public ActionString getDisplay() {
      ActionString text = new ActionString();
      text.addText("Guice Module Context is invalid: " + moduleContext);
      return text;
    }
  }
  
  protected final String moduleContext;
  protected final String message;
  protected final StackTraceElement[] stacktrace;
  
  /**
   * Create a CodeProblem representation.
   * 
   * @param moduleContext the module context the problem occurred in
   * @param exception the underlying exception
   */
  public CodeProblem(String moduleContext,Throwable exception) {
    this.moduleContext = moduleContext;
    if (exception != null) {
      this.message = exception.getMessage();
      this.stacktrace = exception.getStackTrace();
    } else {
      this.message = null;
      this.stacktrace = null;
    }
  }
  
  /**
   * Return the problem as a String.
   */
  @Override
  public String toString() {
    return "Guice Code Problem: " + message;
  }
  
  /**
   * Return the module context the problem occurred in.
   */
  public String getModuleContext() {
    return moduleContext;
  }
  
  /**
   * Return the message from the problem.
   */
  public String getMessage() {
    return message; 
  }
  
  /**
   * Return the stack trace of the problem.
   */
  public StackTraceElement[] getStackTrace() {
    return stacktrace;
  }
  
  /**
   * Return text describing the problem in the form of {@link com.google.inject.tools.ideplugin.results.Results.Node.ActionString}s.
   */
  public ActionString getDisplay() {
    ActionString text = new ActionString();
    text.addText("Guice Code Problem:");
    text.addText(message);
    return text;
  }
}
