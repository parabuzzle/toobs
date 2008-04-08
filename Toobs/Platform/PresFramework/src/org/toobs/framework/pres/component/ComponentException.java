package org.toobs.framework.pres.component;

import org.toobs.framework.exception.BaseException;

/**
 * @author stewari
 */
public class ComponentException extends BaseException {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  /**
   * Default constructor
   */
  public ComponentException() {
    super();
  }

  /**
   * Constructor with a message parameter
   * 
   * @param message
   *          String
   */
  public ComponentException(String message) {
    super(message);
  }

  /**
   * Constructor with message, and cause
   * 
   * @param message
   *          String
   * @param cause
   *          Throwable
   */
  public ComponentException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Constructor with cause
   * 
   * @param cause
   *          Throwable
   */
  public ComponentException(Throwable cause) {
    super(cause);
  }

  private String componentId;

  public void setComponentId(String componentId) {
    this.componentId = componentId;
  }

  public String getComponentId() {
    return this.componentId;
  }
}
