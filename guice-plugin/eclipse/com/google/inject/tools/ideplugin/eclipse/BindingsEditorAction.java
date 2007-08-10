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

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitEditor;

import com.google.inject.tools.ideplugin.GuicePlugin;
import com.google.inject.tools.ideplugin.eclipse.EclipseJavaElement;

/** 
 * The action to take when the user selects some text and chooses "Find Bindings" from
 * the right click menu.
 * 
 * @author Darren Creutz <dcreutz@gmail.com>
 */
@SuppressWarnings("restriction")
//TODO: remove internal class use if possible
public class BindingsEditorAction implements IEditorActionDelegate {
	private IEditorPart editor;
	private GuicePlugin guicePlugin;
	
	/** 
	 * Create the action.
	 */
	public BindingsEditorAction() {
		super();
		guicePlugin = Activator.getGuicePlugin();
	}
	
	/**  (non-Javadoc)
	 * @see org.eclipse.ui.IEditorActionDelegate#setActiveEditor(org.eclipse.jface.action.IAction, org.eclipse.ui.IEditorPart)
	 */
	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		this.editor = targetEditor;
	}

	/**  (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run(IAction action) {
		ICompilationUnit cu = JavaPlugin.getDefault().getWorkingCopyManager().getWorkingCopy(((CompilationUnitEditor)editor).getEditorInput());
		ITextSelection selection = (ITextSelection)editor.getSite().getSelectionProvider().getSelection();
		IJavaElement element = null;
		try {
			IJavaElement[] elements = cu.codeSelect(selection.getOffset(),selection.getLength());
			if (elements.length > 0) {
				element = elements[0];
			}
			guicePlugin.getBindingsEngine(new EclipseJavaElement(element));
		} catch (JavaModelException exception) {
			guicePlugin.getMessenger().display("Not a Java element: " + selection.toString());
		}
	}

	/**  (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
	}
}
