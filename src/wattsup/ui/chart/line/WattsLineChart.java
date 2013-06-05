package wattsup.ui.chart.line;

import java.util.List;

import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;

import wattsup.data.WattsUpPacket;
import wattsup.ui.ChartPanelSupport;
import wattsup.ui.LineChartPanelSupport;
import wattsup.ui.data.TranslatingXYDataset;

public class WattsLineChart extends LineChartPanelSupport<List<WattsUpPacket>>
{

    /**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
    private static final long serialVersionUID = -705459046389814891L;

    /**
     * @param data
     *            The data to be plotted.
     */
    public WattsLineChart(List<WattsUpPacket> data)
    {
        super("Watts", "", data, ChartPanelSupport.DEFAULT_DELAY);
        setRangeAxisRange(0, 180);
    }

    @Override
    protected void createSeries()
    {
        if (this.getSeries().size() < 1)
        {
            this.getTimeSeries().addSeries(new TimeSeries("Watts"));
            this.setDataset(new TranslatingXYDataset(this.getTimeSeries()));
        }
    }

    @Override
    public void update()
    {
        if (this.getSeries().size() < 1)
        {
            return;
        }
        
        if (getData() != null && !getData().isEmpty())
        {
            ((TimeSeries) this.getSeries().get(0)).add(new Millisecond(), Double.parseDouble(getData().get(0).getFields()[0].getValue()) / 10);
        }
    }
}
