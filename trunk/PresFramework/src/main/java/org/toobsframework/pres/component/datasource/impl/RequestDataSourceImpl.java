/*
 * Created by IntelliJ IDEA.
 * User: spudney
 * Date: Oct 6, 2008
 * Time: 3:07:53 PM
 */
package org.toobsframework.pres.component.datasource.impl;

import org.toobsframework.pres.component.datasource.api.*;
import org.toobsframework.pres.util.ComponentRequestManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;
import java.util.Collection;

public class RequestDataSourceImpl implements IDataSource {
  private Log log = LogFactory.getLog(RequestDataSourceImpl.class);
  private ComponentRequestManager componentRequestManager = null;

  public IDataSourceObject getObject(String objectType, String objectDao,
      String objectId, Map params, Map outParams) throws ObjectNotFoundException,
      DataSourceNotInitializedException {

    if(componentRequestManager == null || componentRequestManager.get() == null) {
      throw new DataSourceNotInitializedException("Component Request Manager or Component Request is null.");
    }

    DataSourceObjectImpl dsObj = new DataSourceObjectImpl();
    try {
      Object retObj = componentRequestManager.get().getParam(objectDao + "." + objectId);
      if(retObj instanceof DataSourceObjectImpl) {
        return (IDataSourceObject) retObj;
      }
      // Prepare Result
      dsObj.setValueObject(retObj);

    } catch (Exception e) {
      throw new ObjectNotFoundException("Error getting object:" + objectType + ":" + objectId, e);
    }
    return dsObj;
  }



  public Boolean deleteObject(String dao, String objectId, String permissionContext, String namespace, Map params, Map outParams) throws ObjectNotFoundException, DataSourceNotInitializedException {
    throw new ObjectNotFoundException("This Datasource does not support this action at this time.");
  }

  public IDataSourceObject updateObject(String objectType, String objectDao, String returnObjectType, String objectId, String permissionContext, String namespace, Map valueMap, Map outParams) throws ObjectNotFoundException, PropertyNotFoundException, TypeMismatchException, DataSourceNotInitializedException {
    throw new ObjectNotFoundException("This Datasource does not support this action at this time.");
  }

  public IDataSourceObject updateObjectCollection(String objectType, String objectDao, String returnObjectType, String objectId, String permissionContext, String namespace, String indexParam, Map valueMap, Map outParams) throws ObjectNotFoundException, PropertyNotFoundException, TypeMismatchException, DataSourceNotInitializedException {
    throw new ObjectNotFoundException("This Datasource does not support this action at this time.");
  }

  public IDataSourceObject createObject(String objectType, String objectDao, String returnObjectType, String permissionContext, String namespace, Map params, Map outParams) throws ObjectCreationException, DataSourceNotInitializedException, InvalidContextException {
    throw new ObjectCreationException("This Datasource does not support this action at this time.");
  }

  public IDataSourceObject createObjectCollection(String objectType, String objectDao, String returnObjectType, String permissionContext, String indexParam, String namespace, Map params, Map outParams) throws ObjectCreationException, DataSourceNotInitializedException, InvalidContextException {
    throw new ObjectCreationException("This Datasource does not support this action at this time.");
  }

  public Collection search(String returnValueObject, String dao, String searchCriteria, String searchMethod, String permissionAction, Map params, Map outParams) throws ObjectCreationException, InvalidSearchContextException, InvalidSearchFilterException, DataSourceNotInitializedException {
    throw new ObjectCreationException("This Datasource does not support this action at this time.");
  }

  public Collection searchIndex(String returnValueObject, String dao, String searchCriteria, String searchMethod, String permissionAction, Map params, Map outParams) throws ObjectCreationException, InvalidSearchContextException, InvalidSearchFilterException, DataSourceNotInitializedException {
    throw new ObjectCreationException("This Datasource does not support this action at this time.");
  }

  public Object dispatchAction(String action, String dao, String objectType, String returnObjectType, String guidParam, String permissionContext, String indexParam, String namespace, Map params, Map outParams) throws Exception {
    throw new ObjectNotFoundException("This Datasource does not support this action at this time.");
  }

  public ComponentRequestManager getComponentRequestManager() {
    return componentRequestManager;
  }

  public void setComponentRequestManager(
      ComponentRequestManager componentRequestManager) {
    this.componentRequestManager = componentRequestManager;
  }
  

}