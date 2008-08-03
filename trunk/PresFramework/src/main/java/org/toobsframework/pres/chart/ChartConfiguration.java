package org.toobsframework.pres.chart;

import org.toobsframework.client.ClientConfiguration;
import org.toobsframework.pres.chart.manager.IChartManager;


public class ChartConfiguration extends ClientConfiguration {
  
  private IChartManager chartManager;
  
  public void init() {
    chartManager.addConfigFiles(configFiles);
  }

  public IChartManager getChartManager() {
    return chartManager;
  }

  public void setChartManager(IChartManager chartManager) {
    this.chartManager = chartManager;
  }
}
