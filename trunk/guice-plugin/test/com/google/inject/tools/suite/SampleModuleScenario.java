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

package com.google.inject.tools.suite;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import com.google.inject.AbstractModule;
import com.google.inject.BindingAnnotation;
import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import com.google.inject.name.Named;

/**
 * Sample modules, interfaces and implementations for testing purposes.
 * 
 * NOTE: modifying this file will break the tests, you must also update the line
 * numbers of the bindings.
 * 
 * @author Darren Creutz (dcreutz@gmail.com)
 */
public class SampleModuleScenario {
  public static class WorkingModule extends AbstractModule {
    @Override
    protected void configure() {
      bind(MockInjectedInterface.class).to(MockInjectedInterfaceImpl.class);
      bind(Service.class).annotatedWith(Names.named("blue")).to(
          BlueService.class);
      bind(Service.class).annotatedWith(Red.class).to(
          RedService.class);
      bindConstant().annotatedWith(One.class).to(1);
      bind(new TypeLiteral<PaymentService<CreditCard>>() {}).to(
          CreditCardPaymentService.class);
      bind(ProvidedService.class).toProvider(new Provider<ProvidedService>() {
        public ProvidedService get() {
          return new ProvidedService() {};
        }
      });
    }
  }
  public static class BrokenModule extends AbstractModule {
    @Override
    protected void configure() {
      bind(MockInjectedInterface.class).to(MockInjectedInterfaceImpl.class);
      bind(MockInjectedInterface.class).to(MockInjectedInterfaceImpl.class);
      // this will throw a CreationException as its already bound
    }
  }
  public static class ModuleWithArguments extends AbstractModule {
    public ModuleWithArguments(String requiredArgument) {
    }

    @Override
    protected void configure() {
      bind(MockInjectedInterface.class).to(MockInjectedInterfaceImpl.class);
    }
  }
  public static class WorkingModule2 extends AbstractModule {
    @Override
    protected void configure() {
      bind(MockInjectedInterface2.class).to(MockInjectedInterface2Impl.class);
    }
  }

  public static class CustomContextBuilder {
    public Set<Module> getModules() {
      return Collections.singleton((Module) new WorkingModule());
    }
  }
  
  public static class StaticCustomContextBuilder {
    public static Set<Module> getModules() {
      return Collections.singleton((Module) new WorkingModule());
    }
  }


  public interface MockInjectedInterface {
  }
  public static class MockInjectedInterfaceImpl implements
      MockInjectedInterface {
  }
  public interface MockInjectedInterface2 {
  }
  public static class MockInjectedInterface2Impl implements
      MockInjectedInterface2 {
  }

  public interface Service {
  }
  public static class BlueService implements Service {
  }
  public static class RedService implements Service {
  }
  public static class ServiceImpl implements Service {
  }

  public static class CreditCard {
  }
  public static class CreditCardPaymentService implements
      PaymentService<CreditCard> {
  }
  public interface PaymentService<T> {
  }

  @BindingAnnotation
  @Retention(RetentionPolicy.RUNTIME)
  public @interface Red {
  }
  @BindingAnnotation
  @Retention(RetentionPolicy.RUNTIME)
  public @interface One {
  }
  
  public interface ProvidedService {
  }
  
  public static class Helper extends HashSet<Module> implements Iterable<Module> {
    private static final long serialVersionUID = -5935098537422867205L;

    public Helper() {
      add(new WorkingModule());
      add(new WorkingModule2());
    }
  }
  
  
  @Inject
  public SampleModuleScenario(MockInjectedInterface mockInjectedInterface,
      @Named("blue") Service blueService,
      @Named("red") Service redService,
      @One int one,
      PaymentService<CreditCard> creditCardPaymentService,
      ProvidedService providedService) {
    new AbstractModule() {
      @Override
      public void configure() {
        bind(MockInjectedInterface.class).to(MockInjectedInterfaceImpl.class);
      }
    };
  }
}
