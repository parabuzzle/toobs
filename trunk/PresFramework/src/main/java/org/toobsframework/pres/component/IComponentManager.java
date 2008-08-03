package org.toobsframework.pres.component;

import java.util.List;
import java.util.Map;

import org.toobsframework.exception.ParameterException;


public interface IComponentManager {

  public abstract org.toobsframework.pres.component.Component getComponent(String Id, long deployTime) throws ComponentNotFoundException,
      ComponentInitializationException;
  
  public abstract String renderComponent(
      org.toobsframework.pres.component.Component component,
      String contentType, Map params, Map paramsOut, boolean appendUrlScanner)
      throws ComponentNotInitializedException, ComponentException, ParameterException;
      
  public void init() throws ComponentInitializationException;
  
  public void addConfigFiles(List configFiles);
}