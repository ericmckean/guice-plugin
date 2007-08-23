package com.google.inject.tools.code;

import com.google.inject.tools.module.CustomModuleContextRepresentation;
import com.google.inject.tools.snippets.ModuleContextSnippet;

import java.util.ArrayList;
import java.util.List;

public class RunCustomModuleContextSnippet extends CodeRunner.Runnable {
  private final CustomModuleContextRepresentation moduleContext;
  
  public RunCustomModuleContextSnippet(CodeRunner codeRunner, CustomModuleContextRepresentation moduleContext) {
    super(codeRunner);
    this.moduleContext = moduleContext;
  }

  @Override
  public String label() {
    return "Running module context " + moduleContext.getName();
  }
  
  @Override
  protected String getFullyQualifiedSnippetClass() {
    return ModuleContextSnippet.class.getName();
  }
  
  @Override
  protected List<? extends Object> getSnippetArguments() {
    final List<Object> args = new ArrayList<Object>();
    args.add(moduleContext.getName());
    args.add(String.valueOf(-1));
    args.add(moduleContext.getClassToUse());
    args.add(moduleContext.getMethodToCall());
    return args;
  }
}
