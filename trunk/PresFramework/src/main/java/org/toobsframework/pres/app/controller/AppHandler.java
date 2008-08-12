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
  
  private String compPrefix = "comp";
  
  /* (non-Javadoc)
   * @see org.toobsframework.pres.app.controller.IAppHandler#handleRequestInternal(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
   */
  public ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {

    String output = "";
    String urlPath = this.urlPathHelper.getLookupPathForRequest(request);

    AppView appView = getAppView(urlPath);
    
    if (log.isDebugEnabled()) {
      
      appManager.showApps();
      
      log.debug("AppView App   : " + appView.appName);
      log.debug("AppView isComp: " + appView.isComp);
      log.debug("AppView Param : " + appView.paramName);
      log.debug("AppView Value : " + appView.paramValue);
      log.debug("AppView View  : " + appView.viewName);
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
      log.debug("Time [" + appView.appName + ":" + appView.viewName + "] - " + (endTime.getTime() - startTime.getTime()));
    }
    return null;

  }

  private AppView getAppView(String urlPath) {
    String[] splitUrl = urlPath.split("/");
    if (log.isDebugEnabled()) {
      for (int i = 0; i < splitUrl.length; i++) {
        log.debug("Url part " + i + ": " + splitUrl[i]);
      }
    }
    if (splitUrl.length <= 1) {
      return new AppView("/", false, null, null, DEFAULT_VIEW);
    }
    if (splitUrl[1].equals(compPrefix)) {
      if (splitUrl.length >= 5) {
        return new AppView("/", true, splitUrl[2], splitUrl[3], splitUrl[4]);
      } else if (splitUrl.length == 4) {
        return new AppView("/", true, splitUrl[2], splitUrl[3], splitUrl[2]);
      } else {
        return new AppView("/", true, null, null, splitUrl[2]);
      }
    }
    if (appManager.containsApp("/" + splitUrl[1])) {
      if (splitUrl[1].equals(compPrefix)) {
        if (splitUrl.length >= 6) {
          return new AppView("/" + splitUrl[1], true, splitUrl[3], splitUrl[4], splitUrl[5]);
        } else if (splitUrl.length == 4) {
          return new AppView("/" + splitUrl[1], true, splitUrl[3], splitUrl[4], splitUrl[3]);
        } else {
          return new AppView("/" + splitUrl[1], true, null, null, splitUrl[3]);
        }
      } else {
        if (splitUrl.length >= 5) {
          return new AppView("/" + splitUrl[1], false, splitUrl[2], splitUrl[3], splitUrl[4]);
        } else if (splitUrl.length == 4) {
          return new AppView("/" + splitUrl[1], false, splitUrl[2], splitUrl[3], splitUrl[2]);
        } else if (splitUrl.length == 3) {
          return new AppView("/" + splitUrl[1], false, null, null, splitUrl[2]);
        } else {
          return new AppView("/" + splitUrl[1], false, null, null, DEFAULT_VIEW);
        }
      }
    } else {
      if (splitUrl.length >= 4) {
        return new AppView("/", true, splitUrl[1], splitUrl[2], splitUrl[3]);
      } else if (splitUrl.length == 3) {
        return new AppView("/", true, splitUrl[1], splitUrl[2], splitUrl[1]);
      } else {
        return new AppView("/", true, null, null, splitUrl[1]);
      }
    }
      
  }

  public class AppView {
    public AppView() {
    }
    public AppView(String appName, boolean isComp, String paramName,
        String paramValue, String viewName) {
      super();
      this.appName = appName;
      this.isComp = isComp;
      this.paramName = paramName;
      this.paramValue = paramValue;
      this.viewName = viewName;
    }
    public String appName;
    public String paramName;
    public String paramValue;
    public String viewName;
    public boolean isComp;
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

  public String getCompPrefix() {
    return compPrefix;
  }

  public void setCompPrefix(String compPrefix) {
    this.compPrefix = compPrefix;
  }

}
