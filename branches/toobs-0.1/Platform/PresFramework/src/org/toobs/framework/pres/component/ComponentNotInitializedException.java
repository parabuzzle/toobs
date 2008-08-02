package org.toobs.framework.pres.component;

import org.toobs.framework.exception.BaseException;

/**
 * @author stewari
 */
public class ComponentNotInitializedException extends BaseException {
    
    /**
   * 
   */
  private static final long serialVersionUID = 1L;
    private String componentId;
    
    public void setComponentId(String componentId) {
        this.componentId = componentId;
    }
    
    public String getComponentId() {
        return this.componentId;
    }
}
