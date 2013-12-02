/**
 *     Copyright (C) 2013 Contributors
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package wattsup.jsdk.core.data.storage.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import wattsup.jsdk.core.data.ID;
import wattsup.jsdk.core.data.WattsUpPacket;
import wattsup.jsdk.core.data.storage.WattsUpMemory;
import wattsup.jsdk.core.serialize.WattsUpPacketSerializer;

public class RAMWattsUpMemory implements WattsUpMemory
{
    /**
     * A thread-safe {@link Map}'s instance to store the measurements.
     */
    private final Map<ID, WattsUpPacket> entriesMap_;

    /**
     * Creates a new {@link RAMWattsUpMemory} empty WattsUpMemory with the specified capacity.
     * 
     * @param initialCapacity
     *            The initial capacity.
     */
    public RAMWattsUpMemory(int initialCapacity)
    {
        this.entriesMap_ = new ConcurrentHashMap<ID, WattsUpPacket>(initialCapacity);
    }

    @Override
    public void clear()
    {
        this.entriesMap_.clear();
    }

    @Override
    public void dump(OutputStream out, WattsUpPacketSerializer serializer) throws IOException
    {
        for (WattsUpPacket packet : this.entriesMap_.values())
        {
            out.write(serializer.serialize(packet));
        }
    }

    @Override
    public boolean put(WattsUpPacket data)
    {
        entriesMap_.put(data.getId(), data);
        return true;
    }

    @Override
    public int size()
    {
        return this.entriesMap_.size();
    }

    @Override
    public Collection<WattsUpPacket> values()
    {
        return Collections.unmodifiableCollection(this.entriesMap_.values());
    }
}
