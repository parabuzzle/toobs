package org.toobsframework.pres.chart.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.imagemap.ImageMapUtilities;
import org.jfree.chart.imagemap.StandardURLTagFragmentGenerator;
import org.jfree.chart.imagemap.URLTagFragmentGenerator;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.UrlPathHelper;
import org.springframework.web.util.WebUtils;
import org.toobsframework.pres.chart.ChartBuilder;
import org.toobsframework.pres.chart.ChartDefinition;
import org.toobsframework.pres.chart.ChartException;
import org.toobsframework.pres.chart.ChartNotFoundException;
import org.toobsframework.pres.chart.manager.IChartManager;
import org.toobsframework.pres.util.ComponentRequestManager;
import org.toobsframework.pres.util.ParameterUtil;
import org.toobsframework.util.Configuration;

@SuppressWarnings("unchecked")
public class ChartHandler implements IChartHandler, BeanFactoryAware {

  private static Log log = LogFactory.getLog(ChartHandler.class);
  
  private UrlPathHelper urlPathHelper = new UrlPathHelper();
  
  private IChartManager chartManager = null;
  private ChartBuilder chartBuilder = null;
  private ComponentRequestManager componentRequestManager = null;
  private BeanFactory beanFactory;

  public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
    this.beanFactory = beanFactory;
  }

  
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

    String urlPath = this.urlPathHelper.getLookupPathForRequest(request);

    String chartId = extractViewNameFromUrlPath(urlPath);
    if (log.isDebugEnabled()) {
      log.debug("Rendering chart '" + chartId + "' for lookup path: " + urlPath);
    }
    
    Date startTime = null;
    if (log.isDebugEnabled()) {
      startTime = new Date();
    }

    JFreeChart chart = null;
    int height = 400;
    int width = 600;
    ChartDefinition chartDef = null;
    if(null != chartId && !chartId.equals("")) {
      try {
        request.setAttribute("chartId", chartId);
        chartDef = this.chartManager.getChartDefinition(chartId);
      } catch (ChartNotFoundException cnfe) {
        log.warn("Chart " + chartId + " not found.");
        throw cnfe;
      }
      try {
        Map params = ParameterUtil.buildParameterMap(request);
        componentRequestManager.set(request, response, params);

        chart = chartBuilder.build(chartDef, componentRequestManager.get());
        width = chartDef.getChartWidth();
        height = chartDef.getChartHeight();
        
      } catch (ChartException e) {
        Throwable t = e.rootCause();
        log.info("Root cause " + t.getClass().getName() + " " + t.getMessage());
        throw e;
      } catch (Exception e) {
        throw e;
      } finally {
        this.componentRequestManager.unset();
      }
      
    } else {
      throw new Exception ("No chartId specified");
    }

    //Write out to the response.
    response.setHeader("Pragma",        "no-cache");                   // HTTP 1.0
    response.setHeader("Cache-Control", "max-age=0, must-revalidate"); // HTTP 1.1
    ChartRenderingInfo chartRenderingInfo = new ChartRenderingInfo();
    if (chartDef.doImageWithMap()) {
      response.setContentType("text/html; charset=UTF-8");
      String genFileName = chartDef.getId() + "-" + new Date().getTime() + ".png";
      String imageOutputFileName = Configuration.getInstance().getUploadDir() + genFileName;
      File imageOutputFile = new File(imageOutputFileName);
      OutputStream os = null;
      try {
        os = new FileOutputStream(imageOutputFile);
        ChartUtilities.writeChartAsPNG(os, chart, width, height, chartRenderingInfo);
      } finally {
        if (os != null) {
          os.close();
        }
      }
      PrintWriter writer = response.getWriter();
      StringBuffer sb = new StringBuffer();

      sb.append("<img id=\"chart-").append(chartDef.getId()).append("\" src=\"").append(Configuration.getInstance().getMainContext() + "/upload/" + genFileName).append("\" ismap=\"ismap\" usemap=\"#").append(chartDef.getId()).append("Map\" />");
      URLTagFragmentGenerator urlGenerator;
      if (chartDef.getUrlFragmentBean() != null) {
        urlGenerator = (URLTagFragmentGenerator)beanFactory.getBean(chartDef.getUrlFragmentBean());
      } else {
        urlGenerator = new StandardURLTagFragmentGenerator();
      }
      sb.append(ImageMapUtilities.getImageMap(chartDef.getId() + "Map", chartRenderingInfo, null, urlGenerator));
      writer.print(sb.toString());
      writer.flush();
      
    } else {
      response.setContentType("image/png");

      ChartUtilities.writeChartAsPNG(response.getOutputStream(), chart, width, height, chartRenderingInfo);
      
    }

    
    if (log.isDebugEnabled()) {
      Date endTime = new Date();
      log.debug("Time [" + chartId + "] - " + (endTime.getTime() - startTime.getTime()));
    }
    return null;

  }

  /**
   * Extract the URL filename from the given request URI.
   * Delegates to <code>WebUtils.extractViewNameFromUrlPath(String)</code>.
   * @param uri the request URI (e.g. "/index.html")
   * @return the extracted URI filename (e.g. "index")
   * @see org.springframework.web.util.WebUtils#extractFilenameFromUrlPath
   */
  protected String extractViewNameFromUrlPath(String uri) {
    return WebUtils.extractFilenameFromUrlPath(uri);
  }

  public IChartManager getChartManager() {
    return chartManager;
  }

  public void setChartManager(
      IChartManager chartManager) {
    this.chartManager = chartManager;
  }

  public ComponentRequestManager getComponentRequestManager() {
    return componentRequestManager;
  }

  public void setComponentRequestManager(
      ComponentRequestManager componentRequestManager) {
    this.componentRequestManager = componentRequestManager;
  }

  public ChartBuilder getChartBuilder() {
    return chartBuilder;
  }

  public void setChartBuilder(ChartBuilder chartBuilder) {
    this.chartBuilder = chartBuilder;
  }

}
