package wattsup.jsdk.core.serialize;

public interface Serializer<T, V>
{
    /**
     * Serializes an object to a specified format.
     * 
     * @param value
     *            Object to serialize.
     * @return The given value serialized.
     */
    V serialize(T value);
}
