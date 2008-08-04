package org.toobsframework.pres.doit.controller;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;
import org.springframework.web.servlet.view.AbstractUrlBasedView;
import org.springframework.web.servlet.view.InternalResourceView;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.UrlPathHelper;
import org.springframework.web.util.WebUtils;
import org.toobsframework.pres.doit.config.Action;
import org.toobsframework.pres.doit.config.DoIt;
import org.toobsframework.pres.doit.config.Forward;
import org.toobsframework.pres.doit.config.Forwards;
import org.toobsframework.pres.doit.manager.IDoItManager;
import org.toobsframework.exception.BaseException;
import org.toobsframework.exception.ParameterException;
import org.toobsframework.exception.PermissionException;
import org.toobsframework.exception.ValidationException;
import org.toobsframework.pres.doit.IDoItRunner;
import org.toobsframework.pres.util.ComponentRequestManager;
import org.toobsframework.pres.util.ParameterUtil;
import org.toobsframework.pres.util.PresConstants;


/**
 * Controller that transforms the virtual filename at the end of a URL into a
 * component request. It then renders that component and dumps the result into
 * the response.
 * 
 * <p>
 * Example: "/index" -> "index" Example: "/index.comp" -> "index"
 * 
 * @author pudney
 */
@SuppressWarnings("unchecked")
public class DoItHandler extends AbstractController implements IDoItHandler {

  private UrlPathHelper urlPathHelper = new UrlPathHelper();
  
  private IDoItRunner doItRunner;
  private IDoItManager doItManager;
  private ComponentRequestManager componentRequestManager = null;
  
  private Log log = LogFactory.getLog(DoItHandler.class);

  public DoItHandler() throws Exception {
  }

  private Forward getForward(DoIt doIt, String name) {
    if (doIt == null) return null;
    Forward forward = null;
    Forwards forwards = doIt.getForwards();
    if (forwards != null) {
      Forward[] allFwds = forwards.getForward();
      for (int f = 0; f<allFwds.length; f++) {
        if (allFwds[f].getName().equals(name)) {
          forward = allFwds[f];
          break;
        }
      }
    }
    return forward;
  }

  /**
   * 
   * Retrieves the URL path to use for lookup and delegates to
   * <code>getViewNameForUrlPath</code>.
   * 
   * @throws Exception
   *           Exception fetching or rendering component.
   * @see #getViewNameForUrlPath
   * 
   */
  public ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) {
    String forwardTo = null;
    HashMap responseParams = new HashMap();
    HashMap forwardParams = new HashMap();

    String urlPath = this.urlPathHelper.getLookupPathForRequest(request);
    String doItId = extractViewNameFromUrlPath(urlPath);
    if (logger.isDebugEnabled()) {
      logger.debug("Staring to do it with doit '" + doItId + "' for lookup path: " + urlPath);
    }
    DoIt doIt = null;
    AbstractUrlBasedView forwardView = null;
    Action thisAction = null;
    Forward forwardDef = null;
    Map params = null;
    try {
      // Get DoIt
      doIt = doItManager.getDoIt(doItId);
      if(doIt == null) {
        throw new BaseException("Can not find config for doit: " + doItId);
      }
      params = ParameterUtil.buildParameterMap(request);
      componentRequestManager.set(request, response, params);

      doItRunner.runDoIt(doIt, params, responseParams);

      Iterator iter = responseParams.keySet().iterator();
      while (iter.hasNext()) {
        Object key = iter.next();
        request.setAttribute((String)key, responseParams.get(key));      
      }
      
      // Everything ran successfully. Forward to forward, if there
      // is one defined.
      String forwardName = getForward("forwardName", params);
      if (forwardName == null) {
        forwardName = "success";
      }
      forwardDef = getForward(doIt, forwardName);
      if (forwardDef != null) {
        forwardTo = ParameterUtil.resoveForwardPath(forwardDef, request.getParameterMap(), urlPath);
        
        forwardView = new RedirectView(forwardTo, true);
      }
      //forwardParams.put("TToken", componentRequestManager.get().getParams().get(PlatformConstants.REQUEST_GUID));
    } catch (Exception e) {
      boolean validationError = false;
      String forwardName = null;
      if (e.getCause() instanceof ValidationException) {
        validationError = true;
        forwardParams.put(PresConstants.VALIDATION_ERROR_MESSAGES, responseParams.get(PresConstants.VALIDATION_ERROR_MESSAGES));
        forwardParams.put(PresConstants.VALIDATION_ERROR_OBJECTS, responseParams.get(PresConstants.VALIDATION_ERROR_OBJECTS));
        addErrorForwardParams(thisAction, forwardParams, forwardParams);
        response.setHeader("toobs.error.validation", "true");
      } else if (e.getCause() instanceof PermissionException) {
        PermissionException pe = (PermissionException)e.getCause();
        forwardName = pe.getReason() + "PermForward";
      } else if(e instanceof BaseException){
        request.setAttribute("userErrorMessages",((BaseException) e).getUserMessages());
      }
      request.setAttribute("org.toobs.exception", e);
      log.error("Error running Action:", e);
      if (forwardName == null) {
        forwardName = getForward("errorForwardName", params);
        if (forwardName == null) {
          forwardName = getForward("errorForwardName", responseParams);
          if (forwardName == null) {
            forwardName = "error";
          }
        }
      }
      forwardDef = getForward(doIt, forwardName);
      boolean forward = false;
      if (forwardDef != null) {
        forwardTo = ParameterUtil.resoveForwardPath(forwardDef, request.getParameterMap(), urlPath);
        forward = forwardDef.getForward();
      } else if (validationError) {
        forwardTo = (String)request.getSession().getAttribute(PresConstants.SESSION_LAST_VIEW);
      }
      if (forwardTo == null || forwardTo.length() == 0) {
        forwardTo = PresConstants.ERROR_FORWARD;
      }
      if (forward || validationError) {
        forwardView = new InternalResourceView(forwardTo);
      } else {
        forwardView = new InternalResourceView(forwardTo);
        //forwardView = new RedirectView(forwardTo, true);
      }
    } finally {
      this.componentRequestManager.unset();
    }
    
    params = ParameterUtil.buildParameterMap(request);
    if (forwardDef != null && forwardDef.getQueryParameterMapping() != null) {
      try {
        ParameterUtil.mapParameters("Forward:" + forwardDef.getUri(),forwardDef.getQueryParameterMapping().getParameter(), params, forwardParams, doIt.getName());
      } catch (ParameterException e) {
        log.error("Forward Parameter Mapping error " + e.getMessage(), e);
        forwardView = new RedirectView(PresConstants.ERROR_FORWARD, true);
      }
    }
    // Forward out.
    if (forwardView != null) {
      if (log.isDebugEnabled()) {
        log.debug("Forward To: " + (forwardDef != null ? forwardDef.getName() : "null") + " URI: " + (forwardView != null ? forwardView.getUrl() : "null"));
        Iterator iter = forwardParams.entrySet().iterator();
        while (iter.hasNext()) {
          Map.Entry entry = (Map.Entry)iter.next();
          log.debug("  Forward Param - name: " + entry.getKey() + " value: " + entry.getValue());
        }        
      }
      return new ModelAndView(forwardView, forwardParams);
    } else {
      return null;
    }

  }
  
  private void addErrorForwardParams(Action actionDef, Map params, Map forwardParams) {
    Map errorParams = (Map)params.get("ErrorForwardParams");
    if (errorParams != null) {
      String guid = (String)errorParams.get("guid");
      if (guid != null && actionDef != null) {
        forwardParams.put(
            ((String[])ParameterUtil.resolveParam(actionDef.getGuidParam(), params))[0], 
            guid);
      }
      Iterator iter = errorParams.keySet().iterator();
      while (iter.hasNext()) {
        Object key = iter.next();
        Object value = errorParams.get(key);
        forwardParams.put(key, value);
      }
    }
  }
  

  /**
   * Extract the URL filename from the given request URI. Delegates to
   * <code>WebUtils.extractViewNameFromUrlPath(String)</code>.
   * 
   * @param uri
   *          the request URI (e.g. "/index.html")
   * @return the extracted URI filename (e.g. "index")
   * @see org.springframework.web.util.WebUtils#extractFilenameFromUrlPath
   */
  protected String extractViewNameFromUrlPath(String uri) {
    return WebUtils.extractFilenameFromUrlPath(uri);
  }

  private String getForward(String name, Map params) {
    if (name == null || params == null) {
      return null;
    }
    Object forward = params.get(name);
    if (forward != null && forward.getClass().isArray()) {
      return ((String[])forward)[0];
    } else {
      return (String)forward;
    }
  }
  
  /* (non-Javadoc)
 * @see org.toobsframework.pres.doit.controller.IDoItHandler#getDoItRunner()
 */
public IDoItRunner getDoItRunner() {
    return doItRunner;
  }

  /* (non-Javadoc)
 * @see org.toobsframework.pres.doit.controller.IDoItHandler#setDoItRunner(org.toobsframework.pres.doit.IDoItRunner)
 */
public void setDoItRunner(IDoItRunner doItRunner) {
    this.doItRunner = doItRunner;
  }

  /* (non-Javadoc)
 * @see org.toobsframework.pres.doit.controller.IDoItHandler#getComponentRequestManager()
 */
public ComponentRequestManager getComponentRequestManager() {
    return componentRequestManager;
  }

  /* (non-Javadoc)
 * @see org.toobsframework.pres.doit.controller.IDoItHandler#setComponentRequestManager(org.toobsframework.pres.util.ComponentRequestManager)
 */
public void setComponentRequestManager(
      ComponentRequestManager componentRequestManager) {
    this.componentRequestManager = componentRequestManager;
  }

  /* (non-Javadoc)
 * @see org.toobsframework.pres.doit.controller.IDoItHandler#getDoItManager()
 */
public IDoItManager getDoItManager() {
    return doItManager;
  }

  /* (non-Javadoc)
 * @see org.toobsframework.pres.doit.controller.IDoItHandler#setDoItManager(org.toobsframework.pres.doit.manager.IDoItManager)
 */
public void setDoItManager(IDoItManager doItManager) {
    this.doItManager = doItManager;
  }

}
