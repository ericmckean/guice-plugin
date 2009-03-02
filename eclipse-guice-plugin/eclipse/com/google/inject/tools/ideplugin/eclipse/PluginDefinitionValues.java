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

/**
 * Plugin ID values, locations of files, etc.
 * 
 * @author Darren Creutz (dcreutz@gmail.com)
 */
final class PluginDefinitionValues {
  public static final String BASE_ID = "com.google.inject.tools.ideplugin.eclipse";
  
  public static final String BUNDLE_ID = BASE_ID + ".guiceplugin";
  
  //ViewPart IDs
  public static final String RESULTS_VIEW_ID = BASE_ID + ".EclipseResultsView";
  public static final String ERROR_VIEW_ID = BASE_ID + ".EclipseErrorView";
  
  public static final String PREFERENCES_ID = BASE_ID + ".preferences";
  
  //Library locations
  public static final String GUICE_TOOLS_FRAMEWORK_JAR = "lib/GuiceToolsFramework_0.3.5.jar";
  public static final String GUICE_JAR = "lib/Guice/guice-snapshot20080909.jar";
  public static final String AOPALLIANCE_JAR= "lib/Guice/aopalliance.jar";
  public static final String ASM_JAR = "lib/Guice/asm-2.2.3.jar";
  public static final String CGLIB_JAR = "lib/Guice/cglib-2.2_beta1.jar";
  
  //Icon locations
  public static final String GUICE_ICON = "icons/guice.gif";
  public static final String GUICE_ERRORS_ICON = "icons/guiceerrors.gif";
  public static final String FIND_BINDINGS_ICON = "icons/findbindings.gif";
  public static final String CONFIGURE_ICON = "icons/configure.gif";
  public static final String RUN_NOW_ICON = "icons/runnow.gif";
}
