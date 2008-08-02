package org.toobs.framework.pres.component.datasource.api;

import org.toobs.framework.exception.BaseException;

/**
 * @author stewari
 */
public class DataSourceNotInitializedException extends BaseException {
    
    private String dataSourceId;
     
    public DataSourceNotInitializedException() {
        super();
    }
    
    public DataSourceNotInitializedException(String message) {
        super(message);
    }
    
    public void setDataSourceId(String dataSourceId) {
        this.dataSourceId = dataSourceId;
    }
    
    public String getDataSourceId() {
        return this.dataSourceId;
    }
}
