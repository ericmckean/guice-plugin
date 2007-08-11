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

import java.util.HashSet;
import java.util.Set;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.ITypeHierarchyChangedListener;
import org.eclipse.jdt.core.JavaModelException;
import com.google.inject.Singleton;
import com.google.inject.Inject;
import com.google.inject.tools.ideplugin.module.ModulesListener;
import com.google.inject.tools.ideplugin.module.ModuleManager;
import com.google.inject.tools.ideplugin.Messenger;
import com.google.inject.tools.ideplugin.JavaProject;

//TODO: listen to module classes for code changes

/**
 * Eclipse implementation of the {@link ModulesListener}.
 * 
 * @author Darren Creutz <dcreutz@gmail.com>
 */
@Singleton
public class EclipseModulesListener implements ModulesListener {
	private final Messenger messenger;
	private final HashSet<String> modules;
	private final ModuleManager moduleManager;
	private ITypeHierarchy typeHierarchy = null;
	private MyTypeHierarchyChangedListener typeHierarchyListener;
	private IType type;
	private IJavaProject javaProject;
	
	/**
	 * Create an EclipseModulesListener.  This should be injected.
	 */
	@Inject
	public EclipseModulesListener(ModuleManager moduleManager,Messenger messenger) {
		this.moduleManager = moduleManager;
		this.messenger = messenger;
		javaProject = null;
		modules = new HashSet<String>();
		initialize();
	}
	
	/**
	 * (non-Javadoc)
	 * @see com.google.inject.tools.ideplugin.module.ModulesListener#projectChanged(com.google.inject.tools.ideplugin.JavaProject)
	 */
	public void projectChanged(JavaProject project) {
		if (project instanceof EclipseJavaProject) {
			javaProject = ((EclipseJavaProject)project).getIJavaProject();
			initialize();
		} else {
			javaProject = null;
		}
	}
	
	private boolean initialize() {
		if (javaProject != null) {
	      try {
			type = javaProject.findType("com.google.inject.Module");
			typeHierarchy = type.newTypeHierarchy(null);
	      } catch (JavaModelException exception) {
	    	hadProblem(exception);
	      } finally {
			typeHierarchyListener = new MyTypeHierarchyChangedListener();
			if (typeHierarchy!=null) {
			  typeHierarchy.addTypeHierarchyChangedListener(typeHierarchyListener);
			  findModulesInCode();
			  return true;
			}
	      }
	    }
	    return false;
	}
	
	/**
	 * (non-Javadoc)
	 * @see com.google.inject.tools.ideplugin.module.ModulesListener#findModules()
	 */
	public Set<String> findModules() {
	    if (typeHierarchy!=null) {
	      try {
	        typeHierarchy.refresh(null);
	      } catch (JavaModelException exception) {
			hadProblem(exception);
	      }
	      findModulesInCode();
	      return new HashSet<String>(modules);
	    } else {
	      if (initialize()) return findModules();
	      else return new HashSet<String>();
	    }
	}
	
	private class MyTypeHierarchyChangedListener implements ITypeHierarchyChangedListener {
		public void typeHierarchyChanged(ITypeHierarchy typeHierarchy) {
			EclipseModulesListener.this.setTypeHierarchy(typeHierarchy);
			typeHierarchy.addTypeHierarchyChangedListener(this);
		}
	}
	
	private void setTypeHierarchy(ITypeHierarchy typeHierarchy) {
		this.typeHierarchy = typeHierarchy;
		findModulesInCode();
	}
	
	private void findModulesInCode() {
		final HashSet<String> moduleNames = new HashSet<String>();
		IType[] subclasses = typeHierarchy.getAllSubtypes(type);
		for (IType subclass : subclasses) {
			try {
				if (subclass.isClass()) {
					if (!Flags.isAbstract(subclass.getFlags())) {
						moduleNames.add(subclass.getFullyQualifiedName());
					}
				}
			} catch (JavaModelException exception) {
				hadProblem(exception);
			}
		}
		keepModulesByName(moduleNames);
	}
	
  //TODO: move up and test
	private synchronized void keepModulesByName(Set<String> modulesNames) {
		for (String module : modules) {
			boolean keep = false;
			for (String name : modulesNames) {
				if (name.equals(module)) {
					keep = true;
					modulesNames.remove(name);
				}
			}
			if (!keep) {
				modules.remove(module);
				moduleManager.removeModule(module);
			}
		}
		for (String moduleName : modulesNames) {
			moduleManager.addModule(moduleName);
		}
	}
	
	private void hadProblem(JavaModelException exception) {
		//TODO: handle this for real
		messenger.display(exception.toString());
	}
	
	/**
	 * (non-Javadoc)
	 * @see com.google.inject.tools.ideplugin.module.ModulesListener#findChanges()
	 */
	public void findChanges() {
		if (javaProject != null) findModulesInCode();
	}
}
