package org.toobs.framework.pres.sec;

import org.toobs.framework.pres.util.ComponentRequestManager;

public interface IComponentSecurity {

  public abstract boolean hasAccess(String layoutId);

  public abstract ComponentRequestManager getRequestManager();

  public abstract void setRequestManager(ComponentRequestManager requestManager);

}