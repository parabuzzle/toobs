package org.toobsframework.doitref;

import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.toobsframework.doitref.beans.DoItRefBean;
import org.toobsframework.jms.doitref.JmsDoItRefException;
import org.toobsframework.util.BaseRequestManager;


public interface IDoItRefQueue {

  public abstract void setBeanFactory(BeanFactory beanFactory) throws BeansException;

  public abstract void put(String doItName, Map params) throws JmsDoItRefException;

  public abstract void runDoItRef(DoItRefBean bean) throws JmsDoItRefException;

  public abstract BaseRequestManager getRequestManager();

  public abstract void setRequestManager(BaseRequestManager requestManager);

}