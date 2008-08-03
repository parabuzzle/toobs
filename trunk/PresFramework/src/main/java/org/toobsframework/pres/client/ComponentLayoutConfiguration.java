package org.toobsframework.pres.client;

import org.toobsframework.client.ClientConfiguration;
import org.toobsframework.pres.component.IComponentLayoutManager;


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
