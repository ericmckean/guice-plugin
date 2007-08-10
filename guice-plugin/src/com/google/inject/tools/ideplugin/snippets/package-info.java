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

/**
 * The code snippets that must be run in the user's workspace to resolve guice bindings etc.
 * 
 * <p>The snippets package will be compiled and shipped as a standalone .jar file in addition to
 * being part of the main project. The snippets.jar will be used to run code in userspace by the
 * {@link com.google.inject.tools.ideplugin.code.CodeRunner}. Essentially the user's code and the snippets.jar file will be placed in the
 * classpath of a newly launched java vm which will then run a snippet to determine how the
 * user's modules behave.
 */

package com.google.inject.tools.ideplugin.snippets;
