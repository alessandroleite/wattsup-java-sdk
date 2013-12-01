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

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import wattsup.jsdk.core.data.Field;
import wattsup.jsdk.core.data.WattsUpPacket;
import wattsup.jsdk.core.data.WattsUpConfig.Delimiter;
import wattsup.jsdk.core.event.WattsUpDataAvailableEvent;
import wattsup.jsdk.core.exception.WattsUpException;
import wattsup.jsdk.core.listener.WattsUpDataAvailableListener;

public class ExportCsvListener implements WattsUpDataAvailableListener
{
    /**
     * The format of the data print to the {@code output}.
     */
    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * The reference for the {@link OutputStream} to write the data.
     */
    private final OutputStream output_;

    /**
     * A flag to indicate if the row's number must be included in the file.
     */
    private boolean addRownum_;
    
    /**
     * A flag to indicate if a header number must be included in output. The header is the name of the fields/columns.
     */
    private boolean addHeader_;
    
    /**
     * Flag to indicate if the header has already been inserted.
     */
    private volatile boolean headerAdded_;

    /**
     * The number of the row incremented after each write in the file.
     */
    private volatile int rownum_;


    /**
     * @param output
     *            The reference for the {@link OutputStream} to write the data as soon as they are available.
     */
    public ExportCsvListener(OutputStream output)
    {
        this(output, true, true);
    }

    /**
     * 
     * @param output
     *            The reference for the {@link OutputStream} to write the data as soon as they are available.
     * @param addHeader
     *            Flag to indicates if the header must be included in the output.
     * @param addRownum
     *            Flag to indicates if the row number must be included as the first column/field of the output file.
     */
    public ExportCsvListener(OutputStream output, boolean addHeader, boolean addRownum)
    {
        this.output_ = Objects.requireNonNull(output);
        this.addHeader_ = addHeader;
        this.addRownum_ = addRownum;
    }

    /**
     * Inserts the header into the output file. The header is the name of each field including only letters [a-z,A-Z].
     * 
     * @param data
     *            The reference to the {@link WattsUpPacket} that has the fields for the CSV's file.
     * @param output
     *            A {@link StringBuffer} to append a line with the reader.
     */
    private void header(final WattsUpPacket data, final StringBuilder output)
    {
        if (addRownum_)
        {
            output.append("rownum").append(Delimiter.COMMA.getSymbol());
        }
        
        output.append("date");
        
        for (Field f : data.getFields())
        {
            output.append(Delimiter.COMMA.getSymbol());
            output.append(f.getName() != null ? f.getName().replaceAll("\\W", "") : "null");
        }
        output.append("\n");
    }

    @Override
    public void processDataAvailable(WattsUpDataAvailableEvent event)
    {
        StringBuilder sb = new StringBuilder();
        external: for (WattsUpPacket data : event.getValue())
        {
            if (addHeader_ && !headerAdded_)
            {
                header(data, sb);
                headerAdded_ = true;
            }

            if (addRownum_)
            {
                sb.append(++rownum_).append(Delimiter.COMMA.getSymbol());
            }
            
            sb.append(FORMAT.format(new Date(data.getTime())));

            for (Field f : data.getFields())
            {
                try
                {
                    sb.append(Delimiter.COMMA.getSymbol());
                    sb.append(Double.valueOf(f.getValue()) / 10);
                }
                catch (NumberFormatException nfe)
                {
                    continue external;
                }
            }

            sb.append("\n");

            try
            {
                output_.write(sb.toString().getBytes());
            }
            catch (IOException exception)
            {
                throw new WattsUpException(exception.getMessage(), exception);
            }

            sb.setLength(0);
        }
    }
}
