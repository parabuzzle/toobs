package org.jfree.chart.renderer.category;

import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;

import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.ui.GradientPaintTransformer;
import org.jfree.ui.RectangleEdge;

/**
 * A {@link CategoryItemRenderer} that draws individual data items as bars.
 */
public class EvenOddBarRenderer extends BarRenderer {

    private boolean even = true;
    
    /**
     * Creates a new bar renderer with default settings.
     */
    public EvenOddBarRenderer(boolean even) {
        super();
        this.even = even;
    }


    /**
     * Calculates the coordinate of the first "side" of a bar.  This will be 
     * the minimum x-coordinate for a vertical bar, and the minimum 
     * y-coordinate for a horizontal bar.
     *
     * @param plot  the plot.
     * @param orientation  the plot orientation.
     * @param dataArea  the data area.
     * @param domainAxis  the domain axis.
     * @param state  the renderer state (has the bar width precalculated).
     * @param row  the row index.
     * @param column  the column index.
     * 
     * @return The coordinate.
     */
    protected double calculateBarW0(CategoryPlot plot, 
                                    PlotOrientation orientation, 
                                    Rectangle2D dataArea,
                                    CategoryAxis domainAxis,
                                    CategoryItemRendererState state,
                                    int row,
                                    int column) {
        // calculate bar width...
        double space = 0.0;
        if (orientation == PlotOrientation.HORIZONTAL) {
            space = dataArea.getHeight();
        }
        else {
            space = dataArea.getWidth();
        }
        double barW0 = domainAxis.getCategoryStart(column, getColumnCount(), dataArea, plot.getDomainAxisEdge());
        int seriesCount = getRowCount();
        int categoryCount = getColumnCount();
        if (seriesCount > 1) {
            double seriesGap = space * getItemMargin() / (categoryCount * (seriesCount - 1));
            double seriesW = calculateSeriesWidth(space, domainAxis, categoryCount, seriesCount);
            if (even) {
              barW0 = barW0 + row * (seriesW + seriesGap) + (seriesW / 2.0) - (state.getBarWidth() / 2.0);
            } else {
              barW0 = barW0 + row * (seriesW + seriesGap) + (seriesW / 2.0) + (seriesGap / 2.0);
            }
        }
        else {
          if (even) {
            barW0 = domainAxis.getCategoryMiddle(column, getColumnCount(), dataArea, plot.getDomainAxisEdge()) - state.getBarWidth() / 2.0;
          } else {
            barW0 = domainAxis.getCategoryMiddle(column, getColumnCount(), dataArea, plot.getDomainAxisEdge()) + (state.getBarWidth() * 0.05);
          }
        }
        return barW0;
    }

    /**
     * Draws the bar for a single (series, category) data item.
     *
     * @param g2  the graphics device.
     * @param state  the renderer state.
     * @param dataArea  the data area.
     * @param plot  the plot.
     * @param domainAxis  the domain axis.
     * @param rangeAxis  the range axis.
     * @param dataset  the dataset.
     * @param row  the row index (zero-based).
     * @param column  the column index (zero-based).
     * @param pass  the pass index.
     */
    public void drawItem(Graphics2D g2,
                         CategoryItemRendererState state,
                         Rectangle2D dataArea,
                         CategoryPlot plot,
                         CategoryAxis domainAxis,
                         ValueAxis rangeAxis,
                         CategoryDataset dataset,
                         int row,
                         int column,
                         int pass) {

        // nothing is drawn for null values...
        Number dataValue = dataset.getValue(row, column);
        if (dataValue == null) {
            return;
        }
        
        double value = dataValue.doubleValue();
        
        PlotOrientation orientation = plot.getOrientation();
        double barW0 = calculateBarW0(plot, orientation, dataArea, domainAxis, 
                state, row, column);
        double[] barL0L1 = calculateBarL0L1(value);
        if (barL0L1 == null) {
            return;  // the bar is not visible
        }
        
        RectangleEdge edge = plot.getRangeAxisEdge();
        double transL0 = rangeAxis.valueToJava2D(barL0L1[0], dataArea, edge);
        double transL1 = rangeAxis.valueToJava2D(barL0L1[1], dataArea, edge);
        double barL0 = Math.min(transL0, transL1);
        double barLength = Math.max(Math.abs(transL1 - transL0), 
                getMinimumBarLength());

        // draw the bar...
        Rectangle2D bar = null;
        
        double barWidth = (state.getBarWidth() / 2) - (state.getBarWidth() * 0.05);
        
        if (orientation == PlotOrientation.HORIZONTAL) {
            bar = new Rectangle2D.Double(barL0, barW0, barLength, barWidth);
        }
        else {
            bar = new Rectangle2D.Double(barW0, barL0, barWidth, barLength);
        }
        Paint itemPaint = getItemPaint(row, column);
        GradientPaintTransformer t = getGradientPaintTransformer();
        if (t != null && itemPaint instanceof GradientPaint) {
            itemPaint = t.transform((GradientPaint) itemPaint, bar);
        }
        g2.setPaint(itemPaint);
        g2.fill(bar);

        // draw the outline...
        if (isDrawBarOutline() && barWidth > BAR_OUTLINE_WIDTH_THRESHOLD) {
            Stroke stroke = getItemOutlineStroke(row, column);
            Paint paint = getItemOutlinePaint(row, column);
            if (stroke != null && paint != null) {
                g2.setStroke(stroke);
                g2.setPaint(paint);
                g2.draw(bar);
            }
        }

        CategoryItemLabelGenerator generator 
            = getItemLabelGenerator(row, column);
        if (generator != null && isItemLabelVisible(row, column)) {
            drawItemLabel(g2, dataset, row, column, plot, generator, bar, 
                    (value < 0.0));
        }        

        // add an item entity, if this information is being collected
        EntityCollection entities = state.getEntityCollection();
        if (entities != null) {
            addItemEntity(entities, dataset, row, column, bar);
        }

    }

}
