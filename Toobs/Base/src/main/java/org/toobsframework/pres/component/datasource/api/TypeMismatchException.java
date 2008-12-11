package org.toobsframework.pres.component.datasource.api;

import org.toobsframework.exception.BaseException;

/**
 * @author stewari
 */
public class TypeMismatchException extends BaseException {
    
    private String dataSourceId;
    private String propertyName;
    private Class expectedType;
    private Class givenType;

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
    
    public void setExpectedType(Class expectedType) {
        this.expectedType = expectedType;
    }
    
    public Class getExpectedType() {
        return this.expectedType;
    }
    
    public void setGivenType(Class givenType) {
        this.givenType = givenType;
    }
    
    public Class getGivenType() {
        return this.givenType;
    }
}
