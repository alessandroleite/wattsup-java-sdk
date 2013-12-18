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
import java.io.Serializable;
import java.util.Collection;

import wattsup.jsdk.core.convert.WattsUpPacketMeasurementConverter;
import wattsup.jsdk.core.data.ID;
import wattsup.jsdk.core.data.Measurement;
import wattsup.jsdk.core.data.WattsUpPacket;
import wattsup.jsdk.core.data.storage.Memory;
import wattsup.jsdk.core.data.storage.database.MeasurementDAO;
import wattsup.jsdk.core.serialize.Serializer;

public final class DatabaseMemory implements Memory<WattsUpPacket>
{
    /**
     * 
     */
    private final static WattsUpPacketMeasurementConverter WATTSUP_TO_MEASUREMENT_CONVERTER = new WattsUpPacketMeasurementConverter();

    /**
     * The object to persist the data into the database.
     */
    private MeasurementDAO measurementDAO_;

    /**
     * The time of the first measurement.
     */
    private volatile Long startTime_;

    @Override
    public void clear()
    {
        this.measurementDAO_.deleteInInterval(this.startTime_, System.currentTimeMillis());
    }

    @Override
    public void dump(OutputStream out, Serializer serializer) throws IOException
    {
        serializer.serialize(out, (Serializable) values());
    }

    @Override
    public void put(ID id, WattsUpPacket data)
    {
        synchronized (this)
        {
            if (this.startTime_ == null)
            {
                this.startTime_ = data.getTime();
            }

            this.measurementDAO_.insert(convert(data));
        }
    }

    @Override
    public int size()
    {
        return this.measurementDAO_.countInInterval(this.startTime_, System.currentTimeMillis());
    }

    @Override
    public Collection<WattsUpPacket> values()
    {
        return null;
    }

    /**
     * Converts a {@link WattsUpPacket} object to a {@link Measurement} object.
     * 
     * @param data
     *            The {@link WattsUpPacket} to be converted to {@link Measurement}.
     * @return A {@link Measurement} object with the state of the {@link WattsUpPacket}.
     */
    private Measurement convert(WattsUpPacket data)
    {
        return WATTSUP_TO_MEASUREMENT_CONVERTER.convert(data);
    }
}
