package org.toobsframework.pres.component.datasource.api;

import java.util.Map;
import java.util.List;
import java.util.Iterator;

import org.apache.commons.lang.enums.Enum;


/**
 * @author stewari
 */
public class SearchScope extends Enum {
    
    public static final SearchScope ONELEVEL_SCOPE = new SearchScope("OnelevelScope");
    public static final SearchScope SUBTREE_SCOPE = new SearchScope("SubtreeScope");
    
    private SearchScope(String scope) {
        super(scope);
    }
    
    public static SearchScope getEnum(String scope) {
        return (SearchScope) getEnum(SearchScope.class, scope);
    }
    
    public static Map getEnumMap() {
        return getEnumMap(SearchScope.class);
    }
    
    public static List getEnumList() {
        return getEnumList(SearchScope.class);
    }
    
    public static Iterator iterator() {
        return iterator(SearchScope.class);
    }
}
