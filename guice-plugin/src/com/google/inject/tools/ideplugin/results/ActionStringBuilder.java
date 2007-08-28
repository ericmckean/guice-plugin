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

package com.google.inject.tools.ideplugin.results;

import com.google.inject.tools.ideplugin.ActionsHandler;
import com.google.inject.tools.suite.module.ClassNameUtility;
import com.google.inject.tools.suite.snippets.BindingCodeLocation;
import com.google.inject.tools.suite.snippets.CodeLocation;
import com.google.inject.tools.suite.snippets.CodeProblem;
import com.google.inject.tools.suite.snippets.BindingCodeLocation.NoBindingLocation;
import com.google.inject.tools.suite.snippets.CodeProblem.BindingProblem;
import com.google.inject.tools.suite.snippets.CodeProblem.InvalidModuleContextProblem;
import com.google.inject.tools.suite.snippets.CodeProblem.InvalidModuleProblem;

import java.util.ArrayList;
import java.util.List;

/**
 * Resolve {@link CodeLocation}s in {@link ActionString} form.
 * 
 * @author Darren Creutz (dcreutz@gmail.com)
 */
public class ActionStringBuilder {
  /**
   * An ActionStringElement is a piece of text with an Action associated to
   * it.
   */
  public static class ActionStringElement {
    private final String label;
    private final ActionsHandler.Action action;
    private final String tooltip;

    public ActionStringElement(String label, ActionsHandler.Action action,
        String tooltip) {
      this.label = label;
      this.action = action;
      this.tooltip = tooltip;
    }

    public ActionStringElement(String label) {
      this(label, new ActionsHandler.NullAction(), null);
    }

    public ActionStringElement(String label, String tooltip) {
      this(label, new ActionsHandler.NullAction(), tooltip);
    }

    public String label() {
      return label;
    }

    public ActionsHandler.Action action() {
      return action;
    }

    public String tooltip() {
      return tooltip;
    }

    @Override
    public boolean equals(Object object) {
      if (!(object instanceof ActionStringElement)) {
        return false;
      }
      ActionStringElement element = (ActionStringElement) object;
      return label.equals(element.label()) && action.equals(element.action());
    }

    @Override
    public int hashCode() {
      return label.hashCode();
    }

    @Override
    public String toString() {
      return label;
    }
  }

  /**
   * An ActionString is a list of {@link ActionStringElement}s.
   */
  public static class ActionString {
    private final List<ActionStringElement> elements;

    public ActionString() {
      elements = new ArrayList<ActionStringElement>();
    }

    public void addText(String text, String tooltip) {
      elements.add(new ActionStringElement(text, tooltip));
    }

    public void addTextWithAction(String text, ActionsHandler.Action action,
        String tooltip) {
      elements.add(new ActionStringElement(text, action, tooltip));
    }

    public List<ActionStringElement> elements() {
      return elements;
    }

    @Override
    public String toString() {
      final StringBuilder string = new StringBuilder();
      for (ActionStringElement element : elements) {
        string.append(element.label());
      }
      return string.toString();
    }

    @Override
    public boolean equals(Object object) {
      if (!(object instanceof ActionString)) {
        return false;
      }
      return elements.equals(((ActionString) object).elements());
    }

    @Override
    public int hashCode() {
      return elements.hashCode();
    }
  }
  
  /**
   * Create an ActionString for a {@link CodeLocation}.
   */
  public static ActionString getDisplayString(CodeLocation location) {
    if (location instanceof NoBindingLocation) {
      return new NoBindingLocationActionString(
          (NoBindingLocation)location);
    } else if (location instanceof BindingCodeLocation) {
      return new BindingCodeLocationActionString(
          (BindingCodeLocation)location);
    } else {
      throw new UnsupportedCodeLocationException(location);
    }
  }
  
  public static class UnsupportedCodeLocationException extends RuntimeException {
    private final CodeLocation location;
    public UnsupportedCodeLocationException(CodeLocation location) {
      this.location = location;
    }
    @Override
    public String toString() {
      return "Unsupported CodeLocation: " + location;
    }
  }
  
  public static class NoBindingLocationActionString extends ActionString {
    public NoBindingLocationActionString(NoBindingLocation location) {
      super();
      String theClass = location.getTheClass();
      addText("No binding for", "");
      addTextWithAction(ClassNameUtility.shorten(theClass), new ActionsHandler.GotoFile(
        theClass), "Goto source of " + theClass);
    }
  }
  
  public static class BindingCodeLocationActionString extends ActionString {
    public BindingCodeLocationActionString(BindingCodeLocation location) {
      super();
      String bindWhat = location.bindWhat();
      String annotatedWith = location.annotatedWith();
      String bindTo = location.bindTo();
      String file = location.file();
      int line = location.location();
      addTextWithAction(ClassNameUtility.shorten(bindWhat), new ActionsHandler.GotoFile(
          bindWhat), "Goto source of " + bindWhat);
      if (annotatedWith != null) {
        addText(" annotated with ", null);
        addText(ClassNameUtility.shorten(annotatedWith), annotatedWith);
      }
      addText(" is bound to ", null);
      addTextWithAction(ClassNameUtility.shorten(bindTo),
          new ActionsHandler.GotoFile(bindTo), "Goto source of " + bindTo);
      if (file != null) {
        addText(" at ", null);
        addTextWithAction(file + ":" + String.valueOf(line),
            new ActionsHandler.GotoCodeLocation(location.getStackTrace(), file,
                line), "Goto binding location of " + bindWhat + " as "
                + bindTo);
      }
      if (location.locationDescription() != null) {
        addText(" " + location.locationDescription(), null);
      }
    }
  }
  
  /**
   * Create an ActionString for a {@link CodeProblem}.
   */
  public static ActionString getDisplayString(CodeProblem problem) {
    if (problem instanceof BindingProblem) {
      return new BindingProblemActionString((BindingProblem)problem);
    } else if (problem instanceof InvalidModuleContextProblem) {
      return new InvalidModuleContextProblemActionString((InvalidModuleContextProblem)problem);
    } else if (problem instanceof InvalidModuleProblem) {
      return new InvalidModuleProblemActionString((InvalidModuleProblem)problem);
    } else {
      return new CodeProblemActionString(problem);
    }
  }
  
  public static class CodeProblemActionString extends ActionString {
    public CodeProblemActionString(CodeProblem problem) {
      super();
      addText("Guice Code Problem: ", null);
      addText(problem.getMessage(), null);
    }
  }
  
  public static class BindingProblemActionString extends CodeProblemActionString {
    public BindingProblemActionString(BindingProblem problem) {
      super(problem);
      String theClass = problem.getTheClass();
      String moduleContext = problem.getModuleContext();
      addText("Guice Code Problem: ", null);
      addTextWithAction(ClassNameUtility.shorten(theClass), new ActionsHandler.GotoFile(
          theClass), "Goto source of " + theClass);
      addText(" has a binding problem in ", null);
      addText("Module Context: ", null);
      addText(moduleContext, null);
    }
  }
  
  public static class InvalidModuleContextProblemActionString extends CodeProblemActionString {
    public InvalidModuleContextProblemActionString(InvalidModuleContextProblem problem) {
      super(problem);
      addText("Guice Module Context is invalid: " + problem.getModuleContext(), null);
    }
  }
  
  public static class InvalidModuleProblemActionString extends CodeProblemActionString {
    public InvalidModuleProblemActionString(InvalidModuleProblem problem) {
      super(problem);
      addText("Invalid Module: ", null);
      addText(problem.getModuleContext(), null);
    }
  }
}