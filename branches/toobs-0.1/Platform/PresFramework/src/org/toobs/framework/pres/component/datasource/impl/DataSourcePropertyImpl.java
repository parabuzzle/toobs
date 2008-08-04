package org.toobs.framework.pres.component.datasource.impl;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.HashMap;

import org.toobs.framework.pres.component.datasource.api.IDataSourceObjectProperty;
import org.toobs.framework.pres.component.datasource.api.PropertyType;


public class DataSourcePropertyImpl implements IDataSourceObjectProperty {

  private String parentId;
  private String propertyName;
  private PropertyType propertyType;
  private Class valueType;
  private Object propertyValue;
  
  public DataSourcePropertyImpl(PropertyDescriptor propertyDescriptor) {
   
    this.propertyType = null;
    this.valueType = propertyDescriptor.getPropertyType();
    if (this.propertyType.getName().equals(PropertyType.INDEXED)) {
      this.propertyValue = new ArrayList();
    } else if (this.propertyType.getName().equals(PropertyType.MAPPED)) {
      this.propertyValue = new HashMap();
    }
  }

  public String getParentId() {
    return parentId;
  }

  public String getPropertyName() {
    return propertyName;
  }

  public PropertyType getPropertyType() {
    return propertyType;
  }

  public Class getValueType() {
    return valueType;
  }

  public Object getPropertyValue() {
    return propertyValue;
  }

  public void setParentId(String parentId) {
    this.parentId = parentId;
  }

  public void setPropertyName(String propertyName) {
    this.propertyName = propertyName;
  }

  public void setPropertyType(PropertyType propertyType) {
    this.propertyType = propertyType;
  }

  public void setPropertyValue(Object propertyValue) {
    this.propertyValue = propertyValue;
  }

  public void setValueType(Class valueType) {
    this.valueType = valueType;
  }

  /**
   * Check if this is a mapped property. A mapped property is one that
   * represents a map, i.e., name-value pairs
   * @return true if this is a mapped property, false otherwise
   */
  public boolean isMapped() {
      return this.propertyType.getName().equals(PropertyType.MAPPED.getName()); 
  }
  
  /**
   * Check if this is an indexed property. An indexed property is one that
   * represents a collection, i.e., list, array
   * @return true if this is an indexed property, false otherwise
   */
  public boolean isIndexed() {
      return this.propertyType.getName().equals(PropertyType.INDEXED.getName()); 
  }
  
  /**
   * Check if this is a simple property.
   * @return true if this is a simple property, false otherwise
   */
  public boolean isSimple() {
      return this.propertyType.getName().equals(PropertyType.SIMPLE.getName()); 
  }

}
