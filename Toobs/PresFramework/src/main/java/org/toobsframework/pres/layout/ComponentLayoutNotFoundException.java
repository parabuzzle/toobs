package org.toobsframework.pres.layout;

import org.toobsframework.exception.BaseException;
/**
 * @author stewari
 */
public class ComponentLayoutNotFoundException extends BaseException {
   
  private static final long serialVersionUID = 1L;
  private String componentLayoutId;
  
  public void setComponentLayoutId(String componentLayoutId) {
      this.componentLayoutId = componentLayoutId;
  }
  
  public String getComponentLayoutId() {
      return this.componentLayoutId;
  }

  public String getMessage() {
    return "Component with Id " + componentLayoutId + " not found in registry";
  }
}
