package org.toobsframework.pres.spring.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

@SuppressWarnings("unchecked")
public class ChartController extends AbstractController {

  private static Log log = LogFactory.getLog(ChartController.class);
  
  private IChartHandler chartHandler;
  
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
    return chartHandler.handleRequestInternal(request, response);
  }

  public IChartHandler getChartHandler() {
    return chartHandler;
  }

  public void setChartHandler(IChartHandler chartHandler) {
    this.chartHandler = chartHandler;
  }

}
