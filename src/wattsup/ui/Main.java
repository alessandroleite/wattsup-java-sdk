/**
 * Copyright (c) 2012 GreenI2R
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package wattsup.ui;

import java.awt.Dimension;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import wattsup.data.WattsUpConfig;
import wattsup.data.WattsUpPacket;
import wattsup.event.WattsUpDataAvailableEvent;
import wattsup.listener.WattsUpDataAvailableListener;
import wattsup.meter.WattsUp;
import wattsup.ui.chart.line.WattsLineChart;

public class Main extends JFrame
{
    /**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
    private static final long serialVersionUID = -6442954520291080335L;

    /**
     * 
     */
    private final JTabbedPane tabbedPane_;

    /**
     * 
     */
    private final List<JPanel> panels_ = new ArrayList<JPanel>();

    /**
     * 
     */
    private final WattsUp meter_;

    /**
     * 
     */
    private final List<WattsUpPacket> data_ = new ArrayList<>();

    /**
     * 
     * @param config
     *            The configuration for the meter.
     * @throws IOException
     *             If the meter is not available.
     */
    public Main(WattsUpConfig config) throws IOException
    {
        meter_ = new WattsUp(config);
        this.tabbedPane_ = new JTabbedPane();
        this.addContent();
    }

    /**
     * 
     * @throws IOException
     *             If the meter is not available.
     */
    protected void addContent() throws IOException
    {

        meter_.registerListener(new WattsUpDataAvailableListener()
        {
            @Override
            public void processDataAvailable(final WattsUpDataAvailableEvent event)
            {
                data_.clear();
                data_.addAll(Arrays.asList(event.getValue()));
            }
        });

        this.addPanel(new WattsLineChart(data_));

        this.tabbedPane_.setTabPlacement(JTabbedPane.BOTTOM);
        getContentPane().add(this.tabbedPane_);

        for (JPanel panel : this.getPanels())
        {
            panel.setVisible(true);
            this.tabbedPane_.add(panel, panel.getClass().getSimpleName());
        }
        setPreferredSize(new Dimension(1024, 768));

        this.pack();
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);

        meter_.connect();

        setVisible(true);
    }

    /**
     * Add a panel to this {@link JFrame}.
     * 
     * @param panel
     *            The panel to be added. Might not be <code>null</code>.
     */
    public void addPanel(JPanel panel)
    {
        this.panels_.add(panel);
    }

    /**
     * @return A read-only {@link List} with the panels.
     */
    public List<JPanel> getPanels()
    {
        return Collections.unmodifiableList(panels_);
    }

    /**
     * Creates an {@link WattsUp} to monitor the power consumption.
     * 
     * @param args
     *            The reference to the arguments.
     * @throws IOException
     *             If the power meter is not connected.
     */
    public static void main(final String[] args) throws IOException
    {
        new Main(new WattsUpConfig().withPort(args[0]).scheduleDuration(3 * 60));
    }
}
