package org.toobsframework.pres.doit;

import java.util.Map;

import org.toobsframework.pres.doit.config.DoIt;
import org.toobsframework.pres.component.datasource.api.IDataSource;


public interface IDoItRunner {

  public abstract void runDoIt(DoIt doIt, Map paramMap, Map responseMap) throws Exception;

  public abstract IDataSource getDatasource();

}