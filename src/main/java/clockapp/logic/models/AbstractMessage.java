package clockapp.logic.models;

/**
 * A class meant for ease of implementing new functions of the application.
 */
public abstract class AbstractMessage implements IReadableMessage
{
    protected final ContentType contentType;

    public AbstractMessage(ContentType contentType)
    {
        this.contentType = contentType;
    }

    public enum ContentType{
        DATE,
        TIME
    }

    public ContentType getContentType()
    {
        return contentType;
    }
}
