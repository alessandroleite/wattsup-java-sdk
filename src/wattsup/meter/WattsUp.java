package wattsup.meter;


import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;

import wattsup.data.WattsUpConfig;
import wattsup.data.WattsUpPacket;
import wattsup.event.WattsUpConnectedEvent;
import wattsup.event.WattsUpDataAvailableEvent;
import wattsup.event.WattsUpEvent;
import wattsup.listener.WattsUpConnectionListerner;
import wattsup.listener.WattsUpListener;

import static wattsup.data.command.WattsUpCommand.CLEAR_MEMORY;
import static wattsup.data.command.WattsUpCommand.CONFIGURE_INTERNAL_LOGGING_INTERVAL;
import static wattsup.data.command.WattsUpCommand.REQUEST_ALL_DATA_LOGGED;

public final class WattsUp 
{
	private final List<WattsUpListener> listeners = new LinkedList<>();
	
	/**
	 * The reference for the {@link WattsUpConnection} to execute the commands.
	 */
	private WattsUpConnection connection_;

	/**
	 * The configuration to be used by the meter.
	 */
	private final WattsUpConfig config_;
	
	/**
	 * Creates an instance of this {@link WattsUp} meter.
	 * 
	 * @param config The configuration of the meter.
	 */
	public WattsUp(final WattsUpConfig config) 
	{
		this.config_ = config;
		
		this.registerListener(new WattsUpConnectionListerner() 
		{
			@Override
			public void onConnected(WattsUpConnectedEvent event) 
			{
				Thread pooling = new Thread(new Runnable() 
				{
					@Override
					public void run() 
					{
						while(isConnected())
						{
							try 
							{
								retrieveAllData();
								Thread.sleep(config.getExternalLoggingInterval() * 1000);
							} catch (InterruptedException | IOException ie) 
							{
								Thread.currentThread().interrupt();
							}
						}
					}
				}, "internal-event");
				pooling.setDaemon(true);
				pooling.start();
			}
		});
	}

	/**
	 * Register a {@link WattsUpListener} to be notified when the event that it is interesting happened.
	 * 
	 * @param listener The instance for the {@link WattsUpListener} to be registered. Might not be <code>null</code>. 
	 * @throws NullPointerException If the listener is <code>null</code>.
	 */
	public void registerListener(WattsUpListener listener) 
	{
		if (listener == null) 
		{
			throw new NullPointerException("The listener might not be null!");
		}
		this.listeners.add(listener);
	}
	
	/**
	 * Returns <code>true</code> if the {@link WattsUpListener} reference was removed of <code>false</code> otherwise.
	 * 
	 * @param listener The reference to the {@link WattsUpListener} to be removed.
	 * @return <code>true</code> if the {@link WattsUpListener} reference was removed of <code>false</code> otherwise.
	 */
	public boolean unregisterListener(WattsUpListener listener)
	{
		boolean removed = false;
		
		if (listener != null)
		{
			removed = this.listeners.remove(listener);
		}
		
		return removed;
	}
	
	
	/**
	 * Connect to the meter.
	 */
	public void connect()
	{
		connection_ = new WattsUpConnection(this.config_);
		
		if (connection_.connect())
		{
			notify(new WattsUpConnectedEvent(this));
		}
	}
	
	/**
	 * Returns <code>true</code> if the meter is online (connected).
	 * @return <code>true</code> if the meter is online (connected).
	 */
	public boolean isConnected()
	{
		return this.connection_.isConnected();
	}
	
	/**
	 * Disconnect from the power meter.
	 */
	public void disconnect()
	{
		if (connection_ != null)
		{
			this.connection_.disconnect();
		}
	}
	
	/**
	 * Clear the memory of the meter.
	 * @throws IOException If the communication with the meter is not possible. 
	 */
	public void reset() throws IOException
	{
		this.connection_.execute(CLEAR_MEMORY);
	}
	
	/**
	 * Retrieve and returns all data available in the meter.
	 * 
	 * @return A non-null array with the all data available in the meter.
	 * @throws IOException If is not possible to retrieve the data.
	 */
	public WattsUpPacket[] retrieveAllData() throws IOException
	{
		final WattsUpPacket[] data = this.connection_.execute(REQUEST_ALL_DATA_LOGGED);
		
		if (data.length > 0)
		{
			notify(new WattsUpDataAvailableEvent(this, data));
		}
		return data;
	}
	
	/**
	 * Configure the internal logging interval to this meter.
	 * 
	 * @param interval The interval in seconds to be set. Might be greater than zero.
	 * @throws IOException If the communication with the meter is not possible. 
	 */
	public void setInternalLoggingInterval(int interval) throws IOException
	{
		this.connection_.execute(CONFIGURE_INTERNAL_LOGGING_INTERVAL, "I", "1", String.valueOf(interval), String.valueOf(interval));
	}
	
	
	
	private Map<Class<WattsUpEvent<?>>, WattsUpEventInfo> eventListenerMap = new WeakHashMap<>();
	
	/**
	 * Notifies the correspondent listener about an {@link WattsUpEvent} event.
	 * @param event The reference to the {@link WattsUpEvent} to be notified. Might not be <code>null</code>. 
	 */
	@SuppressWarnings("unchecked")
	private <T> void notify(WattsUpEvent<T> event) 
	{
		for(WattsUpListener listener : listeners)
		{
			if (event.isAppropriateListener(listener))
			{
				WattsUpEventInfo eventInfo = eventListenerMap.get(event.getClass());
				
				if (eventInfo == null)
				{
					eventInfo = new WattsUpEventInfo();
					eventListenerMap.put((Class<WattsUpEvent<?>>) event.getClass(), eventInfo);
				}
				
				try 
				{
					Method method = eventInfo.getEventMethodFor(event, listener);
					Objects.requireNonNull(method).invoke(listener, event);
				} 
				catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException exception) 
				{
					throw new RuntimeException(exception.getMessage(), exception);
				}
			}
		}
	}
	
	private static final class WattsUpEventInfo
	{
		private final Map<Class<?>, Method> eventListenerMap = new WeakHashMap<>();
		
		private <T> Method getEventMethodFor(final WattsUpEvent<T> event, final WattsUpListener listener)
		{
			Method method = eventListenerMap.get(listener.getClass());
			
			if (method == null)
			{
				for(Method meth : listener.getClass().getDeclaredMethods())
				{
					if (meth.getParameterTypes() != null && 
						meth.getParameterTypes().length == 1 && 
						meth.getParameterTypes()[0].equals(event.getClass()))
					{
						meth.setAccessible(true);
						eventListenerMap.put(listener.getClass(), meth);
						method = meth;
						break;
					}
				}
			}
			return method;
		}
	}
}
