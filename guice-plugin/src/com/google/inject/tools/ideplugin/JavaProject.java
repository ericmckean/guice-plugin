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

public interface JavaProject {
  /**
   * Return the command line for running java.
   * 
   * @throws Exception if the command line cannot be determined
   */
  public String getJavaCommand() throws Exception;
  
  /**
   * Return the classpath entry for the precompiled snippets.jar file.
   * 
   * @throws Exception if the classpath cannot be determined
   */
  public String getSnippetsClasspath() throws Exception;
  
  /**
   * Return the classpath for the project.
   * 
   * @throws Exception if the classpath cannot be determined
   */
  public String getProjectClasspath() throws Exception;
}
