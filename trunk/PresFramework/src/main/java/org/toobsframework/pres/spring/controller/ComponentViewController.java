package org.toobsframework.pres.spring.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

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
public class ComponentViewController extends AbstractController {

  private static Log log = LogFactory.getLog(ComponentViewController.class);
  
  private IComponentViewHandler componentViewHandler;
  
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

    return componentViewHandler.handleRequestInternal(request, response);

  }

  public IComponentViewHandler getComponentViewHandler() {
    return componentViewHandler;
  }

  public void setComponentViewHandler(IComponentViewHandler componentViewHandler) {
    this.componentViewHandler = componentViewHandler;
  }

}
