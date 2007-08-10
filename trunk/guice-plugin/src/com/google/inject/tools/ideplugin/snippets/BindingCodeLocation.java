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

package com.google.inject.tools.ideplugin.snippets;

import java.util.Set;
import com.google.inject.tools.ideplugin.ActionsHandler;
import com.google.inject.tools.ideplugin.results.Results.Node.ActionString;

/**
 * Represents the location in code of where a binding occurs.
 * 
 * @author Darren Creutz <dcreutz@gmail.com>
 */
public class BindingCodeLocation extends CodeLocation {
	private final String moduleContext;
	private final String bindWhat;
	private final String bindTo;
	
	/**
	 * Create a new BindingCodeLocation.
	 * 
	 * @param bindWhat the class to bind
	 * @param bindTo what it is bound to
	 * @param moduleContext the module context this binding happens in
	 * @param file the file this happens in
	 * @param location the line number in that file where this happens
	 * @param problems any {@link CodeProblem}s that occurred during getting this binding
	 */
	public BindingCodeLocation(String bindWhat,String bindTo,String moduleContext,String file,int location,Set<? extends CodeProblem> problems) {
		super(file,location,problems);
		this.bindWhat = bindWhat;
		this.bindTo = bindTo;
		this.moduleContext = moduleContext;
	}
	
	/**
	 * (non-Javadoc)
	 * @see com.google.inject.tools.ideplugin.snippets.CodeLocation#getDisplay()
	 */
	@Override
	public ActionString getDisplay() {
		ActionString text = new ActionString();
    text.addTextWithAction(bindWhat,new ActionsHandler.GotoFile(bindWhat));
    text.addText("is bound to");
    text.addTextWithAction(bindTo,new ActionsHandler.GotoFile(bindTo));
    text.addText("at");
    text.addTextWithAction(file() + ":" + String.valueOf(location()),new ActionsHandler.GotoCodeLocation(file(),location()));
    return text;
  }
	
	/**
	 * Return the module context this binding occurred in.
	 * 
	 * @return the module context
	 */
	public String getModuleContext() {
		return moduleContext;
	}
	
	/**
	 * Return the Class being bound.
	 * 
	 * @return the class
	 */
	public String bindWhat() {
		return bindWhat;
	}
  
  public String bindTo() {
    return bindTo;
  }
}
