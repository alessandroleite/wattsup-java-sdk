/**
 *     WattsUp-J is a Java application to interact with the Watts up? power meter.
 *     Copyright (C) 2013  Contributors
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
 *
 *     Contributors:
 *         Alessandro Ferreira Leite - the initial implementation.
 */
package wattsup.server;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import wattsup.data.WattsUpPacket;
import wattsup.listener.impl.WattsUpDataMemoryStorage;
import wattsup.meter.WattsUp;

public final class AsyncWorker extends AbstractWorker
{
    /**
     * Reference for the WattsUp server storage.
     */
    private final Map<Long, WattsUpPacket> storage_;

    /**
     * @param id
     *            This worker id.
     * @param wattsUp
     *            The reference to the {@link WattsUp} to read the measurements.
     * @param storage
     *            Reference for the WattsUp storage.
     */
    public AsyncWorker(UUID id, WattsUp wattsUp, Map<Long, WattsUpPacket> storage)
    {
        super(id, wattsUp, new WattsUpDataMemoryStorage(Objects.requireNonNull(storage)));
        this.storage_ = storage;
    }

    @Override
    public Map<Long, WattsUpPacket> getData()
    {
        return Collections.unmodifiableMap(new HashMap<Long, WattsUpPacket>(storage_));
    }
}
