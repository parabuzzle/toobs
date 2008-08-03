package org.toobsframework.pres.security;

import org.toobsframework.pres.util.ComponentRequestManager;

public interface IComponentSecurity {

  public abstract boolean hasAccess(String layoutId);

  public abstract ComponentRequestManager getRequestManager();

  public abstract void setRequestManager(ComponentRequestManager requestManager);

}