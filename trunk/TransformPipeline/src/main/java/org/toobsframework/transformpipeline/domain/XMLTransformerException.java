package org.toobsframework.transformpipeline.domain;

import org.toobsframework.exception.BaseException;

public class XMLTransformerException extends BaseException {
  /**
   * 
   */
  private static final long serialVersionUID = -5261738594889638341L;

  /**
   * Default constructor
   */
  public XMLTransformerException() {
    super();
  }

  /**
   * Constructor with a message parameter
   *
   * @param message String
   */
  public XMLTransformerException(String message) {
    super(message);
  }


  /**
   * Constructor with message, and cause
   *
   * @param message String
   * @param cause Throwable
   */
  public XMLTransformerException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Constructor with cause
   *
   * @param cause Throwable
   */
  public XMLTransformerException(Throwable cause) {
    super(cause);
  }

}
