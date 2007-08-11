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

package com.google.inject.tools.ideplugin;

/**
 * The ActionsHandler responds to {@link Action} requests by taking the appropriate action,
 * such as going to a given code location.
 * 
 * IDE specific implementations implement the Actions defined in this interface.
 * 
 * @author Darren Creutz <dcreutz@gmail.com>
 */
public abstract class ActionsHandler {
  /**
   * An Action is anything the IDE can do in response to a trigger.
   * For example, going to a code location.
   * 
   * The ActionHandler/Action follows th visitor pattern.
   */
  public static interface Action {}
  
  /**
   * An action that does nothing.
   */
  public static class NullAction implements Action {
    @Override
    public String toString() {
      return "Null Action";
    }
    @Override
    public boolean equals(Object object) {
      return object instanceof NullAction;
    }
    @Override
    public int hashCode() {
      return 1;
    }
  }
  
  /**
   * Represents the IDE action of going to a location in the code, i.e. opening
   * the file and moving to the line number.
   */
  public static class GotoCodeLocation implements Action {
    private final String file;
    private final int location;
    
    /**
     * Create a GotoCodeLocation Action.
     * 
     * @param file the file to go to
     * @param location the line number
     */
    public GotoCodeLocation(String file,int location) {
      this.file = file;
      this.location = location;
    }
    
    /**
     * Return the file name.
     */
    public String file() {
      return file;
    }
    
    /**
     * Return the location (line number) to go to.
     */
    public int location() {
      return location;
    }
    
    @Override
    public boolean equals(Object object) {
      if (!(object instanceof GotoCodeLocation)) return false;
      return file.equals(((GotoCodeLocation)object).file()) && (location == ((GotoCodeLocation)object).location());
    }
    
    @Override
    public int hashCode() {
      return file.hashCode() + location;
    }
  }
  
  /**
   * An Action that opens a file to a declaration.
   */
  public static class GotoFile implements Action {
    private final String classname;
    public GotoFile(String classname) {
      this.classname = classname;
    }
    public String getClassname() {
      return classname;
    }
    @Override
    public boolean equals(Object object) {
      if (!(object instanceof GotoFile)) return false;
      return classname.equals(((GotoFile)object).getClassname());
    }
    @Override
    public int hashCode() {
      return classname.hashCode();
    }
  }
  
  /**
   * Perform a GotoCodeLocation Action.
   */
  public abstract void run(GotoCodeLocation action);
  
  /**
   * Perform a GotoFile Action.
   */
  public abstract void run(GotoFile action);
  
  /**
   * Perform a NullAction action.
   * Likely this does nothing.
   */
  public void run(NullAction action) {
    //do nothing
  }
  
  /**
   * Runtime exception thrown if run is called on an undefined action.
   */
  public static class InvalidActionException extends RuntimeException {
    private final Action action;
    public InvalidActionException(Action action) {
      this.action = action;
    }
    @Override
    public String toString() {
      return "Invalid action: " + action.toString();
    }
  }
  
  /**
   * Perform a nonspecific Action (does not satisfy any of the above types).
   * Likely this causes an exception.
   */
  public void run(Action action) {
    throw new InvalidActionException(action);
  }
}
