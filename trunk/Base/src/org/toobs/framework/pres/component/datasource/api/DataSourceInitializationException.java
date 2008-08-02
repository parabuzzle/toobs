package org.toobs.framework.pres.component.datasource.api;

import org.toobs.framework.exception.BaseException;

/**
 * @author stewari
 */
public class DataSourceInitializationException extends BaseException {
    
    private String dataSourceId;
     
    public DataSourceInitializationException() {
        super();
    }
    
    public DataSourceInitializationException(String message) {
        super(message);
    }
    
    public void setDataSourceId(String dataSourceId) {
        this.dataSourceId = dataSourceId;
    }
    
    public String getDataSourceId() {
        return this.dataSourceId;
    }

    public DataSourceInitializationException(String message, Throwable cause) {
      super(message, cause);
    }

    public DataSourceInitializationException(Throwable cause) {
      super(cause);
    }
}
