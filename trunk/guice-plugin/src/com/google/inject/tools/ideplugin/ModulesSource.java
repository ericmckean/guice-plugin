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

package com.google.inject.tools.ideplugin;

import com.google.inject.tools.ideplugin.ModulesSource;
import com.google.inject.tools.suite.module.ModuleRepresentation;

/**
 * Responsible for listening to changes in the user's code involving
 * {@link com.google.inject.Module}s. IDE specific implementations should
 * notify the ModuleManager when changes occur by creating
 * {@link ModuleRepresentation} objects and passing them to the manager.
 * 
 * @author Darren Creutz (dcreutz@gmail.com)
 */
public interface ModulesSource extends Source {
}
