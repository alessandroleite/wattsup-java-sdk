package wattsup.jsdk.core.listener.impl;

import java.util.Objects;

import wattsup.jsdk.core.data.WattsUpPacket;
import wattsup.jsdk.core.data.storage.WattsUpMemory;
import wattsup.jsdk.core.data.storage.impl.RAMWattsUpMemory;
import wattsup.jsdk.core.event.WattsUpDataAvailableEvent;
import wattsup.jsdk.core.listener.WattsUpDataAvailableListener;

public class DefaultWattsUpDataAvailableListener implements WattsUpDataAvailableListener
{
    /**
     * Memory instance to store the data.
     */
    private final WattsUpMemory memory_;

    /**
     * @param memory
     *            Memory instance to store the measurements. Might not be <code>null</code>.
     */
    public DefaultWattsUpDataAvailableListener(WattsUpMemory memory)
    {
        this.memory_ = Objects.requireNonNull(memory);
    }

    /**
     * Creates a new {@link DefaultWattsUpDataAvailableListener} instance storing all data into the RAM memory.
     */
    public DefaultWattsUpDataAvailableListener()
    {
        this(new RAMWattsUpMemory(Integer.MAX_VALUE));
    }

    @Override
    public void processDataAvailable(WattsUpDataAvailableEvent event)
    {
        for (WattsUpPacket value : event.getValue())
        {
            this.memory_.put(value);
        }
    }
}
