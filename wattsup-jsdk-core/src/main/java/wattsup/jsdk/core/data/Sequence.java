package wattsup.jsdk.core.data;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 
 */
public final class Sequence
{
    /**
     */
    private AtomicLong source_ = new AtomicLong(0);

    /**
     * Returns the current value incremented by one.
     * 
     * @return The next sequence value.
     */
    public Long nextValue()
    {
        return this.source_.incrementAndGet();
    }

    /**
     * Returns the current value.
     * 
     * @return The current value.
     */
    public Long currentValue()
    {
        return this.source_.get();
    }
}
