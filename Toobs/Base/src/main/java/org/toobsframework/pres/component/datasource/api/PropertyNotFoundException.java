package org.toobsframework.pres.component.datasource.api;

import org.toobsframework.exception.BaseException;

/**
 * @author stewari
 */
public class PropertyNotFoundException extends BaseException{

    private String propertyName;
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
    
    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }
    
    public String getPropertyName() {
        return this.propertyName;
    }

    public PropertyNotFoundException() {
      super();
      // TODO Auto-generated constructor stub
    }

    public PropertyNotFoundException(String message, Throwable cause) {
      super(message, cause);
      // TODO Auto-generated constructor stub
    }

    public PropertyNotFoundException(String message) {
      super(message);
      // TODO Auto-generated constructor stub
    }

    public PropertyNotFoundException(Throwable cause) {
      super(cause);
      // TODO Auto-generated constructor stub
    }
}
