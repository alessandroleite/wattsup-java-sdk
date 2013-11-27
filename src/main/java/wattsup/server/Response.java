package wattsup.server;

import java.io.Serializable;
import java.util.UUID;

public class Response implements Serializable
{
    /**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
    private static final long serialVersionUID = 5002581119764808127L;

    /**
     * Response's id.
     */
    private UUID id_;

    /**
     * The response's data.
     */
    private Serializable data_;

    /**
     * Creates a new {@link Response} instance with a given ID.
     * 
     * @param id
     *            Response'id. Might not be <code>null</code>.
     */
    public Response(UUID id)
    {
        this.id_ = id;
    }

    /**
     * Default constructor.
     */
    public Response()
    {
        super();
    }

    /**
     * Creates a {@link Response} with a given ID.
     * 
     * @param id
     *            Response's id. Might not be <code>null</code>.
     * @return This response with the given ID.
     */
    public static Response newResponse(UUID id)
    {
        return new Response(id);
    }

    /**
     * 
     * @param id
     *            Response's id. Might not be <code>null</code>.
     * @return This response with the new ID value.
     */
    public Response withId(UUID id)
    {
        this.id_ = id;
        return this;
    }

    /**
     * 
     * @param data
     *            Response's data to assign.
     * @return This response with the new data value.
     */
    public Response withData(Serializable data)
    {
        this.data_ = data;
        return this;
    }

    /**
     * @return the id
     */
    public UUID getId()
    {
        return id_;
    }

    /**
     * @return the data
     * @param <T>
     *            The data's type.
     */
    @SuppressWarnings("unchecked")
    public <T extends Serializable> T getData()
    {
        return (T) data_;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id_ == null) ? 0 : id_.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        Response other = (Response) obj;
        if (id_ == null)
        {
            if (other.id_ != null)
            {
                return false;
            }
        }
        else if (!id_.equals(other.id_))
        {
            return false;
        }
        return true;
    }

}
