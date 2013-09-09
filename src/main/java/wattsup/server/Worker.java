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

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import wattsup.event.WattsUpDisconnectEvent;
import wattsup.listener.WattsUpDisconnectListener;
import wattsup.listener.impl.ExportCsvListener;
import wattsup.meter.WattsUp;

public final class Worker implements Runnable
{
    /**
     * The log reference.
     */
    private static final Logger log = Logger.getLogger(Worker.class.getName());
    
    /**
     * The reference to the socket client.
     */
    private final Socket client_;

    /**
     * Reference to the power meter to read the data and send to this client.
     */
    private final WattsUp wattsUp_;

    /**
     * The socket output to write the data.
     */
    private OutputStream output_;

    /**
     * A flag to indicates if the {@link WattsUp} is still connected.
     */
    private boolean disconnected_;

    /**
     * 
     * @param socket
     *            The socket reference. Might not be <code>null</code>.
     * @param wattsUp
     *            The reference to the {@link WattsUp} to read the measurements.
     * @throws IOException
     *             If an I/O error occurs.
     */
    public Worker(Socket socket, WattsUp wattsUp) throws IOException
    {
        this.client_ = socket;
        this.wattsUp_ = wattsUp;
        output_ = client_.getOutputStream();
        this.wattsUp_.registerListener(new ExportCsvListener(output_));
        this.wattsUp_.registerListener(new WattsUpDisconnectListener()
        {
            @Override
            public void onDisconnect(WattsUpDisconnectEvent event)
            {
                disconnected_ = true;
            }
        });
    }

    @Override
    public void run()
    {
        int i = 0;
        while (!disconnected_)
        {
            i++;
        }

        try
        {
            this.output_.close();
            this.client_.close();
        }
        catch (IOException exception)
        {
            log.log(Level.WARNING, exception.getMessage());
        }
        
        log.log(Level.FINEST, "The number of records read was: " + i);
    }
}
