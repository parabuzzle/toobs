package org.toobsframework.biz.scriptmanager;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
//import org.hibernate.Session;
//import org.hibernate.SessionFactory;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ImporterTopLevel;
import org.mozilla.javascript.JavaScriptException;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.WrappedException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.toobsframework.exception.PermissionException;
import org.toobsframework.exception.ValidationException;


@SuppressWarnings("unchecked")
public final class ScriptServiceImpl implements IScriptManager, BeanFactoryAware {

  private Log log = LogFactory.getLog(ScriptServiceImpl.class);

  private Map registry = new HashMap();

  private boolean doReload = true;

  private BeanFactory beanFactory;

  public ScriptServiceImpl() {
    super();
  }

  public Object runScript(String scriptName, Map params, Map outParams) throws ScriptException {
    Object result = null;
    // Setup script context and scope.
    Context ctx = Context.enter();
    Scriptable scope = new ImporterTopLevel(ctx);

    // Add Spring beanFactory for use in scripts.
    Object wrappedBeanFactory = Context.javaToJS(this.beanFactory, scope);
    ScriptableObject.putProperty(scope, "beanFactory", wrappedBeanFactory);
    Object wrappedLog = Context.javaToJS(this.log, scope);
    ScriptableObject.putProperty(scope, "log", wrappedLog);
    Object wrappedOutParams = Context.javaToJS(outParams, scope);
    ScriptableObject.putProperty(scope, "outParams", wrappedOutParams);

    // Add other parameters as passed in.
    if (null != params) {
      Iterator it = params.entrySet().iterator();
      while (it.hasNext()) {
        Entry thisEntry = (Entry) it.next();
        Object wrappedParam = Context.javaToJS(thisEntry.getValue(), scope);
        ScriptableObject.putProperty(scope, (String) thisEntry.getKey(),
            wrappedParam);
      }
    }

    // Get the script
    Script script = this.loadScript(scriptName, ctx);

    // Run script, return result.
    if (null != script) {
      result = script.exec(ctx, scope);
    } else {
      throw new ScriptException("Can't find script:" + scriptName);
    }

    return result;
  }

  public Object runScript(String action, String objectDao, String objectType, Map params, Map outParams)
      throws ScriptException, ValidationException, PermissionException {
    NativeJavaObject result = null;
    // Setup script context and scope.
    try {
      Context ctx = Context.enter();
      Scriptable scope = new ImporterTopLevel(ctx);
  
      // Add Spring beanFactory for use in scripts.
      Object wrappedBeanFactory = Context.javaToJS(this.beanFactory, scope);
      ScriptableObject.putProperty(scope, "beanFactory", wrappedBeanFactory);
      
      // Add log object to be used in scripts.
      Object wrappedLog = Context.javaToJS(this.log, scope);
      ScriptableObject.putProperty(scope, "log", wrappedLog);
  
      // Add action and objectType as parameters.
      Object wrappedAction = Context.javaToJS(action, scope);
      ScriptableObject.putProperty(scope, "action", wrappedAction);
      
      Object wrappedObjectDao = Context.javaToJS(objectDao, scope);
      ScriptableObject.putProperty(scope, "objectDao", wrappedObjectDao);
      
      if (beanFactory.containsBean(objectDao)) {
        Object wrappedObjectDaoImpl = Context.javaToJS(beanFactory.getBean(objectDao), scope);
        ScriptableObject.putProperty(scope, "objectDaoImpl", wrappedObjectDaoImpl);
      }

      if (objectType != null) {
        Object wrappedObjectType = Context.javaToJS(objectType, scope);
        ScriptableObject.putProperty(scope, "objectType", wrappedObjectType);
      }
      
      Object wrappedParams = Context.javaToJS(params, scope);
      ScriptableObject.putProperty(scope, "params", wrappedParams);
      
      Object wrappedOutParams = Context.javaToJS(outParams, scope);
      ScriptableObject.putProperty(scope, "outParams", wrappedOutParams);
  
      /* TODO Check there are no uses of SessionFactory in scripts 
      SessionFactory sessionFactory = (SessionFactory)beanFactory.getBean("sessionFactory");
      Session session = SessionFactoryUtils.getSession(sessionFactory, false);

      Object wrappedSession = Context.javaToJS(session, scope);
      ScriptableObject.putProperty(scope, "session", wrappedSession);
      */
      
      // Fetch the script with action_objecttype_bizScript.js as the pattern.
      // This includes using the more generic action_bizScript.js if an object
      // specific
      // override does not exist.
      String scriptName = action + "_" + objectType;
      Script script = this.loadScript(scriptName, ctx);
  
      if (null == script) {
        scriptName = action;
        script = this.loadScript(scriptName, ctx);
      }
      // Run script, return result.
      if (null != script) {
        try {
          result = (NativeJavaObject) script.exec(ctx, scope);
        } catch (JavaScriptException e) {
          Object eValue = ((JavaScriptException)e).getValue();
          if (eValue instanceof NativeJavaObject) {
            NativeJavaObject njo = (NativeJavaObject)((JavaScriptException)e).getValue();
            Throwable t = (Throwable)njo.unwrap();
            if (t instanceof ValidationException) {
              throw (ValidationException)t;
            } else {
              log.error("Wrapped Exception: " + t.getMessage(), t);
              throw e;
            }
          } else {
            throw e;
          }
        } catch (WrappedException e) {
          Throwable t = (Throwable)e.getWrappedException();
          if (t instanceof ValidationException) {
            throw (ValidationException)t;
          } else if (t instanceof PermissionException) {
            throw (PermissionException)t;
          } else {
            log.error("Wrapped Exception: " + t.getMessage(), t);
            throw e;
          }
        }
      } else {
        throw new ScriptException("Can't find script:" + action + "_" + objectType);
      }
    } finally {
      Context.exit();
    }
    if(result != null) {
      return result.unwrap();
    }
    
    return null;
  }

  private Script loadScript(String scriptName, Context ctx) throws ScriptException {
    Date initStart = new Date();;

    if (registry.containsKey(scriptName) && !doReload) {
      return (Script) registry.get(scriptName);
    }
    Script script = null;

    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    URL configFileURL = classLoader.getResource("script/" + scriptName + ".js");

    // If the file exists, read it.
    if (null != configFileURL) {
      try {
        InputStreamReader reader = new InputStreamReader(configFileURL.openStream());
        script = ctx.compileReader(reader, scriptName, 1, null);
      } catch (IOException e) {
        throw new ScriptException("Can't load script:" + scriptName);
      }
    }

    this.registry.put(scriptName, script);
    
    if (log.isDebugEnabled()) {
      Date initEnd = new Date();
      log.debug("Script [" + scriptName + "] - Compile Time: " + (initEnd.getTime() - initStart.getTime()));
    }
    return script;

  }

  public BeanFactory getBeanFactory() {
    return beanFactory;
  }

  public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
    this.beanFactory = beanFactory;
  }

  public boolean isDoReload() {
    return doReload;
  }

  public void setDoReload(boolean doReload) {
    this.doReload = doReload;
  }

}
