package org.toobsframework.servlet;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.context.WebApplicationContext;

public class ContextHelper implements ApplicationContextAware {

	private static WebApplicationContext webApplicationContext;

	public static WebApplicationContext getWebApplicationContext() {
		return webApplicationContext;
	}

	public void setWebApplicationContext(
			WebApplicationContext webApplicationContext) {
		this.webApplicationContext = webApplicationContext;
	}

	public void setApplicationContext(ApplicationContext appContext)
			throws BeansException {
		this.webApplicationContext = (WebApplicationContext) appContext;
	}

	public static Object getBean(String beanName) {
		return webApplicationContext.getBean(beanName);
	}

}
