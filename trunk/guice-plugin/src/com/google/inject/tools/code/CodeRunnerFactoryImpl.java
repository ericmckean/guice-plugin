package com.google.inject.tools.code;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.binder.AnnotatedBindingBuilder;
import com.google.inject.tools.JavaManager;
import com.google.inject.tools.Messenger;
import com.google.inject.tools.ProgressHandler;
import com.google.inject.tools.GuiceToolsModule.CodeRunnerFactory;

public class CodeRunnerFactoryImpl implements CodeRunnerFactory {
  private final Provider<ProgressHandler> progressHandlerProvider;
  private final Messenger messenger;

  @Inject
  public CodeRunnerFactoryImpl(
      Provider<ProgressHandler> progressHandlerProvider, Messenger messenger) {
    this.progressHandlerProvider = progressHandlerProvider;
    this.messenger = messenger;
  }

  public CodeRunner create(JavaManager project) {
    return new CodeRunnerImpl(project, progressHandlerProvider.get(),
        messenger);
  }
  
  public static void bindCodeRunner(AnnotatedBindingBuilder<CodeRunner> bindCodeRunner) {
    bindCodeRunner.to(CodeRunnerImpl.class);
  }
}