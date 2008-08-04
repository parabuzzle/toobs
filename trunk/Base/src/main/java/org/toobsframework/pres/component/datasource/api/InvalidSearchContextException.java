package org.toobsframework.pres.component.datasource.api;

import org.toobsframework.exception.BaseException;

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
