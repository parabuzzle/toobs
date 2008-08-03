package org.toobsframework.pres.component.datasource.api;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.enums.Enum;

/**
 * @author stewari
 */
public class PropertyType extends Enum {
    
    public static final PropertyType SIMPLE = new PropertyType("simple");
    public static final PropertyType INDEXED = new PropertyType("indexed");
    public static final PropertyType MAPPED = new PropertyType("mapped");
    
    private PropertyType(String type) {
        super(type);
    }
    
    public static PropertyType getEnum(String type) {
        return (PropertyType) getEnum(PropertyType.class, type);
    }
    
    public static Map getEnumMap() {
        return getEnumMap(PropertyType.class);
    }
    
    public static List getEnumList() {
        return getEnumList(PropertyType.class);
    }
    
    public static Iterator iterator() {
        return iterator(PropertyType.class);
    }
}
