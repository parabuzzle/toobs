package org.toobs.framework.doitref;

import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.toobs.framework.doitref.beans.DoItRefBean;
import org.toobs.framework.jms.doitref.JmsDoItRefException;
import org.toobs.framework.util.BaseRequestManager;


public interface IDoItRefQueue {

  public abstract void setBeanFactory(BeanFactory beanFactory) throws BeansException;

  public abstract void put(String doItName, Map params) throws JmsDoItRefException;

  public abstract void runDoItRef(DoItRefBean bean) throws JmsDoItRefException;

  public abstract BaseRequestManager getRequestManager();

  public abstract void setRequestManager(BaseRequestManager requestManager);

}