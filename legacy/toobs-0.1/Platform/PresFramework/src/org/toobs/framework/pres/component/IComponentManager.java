package org.toobs.framework.pres.component;

import java.util.List;
import java.util.Map;

import org.toobs.framework.exception.ParameterException;


public interface IComponentManager {

  public abstract org.toobs.framework.pres.component.Component getComponent(String Id, long deployTime) throws ComponentNotFoundException,
      ComponentInitializationException;
  
  public abstract String renderComponent(
      org.toobs.framework.pres.component.Component component,
      String contentType, Map params, Map paramsOut, boolean appendUrlScanner)
      throws ComponentNotInitializedException, ComponentException, ParameterException;
      
  public void init() throws ComponentInitializationException;
  
  public void addConfigFiles(List configFiles);
}