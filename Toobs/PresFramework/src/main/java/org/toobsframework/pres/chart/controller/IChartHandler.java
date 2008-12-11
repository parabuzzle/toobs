package org.toobsframework.pres.chart.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.toobsframework.pres.chart.manager.IChartManager;
import org.toobsframework.pres.util.ComponentRequestManager;


public interface IChartHandler {

  public abstract IChartManager getChartManager();

  public abstract void setChartManager(IChartManager chartManager);

  public abstract ComponentRequestManager getComponentRequestManager();

  public abstract void setComponentRequestManager(ComponentRequestManager componentRequestManager);

  public abstract ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception;
}