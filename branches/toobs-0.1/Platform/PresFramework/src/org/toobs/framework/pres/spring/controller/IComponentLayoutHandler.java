package org.toobs.framework.pres.spring.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.toobs.framework.doitref.IDoItRefQueue;
import org.toobs.framework.pres.component.IComponentLayoutManager;
import org.toobs.framework.pres.util.ComponentRequestManager;


public interface IComponentLayoutHandler {

  public abstract IComponentLayoutManager getComponentLayoutManager();

  public abstract void setComponentLayoutManager(IComponentLayoutManager componentLayoutManager);

  public abstract ComponentRequestManager getComponentRequestManager();

  public abstract void setComponentRequestManager(ComponentRequestManager componentRequestManager);

  public abstract IDoItRefQueue getDoItRefQueue();

  public abstract void setDoItRefQueue(IDoItRefQueue doItRefQueue);
  
  public abstract ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception;

}