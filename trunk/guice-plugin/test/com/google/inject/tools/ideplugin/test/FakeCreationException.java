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

import com.google.inject.CreationException;
import com.google.inject.spi.Message;
import java.util.HashSet;

/**
 * Mock the {@link CreationException} object.
 * 
 * @author Darren Creutz <dcreutz@gmail.com>
 */
public class FakeCreationException extends CreationException {
  /**
   * Automatically generated serial version UID.
   */
  private static final long serialVersionUID = -6889671178292449161L;
  private static HashSet<Message> messages = makeMessages();
  private static HashSet<Message> makeMessages() {
    HashSet<Message> collection = new HashSet<Message>();
    collection.add(new Message("Mock Guice Message."));
    return collection;
  }
  
  /**
   * Create the Mock object.
   */
  public FakeCreationException() {
    super(messages);
  }
  
  /**
   * (non-Javadoc)
   * @see java.lang.Throwable#toString()
   */
  @Override
  public String toString() {
    return "Mock Creation Exception.";
  }
}
