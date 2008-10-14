package org.toobsframework.pres.app.controller;

import java.util.Map;

public interface IAppView {

  public abstract String getAppName();
  public abstract void setAppName(String appName);
  
  public abstract String getViewName();
  public abstract void setViewName(String viewName);
  
  public abstract boolean isComponentView();
  public abstract void setComponentView(boolean isComponentView);
  
  public abstract String getUrlParam(String name);
  public abstract void setUrlParam(String name, String value);
  public abstract void removeUrlParam(String name);
  public abstract Map<String,String> getUrlParams();

  public abstract void debugUrlParams();
}
