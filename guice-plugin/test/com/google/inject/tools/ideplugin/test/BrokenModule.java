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

package com.google.inject.tools.ideplugin.test;

import com.google.inject.AbstractModule;

/**
 * Guice {@link com.google.inject.Module} that is broken and will throw a {@link com.google.inject.CreationException}.
 * 
 * @author Darren Creutz <dcreutz@gmail.com>
 */
public class BrokenModule extends AbstractModule {
	/*
	 * (non-Javadoc)
	 * @see com.google.inject.AbstractModule#configure()
	 */
	@Override
	protected void configure() {
		bind(MockInjectedInterface.class).to(MockInjectedInterfaceImpl.class);
		bind(MockInjectedInterface.class).to(MockInjectedInterfaceImpl.class);
		//this will throw a CreationException as its already bound
	}
}
