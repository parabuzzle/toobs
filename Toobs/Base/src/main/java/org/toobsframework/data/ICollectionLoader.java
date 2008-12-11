package org.toobsframework.data;

import org.toobsframework.exception.PermissionException;

public interface ICollectionLoader {

  public Object load(java.lang.Integer id) throws PermissionException;

  public Object load(int transform, java.lang.Integer id) throws PermissionException;

}
