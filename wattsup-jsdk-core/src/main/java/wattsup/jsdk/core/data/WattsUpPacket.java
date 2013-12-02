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
package wattsup.jsdk.core.data;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import wattsup.jsdk.core.data.WattsUpConfig.Delimiter;

public final class WattsUpPacket implements Serializable
{
    /**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
    private static final long serialVersionUID = -252864256406279128L;

    /**
     * The number of field defined by the power meter.
     */
    private static final int NUM_FIELDS = 18;

    /**
     * The delimiter of a record (line). This cannot be changed.
     */
    private static final String RECORD_DELIMITER = ";";

    /**
     * The labels defined by the power meter.
     */
    private static final String[] LABELS = new String[NUM_FIELDS];
    
    
    /**
     * This packet id.
     */
    private final ID id_;

    /**
     * The command for the packet.
     */
    private String command_;

    /**
     * The sub-command for the packet.
     */
    private String subCommand_;

    /**
     * The data as returned by the meter.
     */
    private String data_;

    /**
     * The number of fields returned by the power meter. It can be less than the number of fields.
     */
    private int count_;

    /**
     * The time in milliseconds that this {@link WattsUpPacket} was read.
     */
    private long time_;

    /**
     * The fields available in this packet.
     */
    private final Field[] fields_ = new Field[NUM_FIELDS];

    static
    {
        LABELS[0] = "watts";
        LABELS[1] = "volts";
        LABELS[2] = "amps";
        LABELS[3] = "kWh";
        LABELS[4] = "cost";
        LABELS[5] = "mo. kWh";
        LABELS[6] = "mo. cost";
        LABELS[7] = "max watts";
        LABELS[8] = "max volts";
        LABELS[9] = "max amps";
        LABELS[10] = "min watts";
        LABELS[11] = "min volts";
        LABELS[12] = "min amps";
        LABELS[13] = "power factor";
        LABELS[14] = "duty cycle";
        LABELS[15] = "power cycle";
    }

    /**
     * Private constructor to avoid instance of this class outside of the method {@link #parser(String, Delimiter, long)}.
     * @param record The data as returned by the meter.
     * @param time The time that the data were read.
     */
    private WattsUpPacket(String record, long time)
    {
        this.data_ = record;
        this.time_ = time;
        this.id_ = ID.fromLong(time);
    }

    /**
     * Parser the output of the power meter as an instance of {@link WattsUpPacket}.
     * 
     * @param data
     *            The data to be parsed. Might not be <code>null</code>.
     * @param delimiter
     *            The delimiter used by the power meter.
     * @param packetTime
     *            The time that the {@code data} was read.
     * @return A non <code>null</code> array with the packets.
     */
    public static WattsUpPacket[] parser(final String data, final Delimiter delimiter, long packetTime)
    {
        WattsUpPacket[] packets = new WattsUpPacket[0];
        if (data != null && data.length() > 0)
        {
            String[] lines = data.split(RECORD_DELIMITER);

            // if (lines.length > 3)
            // {
            // String [] records = new String[lines.length - 3];
            // System.arraycopy(lines, 1, records, 0, records.length);
            // packets = new WattsUpPacket[Integer.valueOf(lines[0].split(delimiter.getSymbol())[5])];
            //
            // }
            packets = parser(lines, delimiter, packetTime);
        }
        return packets;
    }

    /**
     * 
     * @param records
     *            The Watts Up record to be parser to {@link WattsUpPacket}.
     * @param delimiter
     *            The reference to the field {@link Delimiter}.
     * @param packetTime
     *            The time that the {@code data} was read.
     * @return A non-null array with the data about the watts up measures.
     */
    protected static WattsUpPacket[] parser(final String[] records, final Delimiter delimiter, long packetTime)
    {
        List<WattsUpPacket> packets = new LinkedList<>();

        for (int i = 0; i < records.length; i++)
        {
            final String record = records[i].trim();

            if (!record.startsWith("#d") && !record.endsWith(RECORD_DELIMITER))
            {
                continue;
            }

            final String[] fields = record.split(delimiter.getSymbol());

            if (fields.length >= 3)
            {
                int j = 0;
                WattsUpPacket packet = new WattsUpPacket(record, packetTime);

                packet.command_ = fields[j++].trim();

                if (packet.command_.length() >= 1)
                {
                    packet.command_ = packet.command_.substring(1).trim();
                }

                packet.subCommand_ = fields[j++].trim();
                packet.count_ = Integer.valueOf(fields[j++].trim());

                for (int k = 0; k < packet.count_ && j < fields.length; k++)
                {
                    packet.fields_[k] = Field.valueOf(LABELS[k], fields[j++].trim());
                }

                packets.add(packet);
            }
        }

        return packets.toArray(new WattsUpPacket[packets.size()]);
    }
    
    

    /**
     * @return the id
     */
    public ID getId()
    {
        return id_;
    }

    /**
     * @return the command
     */
    public String getCommand()
    {
        return command_;
    }

    /**
     * @return the subCommand
     */
    public String getSubCommand()
    {
        return subCommand_;
    }

    /**
     * @return the data
     */
    public String getData()
    {
        return data_;
    }

    /**
     * @return the fields
     */
    public Field[] getFields()
    {
        return fields_.clone();
    }
    
    /**
     * @return the time
     */
    public long getTime()
    {
        return time_;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();

        for (Field f : fields_)
        {
            sb.append(" ").append(f.getValue());
        }
        return sb.toString();
    }
}
