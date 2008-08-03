package org.toobsframework.pres.component.datasource.api;

import org.toobsframework.exception.BaseException;


/**
 * @author stewari
 */
public class LockException extends BaseException {
    
    private String dataSourceId;
    private String objectId;
    
    public LockException(String message) {
        super(message);
    }
    
    public void setDataSourceId(String dataSourceId) {
        this.dataSourceId = dataSourceId;
    }
    
    public String getDataSourceId() {
        return this.dataSourceId;
    }
    
    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }
    
    public String getObjectId() {
        return this.objectId;
    }
}
