package org.toobsframework.pres.spring.controller;

import java.io.PrintWriter;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.util.UrlPathHelper;
import org.springframework.web.servlet.ModelAndView;
import org.toobsframework.pres.component.Component;
import org.toobsframework.pres.component.IComponentManager;
import org.toobsframework.pres.util.ComponentRequestManager;
import org.toobsframework.pres.util.ParameterUtil;
import org.toobsframework.pres.util.PresConstants;
import org.toobsframework.util.Configuration;


/**
* Controller that transforms the virtual filename at the end of a URL
* into a component request.  It then renders that component and dumps 
* the result into the response.
*
* <p>Example: "/index" -> "index"
* Example: "/index.comp" -> "index"
* 
* @author Sean
*/
@SuppressWarnings("unchecked")
public class ComponentViewHandler implements IComponentViewHandler {

  private static Log log = LogFactory.getLog(ComponentViewController.class);
  
  private UrlPathHelper urlPathHelper = new UrlPathHelper();
  
  private IComponentManager componentManager = null;
  private ComponentRequestManager componentRequestManager = null;
  
  /**
   * 
   * Retrieves the URL path to use for lookup and delegates to
   * <code>getViewNameForUrlPath</code>.
   * 
   * @throws Exception Exception fetching or rendering component.
   * @see #getViewNameForUrlPath
   * 
   */
  public ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
  
    String output = "";
    String urlPath = this.urlPathHelper.getLookupPathForRequest(request);
    String contextPath = ParameterUtil.extractContextPathFromUrlPath(urlPath);
    String componentId = ParameterUtil.extractViewNameFromUrlPath(urlPath);
    if (log.isDebugEnabled()) {
      log.debug("Rendering component '" + componentId + "' for lookup path: " + urlPath);
    }
    
    //Get settings out of request
    String contentType = (String) request.getParameter("pipelineContentType");
    if(contentType == null) {
      contentType = "xhtml";
    }
    Date startTime = null;
    if (log.isDebugEnabled()) {
      startTime = new Date();
    }
    //Get component and render it.
    if(null != componentId && !componentId.equals("")) {
      long deployTime;
      if (request.getAttribute(PresConstants.DEPLOY_TIME) == null) {
        deployTime = Configuration.getInstance().getDeployTime();
      } else {
        deployTime = Long.parseLong((String)request.getAttribute(PresConstants.DEPLOY_TIME));
      }
      Component component = this.componentManager.getComponent(componentId, deployTime);
      boolean isTilesRequest = true;
      if (request.getAttribute(PresConstants.TILES_REQUEST_LOCAL) == null) {
        log.info("Setting component level request");
        request.setAttribute("contextPath", contextPath + (contextPath.length() > 0 ? "/" : ""));
        request.setAttribute("appContext", contextPath);
        Map params = ParameterUtil.buildParameterMap(request, true);
        componentRequestManager.set(request, response, params);
        isTilesRequest = false;
      }
      try {
        output = this.componentManager.renderComponent(component, contentType, componentRequestManager.get().getParams(), componentRequestManager.get().getParams(), true);
      } catch (Exception e) {
        throw e;
      } finally {
        if (!isTilesRequest) {
          this.componentRequestManager.unset();
        }
      }
    } else {
      throw new Exception ("No componentId specified");
    }
  
    //Write out to the response.
    response.setContentType("text/html; charset=UTF-8");
    response.setHeader("Pragma",        "no-cache");                // HTTP 1.0
    response.setHeader("Cache-Control", "no-cache, must-revalidate, private");        // HTTP 1.1
    PrintWriter writer = response.getWriter();
    writer.print(output);
    writer.flush();
    
    if (log.isDebugEnabled()) {
      Date endTime = new Date();
      log.debug("Time [" + componentId + "] - " + (endTime.getTime() - startTime.getTime()));
    }
    return null;
  
  }
  
  public IComponentManager getComponentManager() {
    return componentManager;
  }
  
  public void setComponentManager(IComponentManager componentManager) {
    this.componentManager = componentManager;
  }
  
  public ComponentRequestManager getComponentRequestManager() {
    return componentRequestManager;
  }
  
  public void setComponentRequestManager(
      ComponentRequestManager componentRequestManager) {
    this.componentRequestManager = componentRequestManager;
  }
  
}
