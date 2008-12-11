package org.toobsframework.pres.doit.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.toobsframework.pres.doit.IDoItRunner;
import org.toobsframework.pres.doit.manager.IDoItManager;
import org.toobsframework.pres.util.ComponentRequestManager;

public interface IDoItHandler {

	public abstract ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response);
	
	public abstract IDoItRunner getDoItRunner();

	public abstract void setDoItRunner(IDoItRunner doItRunner);

	public abstract ComponentRequestManager getComponentRequestManager();

	public abstract void setComponentRequestManager(ComponentRequestManager componentRequestManager);

	public abstract IDoItManager getDoItManager();

	public abstract void setDoItManager(IDoItManager doItManager);

}