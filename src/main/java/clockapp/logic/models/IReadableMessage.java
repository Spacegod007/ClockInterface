package clockapp.logic.models;

public interface IReadableMessage
{
    /**
     * <p>Always starts with DATE or TIME which represents the contents of the data.
     * After this part the message will follow up with the contents.
     * Depending on the DATE or TIME value it will continue with either
     * the DATE using the following format:
     * DATE:[DAY]-[MONTH]-[YEAR]
     * the TIME using the following 24 hour format:
     * TIME:[HOUR]-[MINUTE]
     * </p>
     * @return A String representing the contents of the message
     */
    String getContents();
}
