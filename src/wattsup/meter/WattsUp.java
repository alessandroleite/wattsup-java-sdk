package wattsup.meter;


import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import wattsup.data.WattsUpConfig;
import wattsup.data.WattsUpPacket;
import wattsup.event.WattsUpConnectedEvent;
import wattsup.event.WattsUpDataAvailableEvent;
import wattsup.event.WattsUpEvent;
import wattsup.listener.WattsUpListener;

import static wattsup.data.command.WattsUpCommand.*;
import static wattsup.data.command.WattsUpCommand.CLEAR_MEMORY;
import static wattsup.data.command.WattsUpCommand.CONFIGURE_INTERNAL_LOGGING_INTERVAL;
import static wattsup.data.command.WattsUpCommand.REQUEST_ALL_DATA_LOGGED;

/**
 * Class to interact with the Watts Up? power meter. To use it, it's necessary:
 * 
 * <ul>
 * <li>Creates an instance. Call the constructor {@link #WattsUp(WattsUpConfig)};</li>
 * <li>Register a {@link wattsup.listener.WattsUpDataAvailableListener} listener to be notified when data (measure) are available. 
 *    Call the method {@link #registerListener(WattsUpListener)}</li>
 * <li>Connect to the meter. Call method {@link #connect()};</li>
 * <li>Disconnect after you finish the work/experiment. Call the method {@link #disconnect()}.</li>
 * </ul>
 * 
 * <br />
 * <strong>Usage Example</strong>: Here is a class that connect to the power meter during three minutes and print the measures to the console.
 * <pre>
 * {@code
 * public class WattsUpTest 
 * {
 * 	private static final long THREE_MINUTES = 3 * 60;
 * 
 * 	public static void main(String[] args) throws IOException 
 * 	{
 * 		final SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
 * 		final WattsUp meter = new WattsUp(new WattsUpConfig().withPort(args[0]).scheduleDuration(THREE_MINUTES));
 * 
 * 		meter.registerListener(new WattsUpDataAvailableListener() 
 * 		{
 * 			{@literal @}Override
 * 			public void processDataAvailable(final WattsUpDataAvailableEvent event) 
 * 			{
 * 				WattsUpPacket[] values = event.getValue();
 * 				System.out.printf("[%s] %s\n", format.format(new Date()), Arrays.toString(values));
 * 			}
 * 		});
 * 		meter.connect();
 * 	}
 * }
 * </pre>
 */
public final class WattsUp 
{
	/**
	 * The listeners registered for this {@link WattsUp} meter.
	 */
	private final List<WattsUpListener> listeners_ = new LinkedList<>();

	/**
	 * The configuration to be used by the meter.
	 */
	private final WattsUpConfig config_;
	
	/**
	 * The scheduler used to notify the clients about data available. 
	 * The interval of the notification is determined through {@link WattsUpConfig} or the one given by the method {@link #configureExternalLoggingInterval(int)}.
	 */
	private ScheduledExecutorService scheduler_;
	
	/**
	 * The reference for the {@link WattsUpConnection} to execute the commands.
	 */
	private WattsUpConnection connection_;
	
	/**
	 * A flag to indicate if the meter is ready.
	 */
	private volatile boolean configured_;
	
	/**
	 * Flag to indicate if this meter is connected.
	 */
	private volatile boolean connected_;
	
	/**
	 * Creates an instance of this {@link WattsUp} meter.
	 * 
	 * @param config The configuration of the meter.
	 */
	public WattsUp(final WattsUpConfig config) 
	{
		this.config_ = config;
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
		this.listeners_.add(listener);
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
			removed = this.listeners_.remove(listener);
		}
		
		return removed;
	}
	
	
	/**
	 * Connect to the meter. After connect the meter is reseted.
	 * @throws IOException If the power meter is not available. 
	 * @see #connect(boolean)
	 * @see #reset()
	 */
	public void connect() throws IOException
	{
		connect(true);
	}
	
	/**
	 * Connect to the power meter and reset the memory if configured.
	 * 
	 * @param reset Flag to indicates if the meter should be reseted.
	 * @throws IOException If the power meter is not available.
	 * @see #reset()
	 */
	public void connect(boolean reset) throws IOException
	{
        connection_ = new WattsUpConnection(this.config_);
		
		if (connection_.connect())
		{
			configure();
			
			if (reset)
	        {
	        	this.reset();
	        }
			notify(new WattsUpConnectedEvent(this));
			this.connected_ = true;
		}
		
		start();
	}
	
	/**
	 * Configure the device parameters. 
	 * @throws IOException If the device is not connected. 
	 */
	private void configure() throws IOException 
	{
		configureExternalLoggingInterval(this.config_.getExternalLoggingInterval());
		configured_ = true;
	}

	/**
	 * Returns <code>true</code> if the meter is online (connected).
	 * @return <code>true</code> if the meter is online (connected).
	 */
	public boolean isConnected()
	{
		return this.connected_ && this.connection_.isConnected();
	}
	
	/**
	 * Disconnect from the power meter.
	 * @throws IOException If the meter is disconnected. 
	 */
	public void disconnect() throws IOException
	{
		if (connection_ != null)
		{
			this.scheduler_.shutdownNow();
			
			this.stop();
			this.connection_.disconnect();
			
			this.configured_ = false;
			this.connected_ = false;
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
	public WattsUpPacket[] records() throws IOException
	{
		return this.connection_.execute(REQUEST_ALL_DATA_LOGGED);
	}
	
	/**
	 * Configure the internal logging interval to this meter.
	 * 
	 * @param interval The interval in seconds to be set. Might be greater than zero.
	 * @throws IOException If the communication with the meter is not possible. 
	 */
	public void configureInternalLoggingInterval(int interval) throws IOException
	{
		this.connection_.execute(CONFIGURE_INTERNAL_LOGGING_INTERVAL, "I", String.valueOf(interval), String.valueOf(interval));
	}
	
	/**
	 * Start up logging with the given {@code interval}.
	 * 
	 * @param interval The interval in seconds to configure the external logging.
	 * @throws IOException If it is not possible communicating with the meter.
	 */
	public void configureExternalLoggingInterval(int interval) throws IOException 
	{
		this.connection_.execute(CONFIGURE_EXTERNAL_LOGGING_INTERVAL, "E", String.valueOf(interval), String.valueOf(interval));
	}
	
	/**
	 * Stop logging this meter events.
	 * @throws IOException If it is not possible communicating with the meter.
	 */
	public void stop() throws IOException
	{
		this.connection_.execute(STOP_LOGGING);
	}
	
	/**
	 * Start up logging with the given {@code interval}.
	 */
	protected final void start()
	{
		scheduler_ = Executors.newScheduledThreadPool(1);
		final ScheduledFuture<?> handler = scheduler_.scheduleAtFixedRate(new Runnable() 
		{
			@Override
			public void run() 
			{
				try 
				{
					if (isConnected() && configured_)
					{ 
						final String data = connection_.read();
						final WattsUpPacket[] records = WattsUpPacket.parser(data, config_.getDelimiter());
						
						if (records.length > 0)
						{
							WattsUp.this.notify(new WattsUpDataAvailableEvent(this, records));
						}
					}
				} 
				catch (IOException exception) 
				{
					;
				}
			}
		},1, config_.getExternalLoggingInterval(), TimeUnit.SECONDS);	
		
		if (config_.getScheduleDurationInSeconds() > 0)
		{
			scheduler_.schedule(new Runnable() 
			{
				@Override
				public void run() 
				{
					try 
					{
						handler.cancel(true);
						disconnect();
					} 
					catch (IOException ignore) 
					{
						;
					}
				}
			}, config_.getScheduleDurationInSeconds(), TimeUnit.SECONDS);
		}
	}
	
	private Map<Class<WattsUpEvent<?>>, WattsUpEventInfo> eventListenerMap = new WeakHashMap<>();
	
	/**
	 * Notifies the correspondent listener about an {@link WattsUpEvent} event.
	 * @param event The reference to the {@link WattsUpEvent} to be notified. Might not be <code>null</code>. 
	 */
	@SuppressWarnings("unchecked")
	private <T> void notify(WattsUpEvent<T> event) 
	{
		for(WattsUpListener listener : listeners_)
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