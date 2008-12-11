package org.toobsframework.pres.app.controller;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.toobsframework.pres.app.AppManager;

public class BaseAppView implements IAppView {

  private static final Log log = LogFactory.getLog(AppManager.class);

  private String appName;
  private String viewName;
  private boolean isComponentView;
  private Map<String,String> urlParams;
  
  public BaseAppView() {
    this(null,null);
  }

  public BaseAppView(String appName, String viewName) {
    this(appName,viewName, false);
  }

  public BaseAppView(String appName, String viewName, boolean isComponentView) {
    this(appName, viewName, isComponentView, null);
  }

  public BaseAppView(String appName, String viewName, boolean isComponentView, Map<String, String> urlParams) {
    super();
    this.appName = appName;
    this.viewName = viewName;
    this.isComponentView = isComponentView;
    if (urlParams != null) {
      this.urlParams = urlParams;
    } else {
      urlParams = new HashMap<String,String>();
    }
  }

  public String getAppName() {
    return appName;
  }

  public String getUrlParam(String name) {
    return urlParams.get(name);
  }

  public Map<String, String> getUrlParams() {
    return new HashMap<String,String>(urlParams);
  }

  public String getViewName() {
    return viewName;
  }

  public boolean isComponentView() {
    return isComponentView;
  }

  public void removeUrlParam(String name) {
    urlParams.remove(name);
  }

  public void setAppName(String appName) {
    this.appName = appName;
  }

  public void setComponentView(boolean isComponentView) {
    this.isComponentView = isComponentView;
  }

  public void setUrlParam(String name, String value) {
    urlParams.put(name, value);
  }

  public void setViewName(String viewName) {
    this.viewName = viewName;
  }

  public void debugUrlParams() {
    if (log.isDebugEnabled()) {
      Iterator<Map.Entry<String, String>> iterator = urlParams.entrySet().iterator();
      while (iterator.hasNext()) {
        Map.Entry<String, String> entry = iterator.next();
        log.debug("Url Param name: [" + entry.getKey() + "] value: [" + entry.getValue() + "]");
      }
    }
  }

}
