package org.toobsframework.pres.app.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

@SuppressWarnings("unchecked")
public class AppController extends AbstractController {

  private static Log log = LogFactory.getLog(AppController.class);
  
  private IAppHandler appHandler;
  
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
    return appHandler.handleRequestInternal(request, response);
  }

  public IAppHandler getAppHandler() {
    return appHandler;
  }

  public void setAppHandler(IAppHandler appHandler) {
    this.appHandler = appHandler;
  }

}
