package org.toobsframework.pres.layout.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.toobsframework.doitref.IDoItRefQueue;
import org.toobsframework.pres.layout.manager.IComponentLayoutManager;
import org.toobsframework.pres.util.ComponentRequestManager;


public interface IComponentLayoutHandler {

  public abstract IComponentLayoutManager getComponentLayoutManager();

  public abstract void setComponentLayoutManager(IComponentLayoutManager componentLayoutManager);

  public abstract ComponentRequestManager getComponentRequestManager();

  public abstract void setComponentRequestManager(ComponentRequestManager componentRequestManager);

  public abstract IDoItRefQueue getDoItRefQueue();

  public abstract void setDoItRefQueue(IDoItRefQueue doItRefQueue);
  
  public abstract ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception;

}