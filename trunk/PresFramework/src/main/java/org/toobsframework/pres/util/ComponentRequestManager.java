package org.toobsframework.pres.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.toobsframework.pres.component.datasource.api.IDataSourceObject;
import org.toobsframework.util.BaseRequestManager;
import org.toobsframework.util.IRequest;


@SuppressWarnings("unchecked")
public class ComponentRequestManager extends BaseRequestManager {

  private static Log log = LogFactory.getLog(ComponentRequestManager.class);

  public void cacheObject(String oper, String type, String ident, Object obj) {
    String cacheKey = oper + "-" + type + "-" + ident;
    IRequest cr = get();
    if (cr != null && cr.getHttpRequest() != null) {
      cr.getHttpRequest().setAttribute(cacheKey, obj);
    }
  }
  
  public IDataSourceObject checkRequestCache(String oper, String type, String ident) {
    String cacheKey = oper + "-" + type + "-" + ident;
    //log.info("Looking for instance of " + type + " with guid " + ident + " in request cache");
    Object obj = checkRequestCache(cacheKey);
    if (obj instanceof IDataSourceObject) {
      if (log.isDebugEnabled()) {
        log.debug("Found instance of " + type + " with guid " + ident + " in request cache");
      }
      return (IDataSourceObject)obj;
    } else if (obj != null) {
      log.warn("Object cached with key " + cacheKey + " is not a valid datasource object [" + obj + "]");
    }
    return null;
  }

  public Object checkRequestCache(String key) {
    Object cachedObj = null;
    IRequest cr = get();
    if (cr != null && cr.getHttpRequest() != null) {
      cachedObj = cr.getHttpRequest().getAttribute(key);
    }
    return cachedObj;
  }
}
