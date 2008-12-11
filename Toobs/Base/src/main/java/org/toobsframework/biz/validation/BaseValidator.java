package org.toobsframework.biz.validation;

import java.util.Collection;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.validation.Errors;
import org.toobsframework.util.BaseRequestManager;


public abstract class BaseValidator implements IValidator, BeanFactoryAware {

  protected BeanFactory beanFactory;
  protected BaseRequestManager requestManager;
  
  public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
    this.beanFactory = beanFactory;
  }

  public void setRequestManager(BaseRequestManager requestManager) {
    this.requestManager = requestManager;
  }

  public void prePopulate(Object obj, Map properties) {
  }

  public void audit(Object obj, Map properties) {
  }

  public void validateCollection(Collection collection, Errors e) {
  }

  public boolean doCreateCollectionMember(Object obj) {
    return true;
  }
}
