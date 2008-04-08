package org.toobs.framework.pres.component.datasource.impl;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.toobs.data.beanutil.BeanMonkey;
import org.toobs.framework.pres.component.datasource.api.DataSourceInitializationException;
import org.toobs.framework.pres.component.datasource.api.DataSourceNotInitializedException;
import org.toobs.framework.pres.component.datasource.api.IDataSource;
import org.toobs.framework.pres.component.datasource.api.IDataSourceObject;
import org.toobs.framework.pres.component.datasource.api.InvalidContextException;
import org.toobs.framework.pres.component.datasource.api.ObjectCreationException;
import org.toobs.framework.pres.component.datasource.api.ObjectNotFoundException;
import org.toobs.framework.pres.component.datasource.api.PropertyNotFoundException;
import org.toobs.framework.pres.component.datasource.api.TypeMismatchException;
import org.toobs.framework.pres.util.ParameterUtil;
import org.toobs.framework.util.constants.PlatformConstants;


@SuppressWarnings("unchecked")
public class DataSourceReflectImpl implements  BeanFactoryAware { //IDataSource,

  private BeanFactory beanFactory;
  public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
    this.beanFactory = beanFactory;
  }

  private Log log = LogFactory.getLog(DataSourceReflectImpl.class);

  public String getId() {
    // TODO Auto-generated method stub
    return null;
  }

  public String getLabel() {
    // TODO Auto-generated method stub
    return null;
  }

  public void init(Map params) throws DataSourceInitializationException {
    log.info("entered init");
  }

  public IDataSourceObject getObject(String objectType, String dao,
      String objectId, Map params, Map outParams) throws ObjectNotFoundException,
      DataSourceNotInitializedException {
    DataSourceObjectImpl dsObj = new DataSourceObjectImpl();
    try {

      Object service = beanFactory.getBean(dao);
      Method method = BeanMonkey.findMethod(service.getClass(), "get" + objectType + "ById", String.class);
      
      Object retObj = method.invoke(service, new Object[] {objectId});

      // Prepare Result
      dsObj.setValueObject(retObj);

    } catch (Exception e) {
      throw new ObjectNotFoundException("Error getting object:" + objectType + 
          ":" + objectId, e);
    }
    return dsObj;
  }
  
  public IDataSourceObject createObject(Object valueObject) throws DataSourceNotInitializedException {
    if (valueObject == null) {
      return null;
    }
    DataSourceObjectImpl dsObj = new DataSourceObjectImpl();
    dsObj.setValueObject(valueObject);
    return dsObj;
  }
  
  public Collection search(String returnType, String objectDao,
      String searchCriteria, String searchMethod, String permissionAction, Map params, Map outParams) throws ObjectCreationException {
    ArrayList retObjects = new ArrayList();

    try {
      // Get Object
      Object criteria = Class.forName(searchCriteria).newInstance();
      
      Object service = beanFactory.getBean(objectDao);
      Method method = BeanMonkey.findMethod(service.getClass(), searchMethod, criteria.getClass());
      
      BeanMonkey.populate(criteria, params, false);
      
      List objects = (List)method.invoke(service, new Object[] {criteria});
      
      //Massage Objects.
      Iterator it = objects.iterator();
      while(it.hasNext()){
        DataSourceObjectImpl dsObj = new DataSourceObjectImpl();
        dsObj.setValueObject(it.next());
        retObjects.add(dsObj);
      }

    } catch (Exception e) {
      throw new ObjectCreationException("Error searching for objects.", e);
    }

    return retObjects;
  }

  public Collection searchIndex(String returnType, String objectDao,
      String searchCriteria, String searchMethod, String permissionAction, Map params, Map outParams) throws ObjectCreationException {
    ArrayList retObjects = new ArrayList();

    // Get Object
    params.put("dao", ParameterUtil.resolveParam(objectDao, params));
    params.put("searchMethod", searchMethod);
    params.put("searchCriteria", searchCriteria);
    params.put("permissionAction", permissionAction);
    params.put("returnType", returnType);
    if(params.get("personId") == null) {
      params.put("personId", PlatformConstants.PERSON_ID_NO_PERSON);
    }
    try {
      // Get Object
      ArrayList objects = null;// =  (ArrayList) this.getScriptManager().runScript("searchIndex", objectDao, params, outParams);
      if (objects != null) {
        //Massage Objects.
        Iterator it = objects.iterator();
        while(it.hasNext()){
          DataSourceObjectImpl dsObj = new DataSourceObjectImpl();
          dsObj.setValueObject(it.next());
          retObjects.add(dsObj);
        }
      }
    } catch (Exception e) {
      throw new ObjectCreationException("Error searching for objects.", e);
    }

    return retObjects;
  }

  public Boolean deleteObject(String objectDao, String objectId, String permissionContext, Map params, Map outParams)
      throws ObjectNotFoundException, DataSourceNotInitializedException {
    Boolean retObj;

    params.put("guid", objectId);
    params.put("objectDao", objectDao);
    params.put("permissionContext", permissionContext);
    if(params.get("personId") == null) {
      params.put("personId", PlatformConstants.PERSON_ID_NO_PERSON);
    }
    params.put("modifier", params.get("personId"));
    params.put("lastModified", new Date());

    try {
      retObj = null;// = (Boolean) this.getScriptManager().runScript("delete", objectDao, params, outParams);
    } catch (Exception e) {
      throw new ObjectNotFoundException("Error deleteing object(" + objectDao + ":" + objectId + ")",
          e);
    }
    return retObj;
  }

  public Boolean purgeObject(String objectDao, String objectId, String permissionContext, Map params, Map outParams)
      throws ObjectNotFoundException, DataSourceNotInitializedException {
    Boolean retObj;
    
    params.put("guid", objectId);
    params.put("objectDao", objectDao);
    params.put("permissionContext", permissionContext);
    if(params.get("personId") == null) {
      params.put("personId", PlatformConstants.PERSON_ID_NO_PERSON);
    }
    params.put("modifier", params.get("personId"));
    params.put("lastModified", new Date());
    
    try {
      retObj = null;// = (Boolean) this.getScriptManager().runScript("purge", objectDao, params, outParams);
    } catch (Exception e) {
      throw new ObjectNotFoundException("Error deleteing object(" + objectDao + ":" + objectId + ")",
          e);
    }
    return retObj;
  }

  public IDataSourceObject updateObject(String objectType, String objectDao,
      String returnObjectType, String objectId, String permissionContext, Map valueMap, Map outParams) throws ObjectNotFoundException,
      PropertyNotFoundException, TypeMismatchException,
      DataSourceNotInitializedException {
    DataSourceObjectImpl dsObj = new DataSourceObjectImpl();
    try {

      valueMap.put("guid", objectId);
      valueMap.put("objectDao", objectDao);
      valueMap.put("objectClass", objectType);
      valueMap.put("returnObjectType", returnObjectType);
      valueMap.put("permissionContext", permissionContext);
      if(valueMap.get("personId") == null) {
        valueMap.put("personId", PlatformConstants.PERSON_ID_NO_PERSON);
      }
      if(valueMap.get("modifier") == null) {
        valueMap.put("modifier", valueMap.get("personId"));
      }
      valueMap.put("lastModified", new Date());
      if (objectId == null || objectId.equals("")) {
        valueMap.put("createdOn", new Date());
        if(valueMap.get("creator") == null) {
          valueMap.put("creator", valueMap.get("personId"));
      
        }
      }


      Object retObj = null;// = this.getScriptManager().runScript("update", objectDao, valueMap, outParams);
      dsObj.setValueObject(retObj);
      
    } catch (Exception e) {
      throw new ObjectNotFoundException("Error updating object.", e);
    }
    return dsObj;
  }

  public IDataSourceObject updateObjectCollection(String objectType, String objectDao,
      String returnObjectType, String objectId, String permissionContext, String indexParam, Map valueMap, Map outParams) throws ObjectNotFoundException,
      PropertyNotFoundException, TypeMismatchException,
      DataSourceNotInitializedException {
    DataSourceObjectImpl dsObj = new DataSourceObjectImpl();
    try {

      valueMap.put("guid", objectId);
      valueMap.put("objectDao", objectDao);
      valueMap.put("objectClass", objectType);
      valueMap.put("returnObjectType", returnObjectType);
      valueMap.put("permissionContext", permissionContext);
      valueMap.put("indexParam", indexParam);
      if(valueMap.get("personId") == null) {
        valueMap.put("personId", PlatformConstants.PERSON_ID_NO_PERSON);
      }
      if(valueMap.get("modifier") == null) {
        valueMap.put("modifier", valueMap.get("personId"));
      }
      valueMap.put("lastModified", new Date());
      if (objectId == null || objectId.equals("")) {
        valueMap.put("createdOn", new Date());
        if(valueMap.get("creator") == null) {
          valueMap.put("creator", valueMap.get("personId"));
      
        }
      }


      Object retObj = null;// = this.getScriptManager().runScript("updateCollection", objectDao, valueMap, outParams);
      dsObj.setValueObject(retObj);
      
    } catch (Exception e) {
      throw new ObjectNotFoundException("Error updating object.", e);
    }
    return dsObj;
  }

  public IDataSourceObject createObject(String objectType, String objectDao,
      String returnObjectType, String permissionContext, Map params, Map outParams) throws ObjectCreationException,
      DataSourceNotInitializedException, InvalidContextException {
    DataSourceObjectImpl dsObj = new DataSourceObjectImpl();

    params.put("objectDao", objectDao);
    params.put("objectClass", objectType);
    params.put("returnObjectType", returnObjectType);
    params.put("permissionContext", permissionContext);
    params.put("createdOn", new Date());
    params.put("lastModified", new Date());
    params.put("lastAccessed", new Date());
    if (!objectType.endsWith("PersonImpl")) {
      if(params.get("personId") == null) {
        params.put("personId", PlatformConstants.PERSON_ID_NO_PERSON);
      }
      if(params.get("creator") == null) {
        params.put("creator", params.get("personId"));
      }
      if(params.get("modifier") == null) {
      params.put("modifier", params.get("personId"));
      }
    }
    try {
      // Get Object
      Object retObj = null;// = this.getScriptManager().runScript("create", objectDao, params, outParams);

      // Prepare Result
      dsObj.setValueObject(retObj);

    } catch (Exception e) {
      throw new ObjectCreationException("Error Creating object", e);
    }
    return dsObj;
  }
  
  public IDataSourceObject createObjectCollection(String objectType, String objectDao,
      String returnObjectType, String permissionContext, String indexParam, Map params, Map outParams) throws ObjectCreationException,
      DataSourceNotInitializedException, InvalidContextException {
    DataSourceObjectImpl dsObj = new DataSourceObjectImpl();

    params.put("objectDao", objectDao);
    params.put("objectClass", objectType);
    params.put("returnObjectType", returnObjectType);
    params.put("permissionContext", permissionContext);
    params.put("indexParam", indexParam);
    params.put("createdOn", new Date());
    params.put("lastModified", new Date());
    params.put("lastAccessed", new Date());
    if (!objectType.endsWith("PersonImpl")) {
      if(params.get("personId") == null) {
        params.put("personId", PlatformConstants.PERSON_ID_NO_PERSON);
      }
      params.put("creator", params.get("personId"));
      params.put("modifier", params.get("personId"));
    }
    try {
      // Get Object
      Object retObj = null;// = this.getScriptManager().runScript("createCollection", objectDao, params, outParams);

      // Prepare Result
      dsObj.setValueObject(retObj);

    } catch (Exception e) {
      throw new ObjectCreationException("Error Creating object", e);
    }
    return dsObj;
  }

  public void save(Writer writer) throws IOException {
    // TODO Auto-generated method stub
  }
  
  public Object callAction(String action, String objectDao, String objectId, String permissionContext, Map params, Map outParams) throws ObjectCreationException {
    Object retObj = null;
    params.put("guid", objectId);
    params.put("objectDao", objectDao);
    params.put("permissionContext", permissionContext);
    if(params.get("personId") == null) {
      params.put("personId", PlatformConstants.PERSON_ID_NO_PERSON);
    }

    // Get Object
    try {
      retObj = null;// = this.getScriptManager().runScript(action, objectDao, params, outParams);
    } catch (Exception e) {
      throw new ObjectCreationException("Error running action " + action + " on " + objectDao, e);
    }

    return retObj;
  }

  public Object dispatchAction(String action, String dao, String objectType, 
      String returnObjectType, String guidParam, String permissionContext, String indexParam, Map params, Map outParams) throws Exception {
    Object returnObj = null;
    //Get the guid. If there is one.
    String guid = null;
    if(params.get(guidParam) != null && params.get(guidParam).getClass().isArray()) {
      guid = ((String[]) params.get(guidParam))[0];
    } else {
      guid = (String) params.get(guidParam);
    }        
    //Run action.
    if (action.equalsIgnoreCase("update")) {
      returnObj = this.updateObject(objectType, dao, returnObjectType, guid, permissionContext, params, outParams);
    } else if (action.equalsIgnoreCase("updateCollection")) {
      returnObj = this.updateObjectCollection(objectType, dao, returnObjectType, guid, permissionContext, indexParam, params, outParams);
    } else if (action.equalsIgnoreCase("create")) {
      returnObj = this.createObject(objectType, dao, returnObjectType, permissionContext, params, outParams);
    } else if (action.equalsIgnoreCase("createCollection")) {
      returnObj = this.createObjectCollection(objectType, dao, returnObjectType, permissionContext, indexParam,  params, outParams);
    } else if (action.equalsIgnoreCase("delete")) {
      returnObj = this.deleteObject(dao, guid, permissionContext, params, outParams);
    } else if (action.equalsIgnoreCase("purge")) {
      returnObj = this.purgeObject(dao, guid, permissionContext, params, outParams);
    } else {
      returnObj = this.callAction(action, dao, guid, permissionContext, params, outParams);
    }

    return returnObj;
  }

}
