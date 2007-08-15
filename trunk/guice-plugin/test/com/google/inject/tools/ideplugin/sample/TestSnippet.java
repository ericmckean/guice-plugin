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

package com.google.inject.tools.ideplugin.sample;

import java.util.HashSet;
import com.google.inject.tools.ideplugin.snippets.CodeProblem;
import com.google.inject.tools.ideplugin.snippets.CodeSnippet;
import com.google.inject.tools.ideplugin.snippets.CodeSnippetResult;

/**
 * A sample {@link CodeSnippet} for testing the {@link CodeRunner}.
 * 
 * @author Darren Creutz <dcreutz@gmail.com>
 */
public class TestSnippet extends CodeSnippet {
  public TestSnippet(int secsToTake) {
    super();
    try {
      Thread.sleep(secsToTake * 1000);
    } catch (Exception exception) {
      //do nothing
    }
  }
  
  public static class TestSnippetResult extends CodeSnippetResult {
    private final String blah = "blah";
    public TestSnippetResult() {
      super(new HashSet<CodeProblem>());
    }
    public String getBlah() {
      return blah;
    }
  }
  
  @Override
  public CodeSnippetResult getResult() {
    return new TestSnippetResult();
  }
  public static void main(String[] args) {
    int secsToTake;
    if (args.length > 0) secsToTake = Integer.valueOf(args[0]);
    else secsToTake = -1;
    new TestSnippet(secsToTake).printResult(System.out);
  }
}