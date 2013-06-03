package wattsup.event;

import wattsup.data.WattsUpPacket;
import wattsup.listener.WattsUpDataAvailableListener;
import wattsup.listener.WattsUpListener;

public class WattsUpDataAvailableEvent extends WattsUpEvent<WattsUpPacket[]> 
{
	/**
	 * Serial code version <code>serialVersionUID<code> for serialization.
	 */
	private static final long serialVersionUID = 7266611207810888376L;

	public WattsUpDataAvailableEvent(Object source, WattsUpPacket[] values) 
	{
		super(source, WattsUpEvent.EventType.DATA_AVAILABLE, values);
	}

	@Override
	public void processListener(WattsUpListener listener) 
	{
		((WattsUpDataAvailableListener) listener).processDataAvailable(this);
	}

	@Override
	public boolean isAppropriateListener(WattsUpListener listener) 
	{
		return listener instanceof WattsUpDataAvailableListener;
	}
}
