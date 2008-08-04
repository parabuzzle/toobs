package org.toobsframework.data;

import org.toobsframework.exception.PermissionException;

public interface IObjectLoader {

  public Object load(java.lang.String guid) throws PermissionException;

  public Object load(int transform, java.lang.String guid) throws PermissionException;

}
