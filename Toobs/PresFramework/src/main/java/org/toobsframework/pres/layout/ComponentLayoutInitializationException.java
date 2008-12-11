package org.toobsframework.pres.layout;

import org.toobsframework.exception.BaseException;

/**
 * @author stewari
 */
public class ComponentLayoutInitializationException extends BaseException {
    
    /**
   * 
   */
  private static final long serialVersionUID = 1L;
    private String componentId;
    
    public ComponentLayoutInitializationException(String message, Throwable cause) {
      super(message, cause);
    }

    public ComponentLayoutInitializationException(Throwable cause) {
      super(cause);
    }

    public ComponentLayoutInitializationException() {
        super();
    }
    
    public ComponentLayoutInitializationException(String message) {
        super(message);
    }
    
    public void setComponentId(String componentId) {
        this.componentId = componentId;
    }
    
    public String getComponentId() {
        return this.componentId;
    }
}
