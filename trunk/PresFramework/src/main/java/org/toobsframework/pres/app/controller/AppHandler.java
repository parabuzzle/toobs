package org.toobsframework.pres.app.controller;

import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.UrlPathHelper;
import org.toobsframework.pres.app.AppManager;
import org.toobsframework.pres.app.AppReader;
import org.toobsframework.pres.component.config.Parameter;
import org.toobsframework.doitref.IDoItRefQueue;
import org.toobsframework.exception.PermissionException;
import org.toobsframework.pres.component.ComponentException;
import org.toobsframework.pres.layout.ComponentLayoutNotFoundException;
import org.toobsframework.pres.layout.RuntimeLayout;
import org.toobsframework.pres.layout.manager.IComponentLayoutManager;
import org.toobsframework.pres.security.IComponentSecurity;
import org.toobsframework.pres.util.ComponentRequestManager;
import org.toobsframework.pres.util.ParameterUtil;
import org.toobsframework.pres.util.PresConstants;
import org.toobsframework.util.Configuration;


@SuppressWarnings("unchecked")
public class AppHandler implements IAppHandler {

  private static Log log = LogFactory.getLog(AppHandler.class);
  
  private UrlPathHelper urlPathHelper = new UrlPathHelper();
  
  private AppManager appManager = null;
  private ComponentRequestManager componentRequestManager = null;
  private IDoItRefQueue doItRefQueue = null;
  private IComponentSecurity layoutSecurity;
  private IURLResolver urlResolver; 
  
  /* (non-Javadoc)
   * @see org.toobsframework.pres.app.controller.IAppHandler#handleRequestInternal(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
   */
  public ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {

    String output = "";
    String urlPath = this.urlPathHelper.getLookupPathForRequest(request);

    IAppView appView = urlResolver.resolve( (AppReader)appManager, urlPath, request.getMethod() );
    
    if (log.isDebugEnabled()) {
      
      appManager.showApps();
      
      log.debug("AppView App   : " + appView.getAppName());
      log.debug("AppView isComp: " + appView.isComponentView());
      log.debug("AppView View  : " + appView.getViewName());
      appView.debugUrlParams();
    }
    
    Date startTime = null;
    if (log.isDebugEnabled()) {
      startTime = new Date();
    }

    Map params = ParameterUtil.buildParameterMap(request);
    componentRequestManager.set(request, response, params);

    output = appManager.renderView(appView, componentRequestManager.get());
    
    //Write out to the response.
    response.setContentType("text/html; charset=UTF-8");
    response.setHeader("Pragma",        "no-cache");                           // HTTP 1.0
    response.setHeader("Cache-Control", "no-cache, must-revalidate, private"); // HTTP 1.1
    PrintWriter writer = response.getWriter();
    writer.print(output);
    writer.flush();

    if (log.isDebugEnabled()) {
      Date endTime = new Date();
      log.debug("Time [" + appView.getAppName() + ":" + appView.getViewName() + "] - " + (endTime.getTime() - startTime.getTime()));
    }
    return null;

  }

  /* (non-Javadoc)
   * @see org.toobsframework.pres.app.controller.IAppHandler#getAppManager()
   */
  public AppManager getAppManager() {
    return appManager;
  }

  /* (non-Javadoc)
   * @see org.toobsframework.pres.app.controller.IAppHandler#setAppManager(org.toobsframework.pres.app.AppManager)
   */
  public void setAppManager(AppManager appManager) {
    this.appManager = appManager;
  }

  /* (non-Javadoc)
   * @see org.toobsframework.pres.app.controller.IAppHandler#getComponentRequestManager()
   */
  public ComponentRequestManager getComponentRequestManager() {
    return componentRequestManager;
  }

  /* (non-Javadoc)
   * @see org.toobsframework.pres.app.controller.IAppHandler#setComponentRequestManager(org.toobsframework.pres.util.ComponentRequestManager)
   */
  public void setComponentRequestManager(ComponentRequestManager componentRequestManager) {
    this.componentRequestManager = componentRequestManager;
  }

  /* (non-Javadoc)
   * @see org.toobsframework.pres.app.controller.IAppHandler#getDoItRefQueue()
   */
  public IDoItRefQueue getDoItRefQueue() {
    return doItRefQueue;
  }

  /* (non-Javadoc)
   * @see org.toobsframework.pres.app.controller.IAppHandler#setDoItRefQueue(org.toobsframework.doitref.IDoItRefQueue)
   */
  public void setDoItRefQueue(IDoItRefQueue doItRefQueue) {
    this.doItRefQueue = doItRefQueue;
  }

  /* (non-Javadoc)
   * @see org.toobsframework.pres.app.controller.IAppHandler#getLayoutSecurity()
   */
  public IComponentSecurity getLayoutSecurity() {
    return layoutSecurity;
  }

  /* (non-Javadoc)
   * @see org.toobsframework.pres.app.controller.IAppHandler#setLayoutSecurity(org.toobsframework.pres.security.IComponentSecurity)
   */
  public void setLayoutSecurity(IComponentSecurity layoutSecurity) {
    this.layoutSecurity = layoutSecurity;
  }

}
