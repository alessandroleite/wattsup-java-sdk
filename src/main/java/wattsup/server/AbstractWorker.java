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

import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import wattsup.event.WattsUpDisconnectEvent;
import wattsup.listener.WattsUpDisconnectListener;
import wattsup.listener.WattsUpListener;
import wattsup.meter.WattsUp;

public abstract class AbstractWorker implements Worker
{
    /**
     * The log reference.
     */
    protected static final Logger LOG = Logger.getLogger(AbstractWorker.class.getName());

    /**
     * This worker id.
     */
    private final UUID id_;

    /**
     * Reference to the power meter to read the data and send to this client.
     */
    private final WattsUp wattsUp_;

    /**
     * 
     */
    private final WattsUpListener[] listeners_;

    /**
     * This worker's state.
     */
    private volatile WorkerState state_;

    /**
     * 
     */
    private Object mutex_ = new Object();

    /**
     * @param id
     *            This worker id.
     * @param wattsUp
     *            The reference to the {@link WattsUp} to read the measurements.
     * @param listeners
     *            The listener to register in the {@link WattsUp}. Might not be <code>null</code>. By default this class register a
     *            {@link WattsUpDisconnectListener} to finish this work when the {@link WattsUp} disconnects.
     */
    public AbstractWorker(UUID id, WattsUp wattsUp, WattsUpListener... listeners)
    {
        this.id_ = id;
        this.wattsUp_ = wattsUp;

        this.listeners_ = new WattsUpListener[listeners.length + 1];
        System.arraycopy(listeners, 0, listeners_, 0, listeners.length);

        listeners_[listeners.length] = new WattsUpDisconnectListener()
        {
            @Override
            public void onDisconnect(WattsUpDisconnectEvent event)
            {
                finish();
            }
        };
    }

    @Override
    public void run()
    {
        synchronized (mutex_)
        {
            while (!isFinished())
            {
                try
                {
                    mutex_.wait();
                }
                catch (InterruptedException ignore)
                {
                    LOG.log(Level.FINEST, ignore.getMessage(), ignore);
                }
            }
        }
    }

    @Override
    public synchronized void start()
    {
        if (!this.isFinished() && !WorkerState.RUNNING.equals(state_))
        {
            state_ = WorkerState.RUNNING;

            for (int i = 0; i < this.listeners_.length - 1; i++)
            {
                this.wattsUp_.registerListener(listeners_[i]);
            }
        }
    }

    @Override
    public synchronized void stop()
    {
        if (!isFinished())
        {
            state_ = WorkerState.STOPPED;

            for (int i = 0; i < this.listeners_.length - 1; i++)
            {
                this.wattsUp_.unregisterListener(listeners_[i]);
            }
        }
    }

    @Override
    public synchronized void finish()
    {
        if (!isFinished())
        {
            this.stop();
            state_ = WorkerState.FINISHED;
            this.wattsUp_.unregisterListener(listeners_[listeners_.length - 1]);
            
            synchronized (mutex_)
            {
                mutex_.notifyAll();
            }
        }
    }

    @Override
    public UUID getId()
    {
        return id_;
    }

    /**
     * @return the stopped_
     */
    public boolean isStopped()
    {
        return WorkerState.STOPPED.equals(state_);
    }

    /**
     * @return the finished_
     */
    public boolean isFinished()
    {
        return WorkerState.FINISHED.equals(state_);
    }

    @Override
    public WorkerState getState()
    {
        return this.state_;
    }
}
