package wattsup.jsdk.server.memory;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;

import org.mapdb.DB;
import org.mapdb.DBMaker;

import wattsup.jsdk.core.data.ID;
import wattsup.jsdk.core.data.storage.Memory;
import wattsup.jsdk.core.serialize.Serializer;


public class OffHeapMemory<T> implements Memory<T>
{
    /**
     * 
     */
    private final DB storage_;

    public OffHeapMemory()
    {
        storage_ = DBMaker.newDirectMemoryDB().compressionEnable().make();
    }

    @Override
    public void clear()
    {
    }

    @Override
    public <V> void dump(OutputStream out, Serializer<T, V> serializer) throws IOException
    {
    }

    @Override
    public void put(ID id, T data)
    {
    }

    @Override
    public int size()
    {
        return 0;
    }

    @Override
    public Collection<T> values()
    {
        return null;
    }

    public Memory<T> getRegion(String name)
    {
        return null;
    }

    public synchronized void freeRegion(String name)
    {
        this.storage_.delete(name);
    }

}
