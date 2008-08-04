package org.toobs.framework.pres.client;

import org.toobs.framework.client.ClientConfiguration;
import org.toobs.framework.pres.component.IComponentManager;


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
