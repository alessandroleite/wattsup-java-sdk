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
package wattsup.listener.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import wattsup.data.Field;
import wattsup.data.WattsUpConfig.Delimiter;
import wattsup.data.WattsUpPacket;
import wattsup.event.WattsUpDataAvailableEvent;
import wattsup.exception.WattsUpException;
import wattsup.listener.WattsUpDataAvailableListener;

public class ExportCsvListener implements WattsUpDataAvailableListener
{
    /**
     * The format of the data print to the {@code output}. 
     */
    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    /**
     * The reference for the {@link OutputStream} to write the data.
     */
    private OutputStream output_;

    /**
     * @param output
     *            The reference for the {@link OutputStream} to write the data as soon as they are available.
     */
    public ExportCsvListener(OutputStream output)
    {
        this.output_ = Objects.requireNonNull(output);
    }

    @Override
    public void processDataAvailable(WattsUpDataAvailableEvent event)
    {
        StringBuilder sb = new StringBuilder();
        external: for (WattsUpPacket data : event.getValue())
        {
            sb.append(FORMAT.format(new Date(data.getTime())));
            
            for (Field f: data.getFields())
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
