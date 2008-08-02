package org.toobs.framework.biz.scriptmanager;

import java.util.Map;
import org.springframework.beans.factory.BeanFactory;


/**
 * @author pudneyse
 *
 */
public interface IScriptManager {
  
  public void   setBeanFactory(BeanFactory beanFactory) throws Exception;
    
  public Object runScript(String action, String objectDao, String objectType, Map params, Map outParams) throws Exception;
  
  public Object runScript(String scriptName, Map params, Map outParams) throws Exception;
}
