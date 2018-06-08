package clockapp.logic.models;

/**
 * A class meant for ease of implementing new functions of the application.
 */
public abstract class AbstractMessage implements IReadableMessage
{
    public enum ContentType{
        DATE,
        TIME
    }
}
