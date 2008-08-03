package org.toobsframework.pres.component;

import org.toobsframework.client.ClientConfiguration;
import org.toobsframework.pres.component.manager.IComponentManager;


public class ComponentConfiguration extends ClientConfiguration {
  
  private IComponentManager componentManager;
  
  public void init() {
    componentManager.addConfigFiles(configFiles);
  }

  public IComponentManager getComponentManager() {
    return componentManager;
  }

  public void setComponentManager(IComponentManager componentManager) {
    this.componentManager = componentManager;
  }
}
