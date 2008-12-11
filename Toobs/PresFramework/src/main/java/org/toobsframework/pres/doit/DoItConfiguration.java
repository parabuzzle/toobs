package org.toobsframework.pres.doit;

import org.toobsframework.client.ClientConfiguration;
import org.toobsframework.pres.doit.manager.IDoItManager;


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
