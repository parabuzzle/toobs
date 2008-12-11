package org.jfree.chart.plot;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.axis.CategoryAxis;

@SuppressWarnings("unchecked")
public class CombinedDomainCategoryPlotEx extends CombinedDomainCategoryPlot {

  public CombinedDomainCategoryPlotEx(CategoryAxis domainAxis) {
    super(domainAxis);
  }

  /**
   * Returns a collection of legend items for the plot.
   *
   * @return The legend items.
   */
  public LegendItemCollection getLegendItems() {
      LegendItemCollection result = getFixedLegendItems();
      if (result == null) {
          result = new LegendItemCollection();
          if (this.getSubplots() != null) {
              Map itemMap = new LinkedHashMap();
              Iterator iterator = this.getSubplots().iterator();
              while (iterator.hasNext()) {
                  CategoryPlot plot = (CategoryPlot) iterator.next();
                  LegendItemCollection more = plot.getLegendItems();
                  for (int i = 0; i < more.getItemCount(); i++) {
                    itemMap.put(more.get(i).getLabel(), more.get(i));
                  }
              }
              Iterator iter = itemMap.values().iterator();
              while (iter.hasNext()) {
                result.add((LegendItem)iter.next());
              }
          }
      }
      return result;
  }

}
