package wattsup.listener;

import java.util.EventListener;

/**
 * A generic base interface for event listeners for various types of {@link wattsup.event.WattsUpEvent}. All listener interfaces for specific
 * WattsUpEvent event types must extend this interface.
 * 
 * Implementations of this interface must have a zero-args public constructor.
 */
public interface WattsUpListener extends EventListener
{
}
