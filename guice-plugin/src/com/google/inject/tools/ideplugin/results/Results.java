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

package com.google.inject.tools.ideplugin.results;

import java.util.Set;
import java.util.HashSet;
import com.google.inject.tools.ideplugin.ActionsHandler;

/**
 * Represents a set of results to be displayed by the {@link ResultsHandler}.  Builds a tree
 * structure of the results for display purposes.
 * 
 * @author Darren Creutz <dcreutz@gmail.com>
 */
public class Results {
	/**
	 * A Node in the tree structure of these results.
	 * 
	 * Warning: the tree is not synchronized by the Results object, the client is responsible.
	 */
	public class Node {
		private final String title;
		private final Set<Node> children;
		
		/**
		 * Create a new Node.
		 * 
		 * @param title the display title
		 */
		public Node(String title) {
			this.title = title;
			this.children = new HashSet<Node>();
		}
		
		/**
		 * Return the title of this node.
		 * 
		 * @return the title
		 */
		public String getTitle() {
			return title;
		}
		
		/**
		 * Add a Node as a child of this node.
		 * 
		 * @param child the node to add
		 */
		public void addChild(Node child) {
			children.add(child);
		}
		
		/**
		 * Return the children of this node.
		 * 
		 * @return the children
		 */
		public Set<Node> children() {
			return children;
		}

		/**
		 * (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("Results.Node(" + title + "){");
			for (Node child : children) {
				builder.append(child.toString());
				builder.append(",");
			}
			builder.append("}");
			return builder.toString();
		}
	}
	
	/**
	 * A Node in the tree that is (double) clickable and so has an {@link com.google.inject.tools.ideplugin.ActionsHandler.Action} 
	 * associated to it.
	 */
	public class ClickableNode extends Node {
		private final ActionsHandler.Action action;
		
		/**
		 * Create a ClickableNote with the given name and action.
		 * 
		 * @param name the name
		 * @param action the {@link com.google.inject.tools.ideplugin.ActionsHandler.Action}
		 */
		public ClickableNode(String name,ActionsHandler.Action action) {
			super(name);
			this.action = action;
		}
		
		/**
		 * Return the {@link com.google.inject.tools.ideplugin.ActionsHandler.Action} to perform when the node is clicked.
		 * 
		 * @return the action
		 */
		public ActionsHandler.Action getAction() {
			return action;
		}
	}
	
	private final Node root;
	
	/**
	 * Create a new Results object with the given title.
	 * 
	 * @param title the title
	 */
	public Results(String title) {
		root = new Node(title);
	}
	
	/**
	 * Return the root {@link Node} of the tree of results.
	 * 
	 * @return the root node
	 */
	public Node getRoot() {
		return root;
	}
	
	/**
	 * Notify the results object that the user cancelled the operation.
	 */
	public void userCancelled() {
		// do nothing
	}
}
