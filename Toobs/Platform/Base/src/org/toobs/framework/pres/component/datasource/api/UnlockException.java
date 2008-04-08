package org.toobs.framework.pres.component.datasource.api;

import org.toobs.framework.exception.BaseException;

/**
 * @author stewari
 */
public class UnlockException extends BaseException {
    
    private String dataSourceId;
    private String objectId;
    
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
