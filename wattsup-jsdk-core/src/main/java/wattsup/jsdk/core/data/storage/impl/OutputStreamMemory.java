package wattsup.jsdk.core.data.storage.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

import wattsup.jsdk.core.data.ID;
import wattsup.jsdk.core.data.storage.Memory;
import wattsup.jsdk.core.serialize.Serializer;

public class OutputStreamMemory<T> implements Memory<T>
{
    /**
     * 
     */
    private static final Logger LOG = Logger.getLogger(OutputStreamMemory.class.getName());

    /**
     * 
     */
    private final OutputStream out_;

    /**
     * 
     */
    private final Serializer<T, byte[]> serializer_;

    /**
     * The number of bytes write in the {@link OutputStream}.
     */
    private volatile int size_;

    /**
     * 
     * @param serializer
     *            The serializer to serialize the values.
     * @param out
     *            {@link OutputStream} to write the data.
     */
    public OutputStreamMemory(Serializer<T, byte[]> serializer, OutputStream out)
    {
        this.out_ = out;
        this.serializer_ = serializer;
    }

    @Override
    public void clear()
    {
    }

    @Override
    public <V> void dump(OutputStream out, Serializer<T, V> serializer) throws IOException
    {
        //see https://code.google.com/p/io-tools/
    }

    @Override
    public synchronized void put(ID id, T data)
    {
        try
        {
            byte[] b = this.serializer_.serialize(data);
            size_ += b.length;
            this.out_.write(b);
        }
        catch (IOException exception)
        {
            LOG.log(Level.SEVERE, exception.getMessage(), exception);
        }
    }

    @Override
    public synchronized int size()
    {
        return size_;
    }

    @Override
    public Collection<T> values()
    {
        return Collections.emptyList();
    }
}
