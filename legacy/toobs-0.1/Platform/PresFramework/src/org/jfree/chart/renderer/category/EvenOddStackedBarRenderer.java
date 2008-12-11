package org.jfree.chart.renderer.category;

import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.Rectangle2D;

import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.DataUtilities;
import org.jfree.data.category.CategoryDataset;
import org.jfree.ui.GradientPaintTransformer;
import org.jfree.ui.RectangleEdge;

/**
 * A stacked bar renderer for use with the 
 * {@link org.jfree.chart.plot.CategoryPlot} class.
 */
public class EvenOddStackedBarRenderer extends StackedBarRenderer {

    private boolean even = true;
    
    /**
     * Creates a new renderer.  By default, the renderer has no tool tip 
     * generator and no URL generator.  These defaults have been chosen to 
     * minimise the processing required to generate a default chart.  If you 
     * require tool tips or URLs, then you can easily add the required 
     * generators.
     */
    public EvenOddStackedBarRenderer(boolean even) {
        this(even, false);
    }
    
    /**
     * Creates a new renderer.
     * 
     * @param renderAsPercentages  a flag that controls whether the data values
     *                             are rendered as percentages.
     */
    public EvenOddStackedBarRenderer(boolean even, boolean renderAsPercentages) {
        super(renderAsPercentages);
        this.even = even;
    }

    /**
     * Draws a stacked bar for a specific item.
     *
     * @param g2  the graphics device.
     * @param state  the renderer state.
     * @param dataArea  the plot area.
     * @param plot  the plot.
     * @param domainAxis  the domain (category) axis.
     * @param rangeAxis  the range (value) axis.
     * @param dataset  the data.
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
        double total = 0.0;  // only needed if calculating percentages
        if (getRenderAsPercentages()) {
            total = DataUtilities.calculateColumnTotal(dataset, column);
            value = value / total;
        }
        
        PlotOrientation orientation = plot.getOrientation();
        double barW0;
        if (even) {
          barW0 = domainAxis.getCategoryMiddle(column, getColumnCount(), dataArea, plot.getDomainAxisEdge()) - state.getBarWidth() / 2.0;
        } else {
          barW0 = domainAxis.getCategoryMiddle(column, getColumnCount(), dataArea, plot.getDomainAxisEdge()) + (state.getBarWidth() * 0.05);
        }


        double positiveBase = getBase();
        double negativeBase = positiveBase;

        for (int i = 0; i < row; i++) {
            Number v = dataset.getValue(i, column);
            if (v != null) {
                double d = v.doubleValue();
                if (getRenderAsPercentages()) {
                    d = d / total;
                }
                if (d > 0) {
                    positiveBase = positiveBase + d;
                }
                else {
                    negativeBase = negativeBase + d;
                }
            }
        }

        double translatedBase;
        double translatedValue;
        RectangleEdge location = plot.getRangeAxisEdge();
        if (value >= 0.0) {
            translatedBase = rangeAxis.valueToJava2D(positiveBase, dataArea, 
                    location);
            translatedValue = rangeAxis.valueToJava2D(positiveBase + value, 
                    dataArea, location);
        }
        else {
            translatedBase = rangeAxis.valueToJava2D(negativeBase, dataArea, 
                    location);
            translatedValue = rangeAxis.valueToJava2D(negativeBase + value, 
                    dataArea, location);
        }
        double barL0 = Math.min(translatedBase, translatedValue);
        double barLength = Math.max(Math.abs(translatedValue - translatedBase),
                getMinimumBarLength());

        double barWidth = (state.getBarWidth() / 2) - (state.getBarWidth() * 0.05);
        Rectangle2D bar = null;
        if (orientation == PlotOrientation.HORIZONTAL) {
            bar = new Rectangle2D.Double(barL0, barW0, barLength, barWidth);
        }
        else {
            bar = new Rectangle2D.Double(barW0, barL0, barWidth, barLength);
        }
        if (pass == 0) {
            Paint itemPaint = getItemPaint(row, column);
            GradientPaintTransformer t = getGradientPaintTransformer();
            if (t != null && itemPaint instanceof GradientPaint) {
                itemPaint = t.transform((GradientPaint) itemPaint, bar);
            }
            g2.setPaint(itemPaint);
            g2.fill(bar);
            if (isDrawBarOutline() 
                    && barWidth > BAR_OUTLINE_WIDTH_THRESHOLD) {
                g2.setStroke(getItemOutlineStroke(row, column));
                g2.setPaint(getItemOutlinePaint(row, column));
                g2.draw(bar);
            }

            // add an item entity, if this information is being collected
            EntityCollection entities = state.getEntityCollection();
            if (entities != null) {
                addItemEntity(entities, dataset, row, column, bar);
            }
        }
        else if (pass == 1) {
            CategoryItemLabelGenerator generator = getItemLabelGenerator(row, 
                    column);
            if (generator != null && isItemLabelVisible(row, column)) {
                drawItemLabel(g2, dataset, row, column, plot, generator, bar, 
                        (value < 0.0));
            }
        }        
    }

}
