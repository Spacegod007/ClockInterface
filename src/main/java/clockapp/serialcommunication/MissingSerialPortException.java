package clockapp.serialcommunication;

public class MissingSerialPortException extends Exception
{
    public MissingSerialPortException(String message)
    {
        super(message);
    }

    public MissingSerialPortException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
