package org.toobsframework.pres.chart.manager;

import java.util.List;

import org.toobsframework.pres.chart.ChartDefinition;
import org.toobsframework.pres.chart.ChartInitializationException;
import org.toobsframework.pres.chart.ChartNotFoundException;

public interface IChartManager {

  public ChartDefinition getChartDefinition(String Id) throws ChartNotFoundException, ChartInitializationException;

  public void addConfigFiles(List configFiles);
}
