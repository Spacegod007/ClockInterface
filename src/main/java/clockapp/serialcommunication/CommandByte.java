package clockapp.serialcommunication;

public enum CommandByte
{
    DATE_CHANGED(12),
    TIME_CHANGED(13);

    private final int value;

    CommandByte(int value)
    {
        this.value = value;
    }

    public int getValue()
    {
        return value;
    }

    public static CommandByte valueOf(int value)
    {
        if (value < 0 || value > 255)
        {
            throw new ArrayIndexOutOfBoundsException("Invalid commandByte supplied, lowest = 0, highest = 255");
        }

        for (CommandByte commandByte : CommandByte.values())
        {
            if (commandByte.value == value)
            {
                return commandByte;
            }
        }

        throw new IllegalArgumentException("Invalid commandByte supplied");
    }
}
