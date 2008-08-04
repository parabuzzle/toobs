package org.toobs.framework.pres.component.datasource.api;

import org.toobs.framework.exception.BaseException;


/**
 * @author stewari
 */
public class InvalidContextException extends BaseException {
    
    private String dataSourceId;
    private String context;
    
    public InvalidContextException() {
        super();
    }
    
    public InvalidContextException(String message) {
        super(message);
    }
    
    public void setDataSourceId(String dataSourceId) {
        this.dataSourceId = dataSourceId;
    }
    
    public String getDataSourceId() {
        return this.dataSourceId;
    }
    
    public void setContext(String context) {
        this.context = context;
    }
    
    public String getContext() {
        return this.context;
    }
}
