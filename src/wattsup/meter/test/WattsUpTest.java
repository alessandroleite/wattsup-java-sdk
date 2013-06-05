package wattsup.meter.test;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import wattsup.data.WattsUpConfig;
import wattsup.data.WattsUpPacket;
import wattsup.event.WattsUpDataAvailableEvent;
import wattsup.listener.WattsUpDataAvailableListener;
import wattsup.meter.WattsUp;

public final class WattsUpTest
{
    /**
     * Private constructor to avoid instance of this class.
     */
    private WattsUpTest()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Creates an {@link WattsUp} for monitoring during one minute.
     * 
     * @param args
     *            The reference to the arguments.
     * @throws IOException
     *             If the power meter is not connected.
     */
    public static void main(String[] args) throws IOException
    {
        final SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        final WattsUp meter = new WattsUp(new WattsUpConfig().withPort(args[0]).scheduleDuration(60));

        meter.registerListener(new WattsUpDataAvailableListener()
        {
            @Override
            public void processDataAvailable(final WattsUpDataAvailableEvent event)
            {
                WattsUpPacket[] values = event.getValue();
                System.out.printf("[%s] %s\n", format.format(new Date()), Arrays.toString(values));
            }
        });
        meter.connect();
    }
}
