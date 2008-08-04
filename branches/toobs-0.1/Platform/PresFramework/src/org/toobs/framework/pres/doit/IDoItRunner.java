package org.toobs.framework.pres.doit;

import java.util.Map;

import org.toobs.framework.pres.component.datasource.api.IDataSource;
import org.toobs.framework.pres.doit.config.DoIt;


public interface IDoItRunner {

  public abstract void runDoIt(DoIt doIt, Map paramMap, Map responseMap) throws Exception;

  public abstract IDataSource getDatasource();

}