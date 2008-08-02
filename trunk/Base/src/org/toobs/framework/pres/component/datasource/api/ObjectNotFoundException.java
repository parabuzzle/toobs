package org.toobs.framework.pres.component.datasource.api;

import org.toobs.framework.exception.BaseException;

/**
 * @author stewari
 */
public class ObjectNotFoundException extends BaseException{

    private String objectId;
    private String dataSourceId;
    
    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }
    
    public String getObjectId() {
        return this.objectId;
    }
    
    public void setDataSourceId(String dataSourceId) {
        this.dataSourceId = dataSourceId;
    }
    
    public String getDataSourceId() {
        return this.dataSourceId;
    }

    public ObjectNotFoundException() {
      super();
    }

    public ObjectNotFoundException(String message, Throwable cause) {
      super(message, cause);
    }

    public ObjectNotFoundException(String message) {
      super(message);
    }

    public ObjectNotFoundException(Throwable cause) {
      super(cause);
    }
}
