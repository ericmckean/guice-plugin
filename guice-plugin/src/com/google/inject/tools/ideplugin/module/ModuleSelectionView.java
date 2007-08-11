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

package com.google.inject.tools.ideplugin.module;

/**
 * Interface for IDE specific objects presenting the module choices to the user.
 * Responsible for notifying the {@link ModuleManager} of the user's selections by creating
 * {@link ModuleContextRepresentation} objects and passing them to the manager.
 * 
 * @author Darren Creutz <dcreutz@gmail.com>
 */
public interface ModuleSelectionView {
  //TODO: create modulecontextrepresentations based on user input
}
