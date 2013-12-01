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
package wattsup.jsdk.core.listener.impl;

import java.util.Map;

import wattsup.jsdk.core.data.WattsUpPacket;
import wattsup.jsdk.core.event.WattsUpDataAvailableEvent;
import wattsup.jsdk.core.listener.WattsUpDataAvailableListener;

public class WattsUpDataMemoryStorage implements WattsUpDataAvailableListener
{
    /**
     * 
     */
    private final Map<Long, WattsUpPacket> storage_;

    /**
     * Creates a new {@link WattsUpDataMemoryStorage} assigned it a storage.
     * 
     * @param storage
     *            The storage to insert the data.
     */
    public WattsUpDataMemoryStorage(Map<Long, WattsUpPacket> storage)
    {
        this.storage_ = storage;
    }

    @Override
    public void processDataAvailable(WattsUpDataAvailableEvent event)
    {
        for (WattsUpPacket value : event.getValue())
        {
            storage_.put(value.getTime(), value);
        }
    }
}
