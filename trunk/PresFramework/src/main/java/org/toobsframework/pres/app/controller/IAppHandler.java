package org.toobsframework.pres.app.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.toobsframework.doitref.IDoItRefQueue;
import org.toobsframework.pres.app.AppManager;
import org.toobsframework.pres.security.IComponentSecurity;
import org.toobsframework.pres.util.ComponentRequestManager;

public interface IAppHandler {

  public static String DEFAULT_VIEW = "[default]";
  /**
   * 
   * Retrieves the URL path to use for lookup and delegates to
   * <code>getViewNameForUrlPath</code>.
   * 
   * @throws Exception Exception fetching or rendering component.
   * @see #getViewNameForUrlPath
   * 
   */
  public abstract ModelAndView handleRequestInternal(
      HttpServletRequest request, HttpServletResponse response)
      throws Exception;

  public abstract AppManager getAppManager();

  public abstract void setAppManager(AppManager appManager);

  public abstract ComponentRequestManager getComponentRequestManager();

  public abstract void setComponentRequestManager(
      ComponentRequestManager componentRequestManager);

  public abstract IDoItRefQueue getDoItRefQueue();

  public abstract void setDoItRefQueue(IDoItRefQueue doItRefQueue);

  public abstract IComponentSecurity getLayoutSecurity();

  public abstract void setLayoutSecurity(IComponentSecurity layoutSecurity);

  public abstract String getCompPrefix();

  public abstract void setCompPrefix(String compPrefix);
  
}