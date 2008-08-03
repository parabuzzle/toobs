package org.toobsframework.pres.doit;

import java.util.List;

import org.toobsframework.pres.doit.config.DoIt;


public interface IDoItManager {

  public abstract DoIt getDoIt(String Id) throws Exception;

  public void addConfigFiles(List configFiles);
  
}