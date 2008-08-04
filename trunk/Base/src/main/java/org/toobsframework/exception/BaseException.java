package org.toobsframework.exception;

import java.util.Vector;

/**
 * <p>
 *   This is the base exception for the framework.  
 *   It includes functionality for the developer to add
 *   a user message into the exception to be displayed
 *   to the end user.  If the constructor is passed a Base Exception
 *   then the existing user messages are copied over to the 
 *   newly created exception allowing chaining.
 * </p>
 */
@SuppressWarnings("unchecked")
public class BaseException
    extends Exception {

  /**
   * 
   */
  private static final long serialVersionUID = -2392366062472660769L;
  private Vector userMessages = new Vector();
  /**
   * Default constructor
   */
  public BaseException() {
    super();
  }

  /**
   * Constructor with a message parameter
   *
   * @param message String
   */
  public BaseException(String message) {
    super(message);
  }

  /**
   * Constructor with message, and cause
   *
   * @param message String
   * @param cause Throwable
   */
  public BaseException(String message, Throwable cause) {
    super(message, cause);
    if(cause instanceof BaseException){
      this.setUserMessages(((BaseException)cause).getUserMessages());
    }
  }

  /**
   * Constructor with cause
   *
   * @param cause Throwable
   */
  public BaseException(Throwable cause) {
    super(cause);
    if(cause instanceof BaseException){
      this.setUserMessages(((BaseException)cause).getUserMessages());
    }
  }

  public Throwable rootCause() {
    Throwable t = this;
    while (t != null && t.getCause() != null)
      t = t.getCause();
    return t;
  }
  
  public Vector getUserMessages() {
    return userMessages;
  }

  public void setUserMessages(Vector userMessages) {
    this.userMessages = userMessages;
  }

  public void addUserMessage(String userMessage) {
    this.userMessages.add(userMessage);
  }
}
