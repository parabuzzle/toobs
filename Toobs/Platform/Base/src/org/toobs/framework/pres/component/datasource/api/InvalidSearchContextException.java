package org.toobs.framework.pres.component.datasource.api;

import org.toobs.framework.exception.BaseException;

/**
 * @author stewari
 */
public class InvalidSearchContextException extends BaseException {
    
    private String specifiedContext;
    
    public void setSpecifiedContext(String specifiedContext) {
        this.specifiedContext = specifiedContext;
    }
    
    public String getSpecifiedContext() {
        return this.specifiedContext;
    }
}
