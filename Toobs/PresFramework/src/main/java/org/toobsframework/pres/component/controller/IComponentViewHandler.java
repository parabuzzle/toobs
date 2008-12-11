package org.toobsframework.pres.component.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.toobsframework.pres.component.manager.IComponentManager;
import org.toobsframework.pres.util.ComponentRequestManager;


public interface IComponentViewHandler {

  public abstract IComponentManager getComponentManager();

  public abstract void setComponentManager(IComponentManager componentManager);

  public abstract ComponentRequestManager getComponentRequestManager();

  public abstract void setComponentRequestManager(ComponentRequestManager componentRequestManager);

  public abstract ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception;
}