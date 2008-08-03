package org.toobsframework.pres.chart;

import java.util.List;

public interface IChartManager {

  public ChartDefinition getChartDefinition(String Id) throws ChartNotFoundException, ChartInitializationException;

  public void addConfigFiles(List configFiles);
}
