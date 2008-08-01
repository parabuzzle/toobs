package org.toobs.framework.pres.client;

import org.toobs.framework.client.ClientConfiguration;
import org.toobs.framework.pres.component.IComponentLayoutManager;


public class ComponentLayoutConfiguration extends ClientConfiguration {
  
  private IComponentLayoutManager componentLayoutManager;
  
  public void init() {
    componentLayoutManager.addConfigFiles(configFiles);
  }

  public IComponentLayoutManager getComponentLayoutManager() {
    return componentLayoutManager;
  }

  public void setComponentLayoutManager(IComponentLayoutManager componentLayoutManager) {
    this.componentLayoutManager = componentLayoutManager;
  }
}
