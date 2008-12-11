package org.toobsframework.pres.component.datasource.api;

/**
 * @author stewari
 */
public interface IDataSourceObjectProperty {

    /**
     * get parent object's unique identifier
     * @return parent object Id
     */
    public String getParentId();
    
     
    /**
     * get the property name
     * @return property name
     */
    public String getPropertyName();
     
    /**
     * get property type, e.g., simple, indexed, mapped
     * @return property type
     */
    public PropertyType getPropertyType();
    
    /**
     * get property value type
     * @return property type
     */
    public Class getValueType();
    
    /**
     * get property value
     * @return property value
     */
    public Object getPropertyValue();
    
    /**
     * Check if this is a mapped property. A mapped property is one that
     * represents a map, i.e., name-value pairs
     * @return true if this is a mapped property, false otherwise
     */
    public boolean isMapped();
    
    /**
     * Check if this is an indexed property. An indexed property is one that
     * represents a collection, i.e., list, array
     * @return true if this is an indexed property, false otherwise
     */
    public boolean isIndexed();
    
    /**
     * Check if this is a simple property.
     * @return true if this is a simple property, false otherwise
     */
    public boolean isSimple();
}
