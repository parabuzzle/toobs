package org.toobs.framework.pres.component.datasource.api;

import org.toobs.framework.exception.BaseException;

/**
 * @author stewari
 */
public class ObjectCreationException extends BaseException {
    
    private String dataSourceId;
    
    public ObjectCreationException(String message) {
        super(message);
    }
    
    public void setDataSourceId(String dataSourceId) {
        this.dataSourceId = dataSourceId;
    }
    
    public String getDataSourceId() {
        return this.dataSourceId;
    }

    public ObjectCreationException() {
      super();
      // TODO Auto-generated constructor stub
    }

    public ObjectCreationException(String message, Throwable cause) {
      super(message, cause);
      // TODO Auto-generated constructor stub
    }

    public ObjectCreationException(Throwable cause) {
      super(cause);
      // TODO Auto-generated constructor stub
    }
}
