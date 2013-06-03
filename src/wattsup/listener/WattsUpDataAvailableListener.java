package wattsup.listener;

import wattsup.event.WattsUpDataAvailableEvent;

public interface WattsUpDataAvailableListener extends WattsUpListener 
{

	/***
	 * @param event The {@link WattsUpDataAvailableEvent} event with the data available in the meter.
	 */
	void processDataAvailable(WattsUpDataAvailableEvent event);
	
}
