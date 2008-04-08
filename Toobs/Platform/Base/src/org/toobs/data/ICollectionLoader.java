package org.toobs.data;

import org.toobs.framework.exception.PermissionException;

public interface ICollectionLoader {

  public Object load(java.lang.Integer id) throws PermissionException;

  public Object load(int transform, java.lang.Integer id) throws PermissionException;

}
