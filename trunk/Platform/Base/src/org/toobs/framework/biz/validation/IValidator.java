package org.toobs.framework.biz.validation;

import java.util.Collection;
import java.util.Map;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public interface IValidator extends Validator {
  public Object getSafeBean(Object bean, Map properties);
  public void prePopulate(Object obj, Map properties);
  public void prepare(Object obj, Map properties);
  public void audit(Object obj, Map properties);
  public void validateCollection(Collection collection, Errors e);
  public boolean doCreateCollectionMember(Object obj);
}
