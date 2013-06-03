package wattsup.event;

import java.util.Collections;
import java.util.EventObject;
import java.util.LinkedList;
import java.util.List;

import wattsup.listener.WattsUpListener;

public abstract class WattsUpEvent<T> extends EventObject
{
	/**
	 * Serial code version <code>serialVersionUID<code> for serialization.
	 */
	private static final long serialVersionUID = 8129051864407658211L;


	/**
	 * The enumeration with the available events.
	 */
	public static enum EventType
	{
		/**
		 * Notifies when a connection with the meter is established.
		 */
		CONNECT,
		
		/**
		 * Notifies the reset (memory clear) of the meter.
		 */
		RESET,
		
		/**
		 * Notifies when there are data available. The frequency of the event is determined by the external sampling interval. 
		 */
		DATA_AVAILABLE,
		
		/**
		 * Notifies when the connection with the meter closed.
		 */
		DISCONNECT;
	}
	
	/**
	 * The values associated/returned with the event.
	 */
	private final T value_;
	
	/**
	 * The type of event.
	 */
	private final EventType type_;

	/**
	 * Creates a {@link WattsUpEvent} of the given type
	 * 
	 * @param source The source of this event.
	 * @param type The event's type.
	 * @param value The value(s) associated with this event.
	 */
	public WattsUpEvent(Object source, EventType type, T value) 
	{
		super(source);
		this.type_ = type;
		value_ = value;
	}

	/**
	 * @return the type_
	 */
	public EventType getType() 
	{
		return type_;
	}


	/**
	 * Returns the value(s) associated with this {@link WattsUpEvent}. 
	 * @return The value(s) associated with this {@link WattsUpEvent}.
	 */
	public T getValue() 
	{
		return value_;
	}

	/**
	 * Calls an event processing method, passing this {@link WattsUpEvent} as parameter.
	 * @param listener The listener to send this {@link WattsUpEvent}. 
	 */
	public abstract void processListener(WattsUpListener listener);
	
	/**
	 * Returns <code>true</code> if this {@link WattsUpListener} is an instance of a listener class that this event supports. 
	 * @param listener The {@link WattsUpListener} instance to evaluate.
	 * @return <code>true</code> if this {@link WattsUpListener} is an instance of a listener class that this event supports.
	 */
	public abstract boolean isAppropriateListener(WattsUpListener listener);
}
