package org.toobsframework.pres.doit.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

@SuppressWarnings("unchecked")
public class DoItController extends AbstractController {

  private static Log log = LogFactory.getLog(DoItController.class);
  
  private IDoItHandler doItHandler;
  
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
    return doItHandler.handleRequestInternal(request, response);
  }

  public IDoItHandler getDoItHandler() {
    return doItHandler;
  }

  public void setDoItHandler(
      IDoItHandler doItHandler) {
    this.doItHandler = doItHandler;
  }

}
