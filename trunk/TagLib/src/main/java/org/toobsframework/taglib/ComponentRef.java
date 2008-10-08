/*
 * Created by IntelliJ IDEA.
 * User: spudney
 * Date: Sep 26, 2008
 * Time: 11:15:02 AM
 */
package org.toobsframework.taglib;

import org.toobsframework.pres.util.ParameterUtil;
import org.toobsframework.pres.util.ComponentRequestManager;
import org.toobsframework.pres.util.PresConstants;
import org.toobsframework.pres.component.manager.IComponentManager;
import org.toobsframework.pres.component.config.Component;
import org.toobsframework.pres.component.ComponentException;
import org.toobsframework.pres.component.ComponentNotFoundException;
import org.toobsframework.pres.component.ComponentInitializationException;
import org.toobsframework.pres.component.ComponentNotInitializedException;
import org.toobsframework.pres.layout.manager.IComponentLayoutManager;
import org.toobsframework.pres.layout.ComponentLayoutNotFoundException;
import org.toobsframework.pres.layout.ComponentLayoutInitializationException;
import org.toobsframework.pres.chart.manager.IChartManager;
import org.toobsframework.pres.chart.ChartBuilder;
import org.toobsframework.pres.xsl.ComponentHelper;
import org.toobsframework.util.Configuration;
import org.toobsframework.servlet.ContextHelper;
import org.toobsframework.exception.ParameterException;
import org.toobsframework.transformpipeline.domain.XMLTransformerException;
import org.springframework.beans.factory.BeanFactory;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.HashMap;

public class ComponentRef extends SimpleTagSupport {

  private static BeanFactory beanFactory;

  private static ComponentRequestManager reqManager;
  private static IComponentManager compManager;
  private static boolean debugComponents;
  private static String layoutExtension;
  private static String componentExtension;
  private static String chartExtension;

  static {
    beanFactory = ContextHelper.getWebApplicationContext();
    reqManager = (ComponentRequestManager)beanFactory.getBean("componentRequestManager");
    compManager = (IComponentManager)beanFactory.getBean("IComponentManager");
    debugComponents = Configuration.getInstance().getDebugComponents();
    layoutExtension = Configuration.getInstance().getLayoutExtension();
    componentExtension = Configuration.getInstance().getComponentExtension();
    chartExtension = Configuration.getInstance().getChartExtension();
  }

  private String componentId;
  private String contentType = null;
  private Map    parameterMap = new HashMap();
  private String dataObjectName;
  private Object dataObject;

  public void setParameterMap(Map parameterMap) {
    this.parameterMap = parameterMap;
  }

  public void setdataObject(Object dataObject) {
    this.dataObject = dataObject;
  }

  public void setdataObjectName(String dataObjectName) {
    this.dataObjectName = dataObjectName;
  }

  public void setComponentId(String componentId) {
    this.componentId = componentId;
  }
  public void setcontentType(String contentType) {
    this.contentType = contentType;
  }

  public void doTag() throws JspException, IOException {

    //Setup deploytime
    long deployTime;
    if (parameterMap.get(PresConstants.DEPLOY_TIME) == null) {
      deployTime = Configuration.getInstance().getDeployTime();
    } else {
      deployTime = Long.parseLong((String)parameterMap.get(PresConstants.DEPLOY_TIME));
    }

    //Setup Component Request
    reqManager.set(null, null, parameterMap);

    if(dataObject != null && dataObjectName != null) {
      reqManager.get().putParam(dataObjectName, dataObject);
    }

    //Find component
    org.toobsframework.pres.component.Component component = null;
    try {
      component = compManager.getComponent(componentId, deployTime);
    } catch (ComponentNotFoundException e) {
      throw new JspException("Could not find component with Id:" + componentId, e);
    } catch (ComponentInitializationException e) {
      throw new JspException("Could not initialize component with Id:" + componentId, e);
    }

    //Render Component
    String output = "";
    try {
      output = this.compManager.renderComponent(component, contentType, reqManager.get().getParams(), reqManager.get().getParams(), true);
    } catch (ComponentNotInitializedException e) {
      throw new JspException("Component with Id:" + componentId +": is not intitialized.", e);
    } catch (ComponentException e) {
      throw new JspException("Could not render component with Id:" + componentId, e);
    } catch (ParameterException e) {
      throw new JspException("Could not resolve parameters for component with Id:" + componentId, e);
    } finally {
      this.reqManager.unset();
    }
    
    //Now output results
    JspContext context = getJspContext();
    Writer writer = context.getOut();
    writer.write(output);
    return;
  }

}