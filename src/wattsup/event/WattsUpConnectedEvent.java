package wattsup.event;

import wattsup.listener.WattsUpConnectionListerner;
import wattsup.listener.WattsUpListener;

public class WattsUpConnectedEvent extends WattsUpEvent<Void>
{
    /**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
    private static final long serialVersionUID = 9162190000759203691L;

    /**
     * @param source The reference to the source of this event.
     */
    public WattsUpConnectedEvent(Object source)
    {
        super(source, wattsup.event.WattsUpEvent.EventType.CONNECT, null);
    }

    @Override
    public void processListener(WattsUpListener listener)
    {
        ((WattsUpConnectionListerner) listener).onConnected(this);
    }

    @Override
    public boolean isAppropriateListener(WattsUpListener listener)
    {
        return listener instanceof WattsUpConnectionListerner;
    }
}
