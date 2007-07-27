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

import com.google.inject.Singleton;
import java.util.Set;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Collection;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.ITypeHierarchyChangedListener;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Flags;

import com.google.inject.tools.ideplugin.module.ModuleContextRepresentation;
import com.google.inject.tools.ideplugin.module.ModuleContextRepresentationImpl;
import com.google.inject.tools.ideplugin.module.ModuleSelectionView;

//TODO: write this

/**
 * Eclipse implementation of the {@link ModuleSelectionView}.
 * 
 * @author Darren Creutz <dcreutz@gmail.com>
 */
@Singleton
public class EclipseModuleSelectionView implements ModuleSelectionView {	
	private final Map<String,ModuleContextRepresentation> modules;
	private ITypeHierarchy typeHierarchy = null;
	private MyTypeHierarchyChangedListener typeHierarchyListener;
	private IType type;
	
	/**
	 * Create an EclipseModuleSelectionView.  This will be done by Eclipse internally.
	 */
	public EclipseModuleSelectionView() {
		modules = new HashMap<String,ModuleContextRepresentation>();
		initialize();
	}
	
	/**
	 * Initialize the ModuleSelectionView.
	 */
	public void initialize() {
		//modules = new HashMap<String,ModuleContextRepresentation>();
		IJavaProject javaProject = JavaCore.create(ResourcesPlugin.getWorkspace().getRoot().getProject());
		try {
			type = javaProject.findType("com.google.inject.AbstractModule");
			typeHierarchy = type.newTypeHierarchy(null);
		} catch (JavaModelException exception) {
			type = null;
			typeHierarchy = null;
			//TODO: ....
		} finally {
			typeHierarchyListener = new MyTypeHierarchyChangedListener();
			typeHierarchy.addTypeHierarchyChangedListener(typeHierarchyListener);
			findModulesInCode();
		}
	}
	
	public Collection<ModuleContextRepresentation> getAllModules() {
		try {
			typeHierarchy.refresh(null);
		} catch (JavaModelException exception) {
			//TODO: ....
		}
		findModulesInCode();
		return modules.values();
	}
	
	private class MyTypeHierarchyChangedListener implements ITypeHierarchyChangedListener {
		public void typeHierarchyChanged(ITypeHierarchy typeHierarchy) {
			EclipseModuleSelectionView.this.setTypeHierarchy(typeHierarchy);
			typeHierarchy.addTypeHierarchyChangedListener(this);
		}
	}
	
	private void setTypeHierarchy(ITypeHierarchy typeHierarchy) {
		this.typeHierarchy = typeHierarchy;
		findModulesInCode();
	}
	
	private void findModulesInCode() {
		final HashSet<String> moduleNames = new HashSet<String>();
		IType[] subclasses = typeHierarchy.getSubclasses(type);
		for (IType subclass : subclasses) {
			try {
				if (subclass.isClass()) {
					if (!Flags.isAbstract(subclass.getFlags())) {
						moduleNames.add(subclass.getFullyQualifiedName());
					}
				}
			} catch (JavaModelException exception) {
				//TODO: ....
			}
		}
		keepModulesByName(moduleNames);
	}
	
	private synchronized void keepModulesByName(Set<String> modulesNames) {
		for (String name : modules.keySet()) {
			if (!modulesNames.contains(name)) {
				modules.remove(name);
			}
		}
		for (String name : modulesNames) {
			if (!modules.containsKey(name)) {
				modules.put(name,new ModuleContextRepresentationImpl(name));
			}
		}
	}
}
