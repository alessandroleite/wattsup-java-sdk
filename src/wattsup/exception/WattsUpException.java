package wattsup.exception;

public class WattsUpException extends RuntimeException
{
    /**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
    private static final long serialVersionUID = 2736900463614479116L;

    /**
     * Default constructor.
     */
    public WattsUpException()
    {
        super();
    }

    /**
     * Creates a {@link WattsUpException} with the {@code message}.
     * @param message The message with information about the exception.
     */
    public WattsUpException(String message)
    {
        super(message);
    }

    /**
     * Creates a {@link WattsUpException} with the {@code cause}.
     * @param cause The root of this exception.
     */
    public WattsUpException(Throwable cause)
    {
        super(cause);
    }

    /**
     * Creates a {@link WattsUpException} with the {@code message} and {@code cause}.
     * @param message The message with information about the exception.
     * @param cause The root of this exception.
     */
    public WattsUpException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
