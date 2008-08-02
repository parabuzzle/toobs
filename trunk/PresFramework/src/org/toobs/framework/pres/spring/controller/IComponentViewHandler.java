package org.toobs.framework.pres.spring.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.toobs.framework.pres.component.IComponentManager;
import org.toobs.framework.pres.util.ComponentRequestManager;


public interface IComponentViewHandler {

  public abstract IComponentManager getComponentManager();

  public abstract void setComponentManager(IComponentManager componentManager);

  public abstract ComponentRequestManager getComponentRequestManager();

  public abstract void setComponentRequestManager(ComponentRequestManager componentRequestManager);

  public abstract ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception;
}