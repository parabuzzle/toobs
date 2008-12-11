package org.toobs.framework.exception;

@SuppressWarnings("serial")
public class ParameterException extends BaseException {
  private String context;
  private String name;
  private String path;
  
  public ParameterException(String context, String name, String path) {
    this.context = context;
    this.name = name;
    this.path = path;
  }

  public String getMessage() {
    return "Parameter in context " + context + " with name " 
    + name + " and path " + path + " could not be resolved";
  }
}
