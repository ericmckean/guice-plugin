package com.google.inject.tools.ideplugin;

import com.google.inject.tools.ideplugin.ActionsHandler.GotoCodeLocation;

public interface GotoCodeLocationHandler {
  public void run(GotoCodeLocation action);
}
