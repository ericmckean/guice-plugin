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

import com.google.inject.Inject;

/**
 * 
 * @author Darren Creutz <dcreutz@gmail.com>
 */
public class ActionsHandlerImpl implements ActionsHandler {
  private final GotoCodeLocationHandler gotoCodeLocationHandler;
  private final GotoFileHandler gotoFileHandler;
  
  @Inject
  public ActionsHandlerImpl(GotoCodeLocationHandler gotoCodeLocationHandler, GotoFileHandler gotoFileHandler) {
    this.gotoCodeLocationHandler = gotoCodeLocationHandler;
    this.gotoFileHandler = gotoFileHandler;
  }
  
  public void run(GotoCodeLocation action) {
    gotoCodeLocationHandler.run(action);
  }

  public void run(GotoFile action) {
    gotoFileHandler.run(action);
  }
  
  public void run(NullAction action) {
    //do nothing
  }
  
  public void run(Action action) {
    if (action instanceof GotoCodeLocation) {
      run((GotoCodeLocation)action);
    } else 
      if (action instanceof GotoFile) {
        run((GotoFile)action);
      } else
        if (action instanceof NullAction) {
          run((NullAction)action);
        } else
          throw new InvalidActionException(action);
  }
}

