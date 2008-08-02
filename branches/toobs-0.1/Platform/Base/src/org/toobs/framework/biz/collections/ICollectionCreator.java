package org.toobs.framework.biz.collections;

import java.util.Collection;
import java.util.Map;

public interface ICollectionCreator {

  public abstract void addCollectionElements(Object value, Collection collection, Map parameters, String personId, String namespace) throws Exception;
}