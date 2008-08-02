package org.toobs.framework.pres.spring.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

@SuppressWarnings("unchecked")
public class ComponentLayoutController extends AbstractController {

  private static Log log = LogFactory.getLog(ComponentLayoutController.class);
  
  private IComponentLayoutHandler componentLayoutHandler;
  
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
    return componentLayoutHandler.handleRequestInternal(request, response);
  }

  public IComponentLayoutHandler getComponentLayoutHandler() {
    return componentLayoutHandler;
  }

  public void setComponentLayoutHandler(
      IComponentLayoutHandler componentLayoutHandler) {
    this.componentLayoutHandler = componentLayoutHandler;
  }

}
