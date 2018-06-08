package clockapp.logic.models;

public class Message extends AbstractMessage
{
    private static final int NULL_VALUE = -1;
    private final int minute, hour, day, month, year;

    public Message(int day, int month, int year)
    {
        this(NULL_VALUE, NULL_VALUE, day, month, year, ContentType.DATE);
    }

    public Message(int hour, int minute)
    {
        this(minute, hour, NULL_VALUE, NULL_VALUE, NULL_VALUE, ContentType.TIME);
    }

    private Message(int minute, int hour, int day, int month, int year, ContentType contentType)
    {
        super(contentType);
        this.minute = minute;
        this.hour = hour;
        this.day = day;
        this.month = month;
        this.year = year;
    }

    @Override
    public String getContents()
    {
        StringBuilder builder = new StringBuilder(contentType.toString()).append(':');

        switch (contentType)
        {
            case DATE:
                builder.append(day).append('-').append(month).append('-').append(year);
                break;
            case TIME:
                builder.append(minute).append('-').append(hour);
                break;
        }

        return builder.toString();
    }

    @Override
    public String toString()
    {
        return "Message{" + "contentType=" + contentType + (contentType == ContentType.TIME ? ", minute=" + minute + ", hour=" + hour : ", day=" + day + ", month=" + month + ", year=" + year ) + '}';
    }
}
