package org.toobs.framework.pres.client;

import org.toobs.framework.client.ClientConfiguration;
import org.toobs.framework.pres.doit.IDoItManager;


public class DoItConfiguration extends ClientConfiguration {
  
  private IDoItManager doItManager;
  
  public void init() {
    doItManager.addConfigFiles(configFiles);
  }

  public IDoItManager getDoItManager() {
    return doItManager;
  }

  public void setDoItManager(IDoItManager doItManager) {
    this.doItManager = doItManager;
  }
}
