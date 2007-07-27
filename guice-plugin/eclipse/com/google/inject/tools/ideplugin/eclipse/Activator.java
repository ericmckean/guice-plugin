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

package com.google.inject.tools.ideplugin.eclipse;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import com.google.inject.tools.ideplugin.eclipse.EclipsePluginModule;
import com.google.inject.tools.ideplugin.eclipse.EclipseGuicePlugin;

/** 
 * The activator is created when the plugin is first initialized; it is the equivalent of
 * the main method in a standard application.  We need to run the {@link com.google.inject.tools.ideplugin.GuicePlugin} here
 * to inject our dependencies.
 * 
 * @author Darren Creutz <dcreutz@gmail.com>
 */
public class Activator extends AbstractUIPlugin {
	private static EclipseGuicePlugin guicePlugin;
	
	/** 
	 * The ID of our plugin.
	 */
	public static final String PLUGIN_ID = "Guice_Plugin";

	private static Activator plugin;
	
	/**
	 * Create an activator and a GuicePlugin using the {@link EclipsePluginModule}.
	 */
	public Activator() {
		plugin = this;
		guicePlugin = new EclipseGuicePlugin(new EclipsePluginModule());
	}
	
	/** 
	 * Create an activator using the given module.
	 * FOR TESTING PURPOSES ONLY -- necessary since Eclipse forces a static Activator object on us.
	 */
	public Activator(EclipsePluginModule module) {
		plugin = this;
		guicePlugin = new EclipseGuicePlugin(module);
	}

	/** 
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
	}

	/** 
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}
	
	/** 
	 * Returns the GuicePlugin.
	 * 
	 * @return the GuicePlugin
	 */
	public static EclipseGuicePlugin getGuicePlugin() {
		return guicePlugin;
	}
}
