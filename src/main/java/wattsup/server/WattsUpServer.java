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
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import wattsup.meter.WattsUp;

public final class WattsUpServer implements Runnable
{
    /**
     * The port number, or 0 to use a port number that is automatically allocated.
     */
    private final int port_;

    /**
     * 
     */
    private volatile boolean started_;

    /**
     * 
     */
    private ServerSocket server_;

    /**
     * The thread pool.
     */
    private final ExecutorService threadPool_ = Executors.newFixedThreadPool(10);

    /**
     * The reference to the {@link WattsUp} to read the data.
     */
    private final WattsUp wattsUp_;

    /**
     * @param port
     *            The port to initialize the server. A port number of 0 means that the port number is automatically allocated.
     * @param wattsUp The {@link WattsUp} reference to read the power measurements.
     */
    public WattsUpServer(int port, WattsUp wattsUp)
    {
        this.port_ = port;
        this.wattsUp_ = wattsUp;
    }

    @Override
    public void run()
    {
        openConnection();

        while (started_)
        {
            try
            {
                Socket client = this.server_.accept();
                this.threadPool_.execute(new Worker(client, wattsUp_));
            }
            catch (IOException e)
            {
                if (this.isStopped())
                {
                    return;
                }
                throw new RuntimeException(e.getMessage(), e);
            }
        }
        this.threadPool_.shutdown();
    }

    /**
     * Stop this server.
     */
    public synchronized void stop()
    {
        this.started_ = false;
        try
        {
            this.server_.close();
        }
        catch (IOException exception)
        {
            throw new RuntimeException(exception.getMessage(), exception);
        }
    }

    /**
     * Returns <code>true</code> if the server socket is closed.
     * 
     * @return <code>true</code> if the server socket is closed.
     */
    public synchronized boolean isStopped()
    {
        return !this.started_;
    }

    /**
     * Creates a server socket, bound to a given port.
     * 
     * @throws RuntimeException
     *             If it's impossible to create the server socket.
     */
    private void openConnection()
    {
        try
        {
            this.server_ = new ServerSocket(port_);
            this.started_ = true;
        }
        catch (IOException exception)
        {
            throw new RuntimeException(exception.getMessage(), exception);
        }
    }
}
