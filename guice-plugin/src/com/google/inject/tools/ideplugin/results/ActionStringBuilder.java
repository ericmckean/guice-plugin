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
import com.google.inject.tools.suite.snippets.BindingCodeLocation.ImplicitBindingLocation;
import com.google.inject.tools.suite.snippets.BindingCodeLocation.NoBindingLocation;
import com.google.inject.tools.suite.snippets.problems.BadClassProblem;
import com.google.inject.tools.suite.snippets.problems.CodeProblem;
import com.google.inject.tools.suite.snippets.problems.CreationProblem;
import com.google.inject.tools.suite.snippets.problems.InjectorProblem;
import com.google.inject.tools.suite.snippets.problems.InvalidModuleProblem;
import com.google.inject.tools.suite.snippets.problems.KeyProblem;
import com.google.inject.tools.suite.snippets.problems.LocationProblem;
import com.google.inject.tools.suite.snippets.problems.OutOfScopeProblem;
import com.google.inject.tools.suite.snippets.problems.ScopeProblem;

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
    
    public void addText(String text) {
      addText(text, null);
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
    if (location instanceof ImplicitBindingLocation) {
      return new ImplicitBindingLocationActionString(
          (ImplicitBindingLocation)location);
    } else if (location instanceof NoBindingLocation) {
      return new NoBindingLocationActionString(
          (NoBindingLocation)location);
    } else if (location instanceof BindingCodeLocation) {
      return new BindingCodeLocationActionString(
          (BindingCodeLocation)location);
    } else {
      throw new UnsupportedCodeLocationException(location);
    }
  }
  
  static class UnsupportedCodeLocationException extends RuntimeException {
    private final CodeLocation location;
    public UnsupportedCodeLocationException(CodeLocation location) {
      this.location = location;
    }
    @Override
    public String toString() {
      return "Unsupported CodeLocation: " + location;
    }
  }
  
  static class NoBindingLocationActionString extends ActionString {
    public NoBindingLocationActionString(NoBindingLocation location) {
      super();
      String theClass = location.getTheClass();
      addText("No binding for ");
      addTextWithAction(ClassNameUtility.shorten(theClass), new ActionsHandler.GotoFile(
        theClass), "Goto source of " + theClass);
    }
  }
  
  static class ImplicitBindingLocationActionString extends ActionString {
    public ImplicitBindingLocationActionString(ImplicitBindingLocation location) {
      super();
      String theClass = location.getTheClass();
      addTextWithAction(ClassNameUtility.shorten(theClass), new ActionsHandler.GotoFile(
          theClass), "Goto source of " + theClass);
      addText(" is implicitly bound");
    }
  }
  
  static class BindingCodeLocationActionString extends ActionString {
    public BindingCodeLocationActionString(BindingCodeLocation location) {
      super();
      String bindWhat = location.bindWhat();
      String annotatedWith = location.annotatedWith();
      String bindTo = location.bindTo();
      String bindToProvider = location.bindToProvider();
      String bindToInstance = location.bindToInstance();
      String file = location.file();
      int line = location.location();
      addTextWithAction(ClassNameUtility.shorten(bindWhat), new ActionsHandler.GotoFile(
          bindWhat), "Goto source of " + bindWhat);
      if (annotatedWith != null) {
        addText(" annotated with ");
        addText(ClassNameUtility.shorten(annotatedWith), annotatedWith);
      }
      if (bindToProvider != null) {
        addText(" is bound to the provider ");
        addTextWithAction(ClassNameUtility.shorten(bindToProvider),
            new ActionsHandler.GotoFile(bindToProvider), "Goto source of " + bindToProvider);
      } else if (bindTo != null) {
        if (bindToInstance != null) {
          addText(" is bound to the instance " + bindToInstance + " of ");
        } else {
          addText(" is bound to ");
        }
        addTextWithAction(ClassNameUtility.shorten(bindTo),
            new ActionsHandler.GotoFile(bindTo), "Goto source of " + bindTo);
      } else if (bindToInstance != null) {
        addText(" is bound to the instance " + bindToInstance);
      } else {
        addText(" has an unresolvable binding");
      }
      if (file != null) {
        addText(" at ");
        addTextWithAction(file + ":" + String.valueOf(line),
            new ActionsHandler.GotoCodeLocation(location.getStackTrace(), file,
                line), "Goto binding location of " + bindWhat + " as "
                + bindTo);
      }
      if (location.locationDescription() != null) {
        addText(" " + location.locationDescription());
      }
    }
  }
  
  /**
   * Create an ActionString for a {@link CodeProblem}.
   */
  public static ActionString getDisplayString(CodeProblem problem) {
    if (problem instanceof BadClassProblem) {
      return new BadClassProblemActionString((BadClassProblem)problem);
    } else if (problem instanceof CreationProblem) {
      return new CreationProblemActionString((CreationProblem)problem);
    } else if (problem instanceof InjectorProblem) {
      return new InjectorProblemActionString((InjectorProblem)problem);
    } else if (problem instanceof InvalidModuleProblem) {
      return new InvalidModuleProblemActionString((InvalidModuleProblem)problem);
    } else if (problem instanceof KeyProblem) {
      return new KeyProblemActionString((KeyProblem)problem);
    } else if (problem instanceof LocationProblem) {
      return new LocationProblemActionString((LocationProblem)problem);
    } else if (problem instanceof OutOfScopeProblem) {
      return new OutOfScopeProblemActionString((OutOfScopeProblem)problem);
    } else if (problem instanceof ScopeProblem) {
      return new ScopeProblemActionString((ScopeProblem)problem);
    } else {
      return new CodeProblemActionString(problem);
    }
  }
  
  static class CodeProblemActionString extends ActionString {
    public CodeProblemActionString(CodeProblem problem) {
      super();
      addText("Guice Code Problem: ");
      addText(problem.getMessage());
    }
  }
  
  static class BadClassProblemActionString extends ActionString {
    public BadClassProblemActionString(BadClassProblem problem) {
      super();
      String theClass = problem.className();
      addText("Bad Class Problem: ");
      addTextWithAction(ClassNameUtility.shorten(theClass), new ActionsHandler.GotoFile(
          theClass), "Goto source of " + theClass);    }
  }
  
  static class CreationProblemActionString extends ActionString {
    public CreationProblemActionString(CreationProblem problem) {
      super();
      addText("Guice Creation Exception: ");
      addText(problem.getMessage());
    }
  }
  
  static class InjectorProblemActionString extends ActionString {
    public InjectorProblemActionString(InjectorProblem problem) {
      super();
      addText("Guice Injector Problem: ");
      addText(problem.getMessage());
    }
  }
  
  static class InvalidModuleProblemActionString extends ActionString {
    public InvalidModuleProblemActionString(InvalidModuleProblem problem) {
      super();
      addText("Guice Invalid Module: ");
      addText(problem.moduleName());
    }
  }
  
  static class KeyProblemActionString extends ActionString {
    public KeyProblemActionString(KeyProblem problem) {
      super();
      addText("Guice Key Problem: ");
      addText(problem.bindWhat());
    }
  }
  
  static class LocationProblemActionString extends ActionString {
    public LocationProblemActionString(LocationProblem problem) {
      super();
      addText("Guice Location Problem: ");
      addText(problem.getMessage());
    }
  }
  
  static class OutOfScopeProblemActionString extends ActionString {
    public OutOfScopeProblemActionString(OutOfScopeProblem problem) {
      super();
      addText("Guice Out of Scope Problem: ");
      addText(problem.getMessage());
    }
  }
  
  static class ScopeProblemActionString extends ActionString {
    public ScopeProblemActionString(ScopeProblem problem) {
      super();
      addText("Guice Scope Problem: ");
      addText(problem.getMessage());
    }
  }
}
