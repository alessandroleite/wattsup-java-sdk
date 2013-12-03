package wattsup.jsdk.core.listener.impl;

import java.util.Objects;

import wattsup.jsdk.core.data.WattsUpPacket;
import wattsup.jsdk.core.data.storage.Memory;
import wattsup.jsdk.core.data.storage.impl.RamMemory;
import wattsup.jsdk.core.event.WattsUpDataAvailableEvent;
import wattsup.jsdk.core.listener.WattsUpDataAvailableListener;

public class DefaultWattsUpDataAvailableListener implements WattsUpDataAvailableListener
{
    /**
     * Memory instance to store the data.
     */
    private final Memory<WattsUpPacket> memory_;

    /**
     * @param memory
     *            Memory instance to store the measurements. Might not be <code>null</code>.
     */
    public DefaultWattsUpDataAvailableListener(Memory<WattsUpPacket> memory)
    {
        this.memory_ = Objects.requireNonNull(memory);
    }

    /**
     * Creates a new {@link DefaultWattsUpDataAvailableListener} instance storing all data into the RAM memory.
     */
    public DefaultWattsUpDataAvailableListener()
    {
        this(new RamMemory<WattsUpPacket>(Integer.MAX_VALUE));
    }

    @Override
    public void processDataAvailable(WattsUpDataAvailableEvent event)
    {
        for (WattsUpPacket value : event.getValue())
        {
            this.memory_.put(value.getId(), value);
        }
    }
}
