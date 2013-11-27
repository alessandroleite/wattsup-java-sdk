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

import java.util.Map;
import java.util.UUID;

import wattsup.data.WattsUpPacket;

public interface Worker extends Runnable
{

    /**
     * Return the worker's id.
     * 
     * @return The worker's id.
     */
    UUID getId();

    /**
     * Returns the {@link Worker}'s state.
     * 
     * @return The worker's state.
     */
    WorkerState getState();

    /**
     * Returns a read-only view with the data of this worker. The key is the time in milliseconds of the {@link WattsUpPacket}.
     * 
     * @return A read-only view with the data of this worker.
     */
    Map<Long, WattsUpPacket> getData();

    /**
     * 
     */
    void start();

    /**
     * Stop this work but it's still alive.
     */
    void stop();

    /**
     * 
     */
    void finish();
}
