package org.toobs.framework.pres.doit;

import org.toobs.framework.exception.BaseException;

public class DoItException extends BaseException {

  public DoItException() {
    super();
  }

  public DoItException(String message, Throwable cause) {
    super(message, cause);
  }

  public DoItException(String message) {
    super(message);
  }

  public DoItException(Throwable cause) {
    super(cause);
  }

}
