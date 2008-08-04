package org.toobsframework.pres.component.datasource.manager;

import java.util.Map;

import org.apache.commons.beanutils.ConvertUtils;
import org.toobsframework.data.beanutil.converter.DateToStringConverter;
import org.toobsframework.pres.component.datasource.api.DataSourceInitializationException;
import org.toobsframework.pres.component.datasource.api.IDataSource;


/**
 * @author stewari
 * 
 * singleton class that manages datasources
 */
public final class DataSourceManager {
    
    private static DataSourceManager _instance;
    
    private DataSourceManager() {
      ConvertUtils.register(new DateToStringConverter(), String.class);
    }
    
    public static DataSourceManager getInstance() 
        throws DataSourceInitializationException {
        if (_instance == null) {
            _instance = new DataSourceManager();
        }
        return _instance;
    }
    
    public IDataSource getDataSource(String className, Map params) 
        throws DataSourceNotFoundException, DataSourceInitializationException {
    	
    	IDataSource ds = null;
    	try {
    		//Class dsClass = Class.forName(className);
    		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    		Class dsClass =  Class.forName (className, false, classLoader);
    		if (!IDataSource.class.isAssignableFrom(dsClass)) {
    			throw new DataSourceInitializationException();
    		}
    		ds = (IDataSource) dsClass.newInstance();
    		ds.init(params);
    	}
    	catch (IllegalAccessException ex) {
    		throw new DataSourceInitializationException(ex.getMessage());
    	}
    	catch (InstantiationException ex) {
    		throw new DataSourceInitializationException(ex.getMessage());
    	}
    	catch (ClassNotFoundException ex) {
    		throw new DataSourceNotFoundException(ex.getMessage());
    	}
    	
    	return ds;
    }
}
