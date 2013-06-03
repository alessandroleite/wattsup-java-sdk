package wattsup.listener;

import wattsup.event.WattsUpConnectedEvent;

public interface WattsUpConnectionListerner extends WattsUpListener 
{
	
	/**
	 * Called after a connection with the meter has been established. 
	 * @param event
	 *            The {@link WattsUpConnectedEvent} with the data about the
	 *            connected power meter.
	 */
	void onConnected(WattsUpConnectedEvent event);
}
