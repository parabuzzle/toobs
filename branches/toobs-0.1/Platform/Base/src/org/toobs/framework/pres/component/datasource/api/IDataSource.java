package org.toobs.framework.pres.component.datasource.api;

import java.util.Collection;
import java.util.Map;
import java.io.Writer;
import java.io.IOException;

/**
 * @author stewari
 * 
 * Interface to a data source.
 */
public interface IDataSource {

  /**
   * get the unique identifier for this datasource
   * 
   * @return
   */
  public String getId();

  /**
   * get the label for this datasource
   * 
   * @return label
   */
  public String getLabel();

  /**
   * initialization method which initializes this datasource. should be called
   * before trying to access objects in this datasource
   * 
   * @param params -
   *          initialization parameters
   */
  public void init(Map params) throws DataSourceInitializationException;

  /**
   * create a new object using the specified value object
   * 
   * @param objectId
   * @param params
   * @return specified object
   */
  public IDataSourceObject createObject(Object valueObject) throws DataSourceNotInitializedException;

  /**
   * get the object identified by the specified Id whether the tree rooted at
   * this object is returned is implementation specific
   * 
   * @param objectId
   * @param params
   * @return specified object
   */
  public IDataSourceObject getObject(String returnObjectType, String dao,
      String objectId, Map params, Map outParams) throws ObjectNotFoundException,
      DataSourceNotInitializedException;

  /**
   * delete the specified object
   * 
   * @param objectId
   * @return true if something was removed, false otherwise
   * @throws ObjectNotFoundException
   * @throws DataSourceNotInitializedException
   */
  public Boolean deleteObject(String dao, String objectId, String permissionContext, String namespace, Map params, Map outParams)
      throws ObjectNotFoundException, DataSourceNotInitializedException;

  /**
   * Update the specified simple properties for the specified object.
   * 
   * @param objectId
   * @param values -
   *          property name/value pairs
   */
  public IDataSourceObject updateObject(String objectType, String objectDao,
      String returnObjectType, String objectId, String permissionContext, String namespace, Map valueMap, Map outParams)
      throws ObjectNotFoundException, PropertyNotFoundException,
      TypeMismatchException, DataSourceNotInitializedException;

  /**
   * Update the specified simple properties for the specified object.
   * 
   * @param objectId
   * @param values -
   *          property name/value pairs
   */
  public IDataSourceObject updateObjectCollection(String objectType, String objectDao,
      String returnObjectType, String objectId, String permissionContext, String namespace, String indexParam, Map valueMap, Map outParams)
      throws ObjectNotFoundException, PropertyNotFoundException,
      TypeMismatchException, DataSourceNotInitializedException;

  /**
   * Create an object using the specified context and properties
   * 
   * @param parentId
   * @param context
   * @param properties
   * @return
   * @throws ObjectCreationException
   * @throws DataSourceNotInitializedException
   * @throws InvalidContextException
   */
  public IDataSourceObject createObject(String objectType, String objectDao,
      String returnObjectType, String permissionContext, String namespace, Map params, Map outParams) throws ObjectCreationException,
      DataSourceNotInitializedException, InvalidContextException;

  /**
   * Create an object using the specified context and properties
   * 
   * @param parentId
   * @param context
   * @param properties
   * @return
   * @throws ObjectCreationException
   * @throws DataSourceNotInitializedException
   * @throws InvalidContextException
   */
  public IDataSourceObject createObjectCollection(String objectType, String objectDao,
      String returnObjectType, String permissionContext, String indexParam, String namespace, Map params, Map outParams) throws ObjectCreationException,
      DataSourceNotInitializedException, InvalidContextException;

  /**
   * @param objectType
   *          If null all objects are searched
   * @param filterExpr -
   *          filterExpr expression to use for search. filterExpr is based on
   *          RFC 2254
   * @param scope -
   *          defines the scope of the search
   * @return objects that match the search criteria
   */
  public Collection search(String returnValueObject, String dao,
      String searchCriteria, String searchMethod, String permissionAction, Map params, Map outParams)
      throws ObjectCreationException, InvalidSearchContextException,
      InvalidSearchFilterException, DataSourceNotInitializedException;

  public Collection searchIndex(String returnValueObject, String dao,
      String searchCriteria, String searchMethod, String permissionAction, Map params, Map outParams)
      throws ObjectCreationException, InvalidSearchContextException,
      InvalidSearchFilterException, DataSourceNotInitializedException;

  public Object dispatchAction(String action, String dao, String objectType, 
      String returnObjectType, String guidParam, String permissionContext, String indexParam, String namespace, Map params, Map outParams) throws Exception;

    /**
   * Save this datasource to the specified writer
   * 
   * @param writer
   */
  public void save(Writer writer) throws IOException;
}
