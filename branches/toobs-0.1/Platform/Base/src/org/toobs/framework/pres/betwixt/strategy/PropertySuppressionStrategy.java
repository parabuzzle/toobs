/**
 * 
 */
package org.toobs.framework.pres.betwixt.strategy;

import java.util.Collection;

/**
 * @author pudney
 * 
 */
public class PropertySuppressionStrategy extends
    org.apache.commons.betwixt.strategy.PropertySuppressionStrategy {

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.commons.betwixt.strategy.PropertySuppressionStrategy#suppressProperty(java.lang.Class,
   *      java.lang.Class, java.lang.String)
   */
  @Override
  public boolean suppressProperty(Class clazz, Class propertyType,
      String propertyName) {
    boolean result = false;
    // ignore class properties
    if (Class.class.equals(propertyType) && "class".equals(propertyName)) {
      result = true;
    }
    // ignore isEmpty for collection subclasses
    if ("empty".equals(propertyName)
        && Collection.class.isAssignableFrom(clazz)) {
      result = true;
    }

    return result;
  }

}
