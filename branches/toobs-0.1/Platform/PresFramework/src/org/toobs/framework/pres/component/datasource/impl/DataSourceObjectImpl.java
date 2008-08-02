package org.toobs.framework.pres.component.datasource.impl;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.toobs.data.IObjectLoader;
import org.toobs.framework.pres.component.datasource.api.IDataSourceObject;
import org.toobs.framework.pres.component.datasource.api.IDataSourceObjectProperty;
import org.toobs.framework.pres.component.datasource.api.InvalidContextException;
import org.toobs.framework.pres.component.datasource.api.LockException;
import org.toobs.framework.pres.component.datasource.api.NotAMappedPropertyException;
import org.toobs.framework.pres.component.datasource.api.NotAnIndexedPropertyException;
import org.toobs.framework.pres.component.datasource.api.ObjectSaveException;
import org.toobs.framework.pres.component.datasource.api.PropertyNotFoundException;
import org.toobs.framework.pres.component.datasource.api.TypeMismatchException;
import org.toobs.framework.pres.component.datasource.api.UnlockException;
import org.toobs.framework.util.BetwixtUtil;


public class DataSourceObjectImpl implements IDataSourceObject {

  private static Log log = LogFactory.getLog(DataSourceObjectImpl.class);

  private Object valueObject = null;

  private String dao = null;

  public Object getValueObject() {
    return this.valueObject;
  }

  public void setValueObject(Object vo) {
    this.valueObject = vo;
  }

  public String getId() {
    // Override in subclass
    return null;
  }

  public void lock() throws LockException {
    // Not needed since we're not implementing versioning
    log.info("lock() not implemented");
  }

  public void unlock() throws UnlockException {
    // Not needed since we're not implementing versioning
    log.info("unlock() not implemented");
  }

  public boolean isLocked() {
    // Not needed since we're not implementing versioning
    return false;
  }

  public IDataSourceObjectProperty getProperty(String propertyName)
      throws PropertyNotFoundException {

    DataSourcePropertyImpl dsProperty = null;

    try {
      PropertyDescriptor property = PropertyUtils.getPropertyDescriptor(this
          .getValueObject(), propertyName);
      dsProperty = new DataSourcePropertyImpl(property);
      dsProperty.setPropertyValue(property.getReadMethod().invoke(this,
          (Object[]) null));
    } catch (IllegalAccessException e) {
      throw new PropertyNotFoundException("Property Not found.", e);
    } catch (InvocationTargetException e) {
      throw new PropertyNotFoundException("Property Not found.", e);
    } catch (NoSuchMethodException e) {
      throw new PropertyNotFoundException("Property Not found.", e);
    }

    return dsProperty;
  }

  public IDataSourceObjectProperty[] getProperties() {
    // TODO Auto-generated method stub
    return null;
  }

  public IDataSourceObjectProperty[] getProperties(String[] propertyNames)
      throws PropertyNotFoundException {
    // TODO Auto-generated method stub
    return null;
  }

  public Object get(String propertyName) throws PropertyNotFoundException {
    // TODO Auto-generated method stub
    return null;
  }

  public Object get(String propertyName, int index)
      throws PropertyNotFoundException, NotAnIndexedPropertyException {
    // TODO Auto-generated method stub
    return null;
  }

  public Object get(String propertyName, String key)
      throws PropertyNotFoundException, NotAMappedPropertyException {
    // TODO Auto-generated method stub
    return null;
  }

  public void set(String propertyName, Object value)
      throws PropertyNotFoundException, TypeMismatchException {
    // TODO Auto-generated method stub

  }

  public void set(String propertyName, int index, Object value)
      throws PropertyNotFoundException, NotAnIndexedPropertyException,
      TypeMismatchException {
    // TODO Auto-generated method stub

  }

  public void set(String propertyName, String key, Object value)
      throws PropertyNotFoundException, NotAMappedPropertyException,
      TypeMismatchException {
    // TODO Auto-generated method stub

  }

  public IDataSourceObject[] getChildren() {
    // TODO Auto-generated method stub
    return null;
  }

  public IDataSourceObject getChild(int index) {
    // TODO Auto-generated method stub
    return null;
  }

  public Object callMethod(String methodName, Class[] parameterTypes,
      Object[] parameterValues) throws IllegalAccessException,
      IllegalArgumentException, InvocationTargetException {
    // TODO Auto-generated method stub
    return null;
  }

  public void update(Map valueMap) throws PropertyNotFoundException,
      TypeMismatchException {
    // TODO Auto-generated method stub

  }

  public void addChild(String context, IDataSourceObjectProperty[] properties)
      throws InvalidContextException {
    // TODO Auto-generated method stub

  }

  public String save() throws ObjectSaveException {
    // TODO Auto-generated method stub
    return null;
  }

  public String toXml() throws IOException {
    return BetwixtUtil.toXml(this.valueObject);
  }

  public String getDao() {
    return dao;
  }

  public void setDao(String dao) {
    this.dao = dao;
  }

  /**
   * returns the classname of the valueobject.
   * 
   * @return classname o the valueobject.
   */
  public String getValueObjectClassName() {
    return this.getValueObject().getClass().getSimpleName();
  }

  /**
   * returns the dao used to get the object..
   * 
   * @return value object Dao interface.
   */
  public String getValueObjectDao() {
    return this.getDao();
  }

}
