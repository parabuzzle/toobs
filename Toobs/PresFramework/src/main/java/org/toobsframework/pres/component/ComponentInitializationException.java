package org.toobsframework.pres.component;

import org.toobsframework.exception.BaseException;

/**
 * @author stewari
 */
public class ComponentInitializationException extends BaseException {
    
    /**
   * 
   */
  private static final long serialVersionUID = 1L;
    private String componentId;
    
    public ComponentInitializationException(String message, Throwable cause) {
      super(message, cause);
    }

    public ComponentInitializationException(Throwable cause) {
      super(cause);
    }

    public ComponentInitializationException() {
        super();
    }
    
    public ComponentInitializationException(String message) {
        super(message);
    }
    
    public void setComponentId(String componentId) {
        this.componentId = componentId;
    }
    
    public String getComponentId() {
        return this.componentId;
    }
}
