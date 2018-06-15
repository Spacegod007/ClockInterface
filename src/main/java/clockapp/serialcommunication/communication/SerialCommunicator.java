package clockapp.serialcommunication.communication;

import clockapp.logic.messages.ICommunicationManager;
import clockapp.logic.models.ClockDateTimeValue;
import clockapp.serialcommunication.MissingSerialPortException;
import gnu.io.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.TooManyListenersException;
import java.util.logging.Level;
import java.util.logging.Logger;

//todo make modular for multiple classes
//todo improve algorithm
//todo improve code quality

@Component
public class SerialCommunicator implements SerialPortEventListener
{
    private static final Logger LOGGER = Logger.getLogger(SerialCommunicator.class.getName());

    private static final int TIMEOUT = 2000;

    private static final int START_MESSAGE_BYTE_1 = 240;
    private static final int START_MESSAGE_BYTE_2 = 15;
    private static final int END_MESSAGE = 90;

    private final ICommunicationManager manager;

    private SerialPort serialPort;
    private InputStream inputStream;
    private OutputStream outputStream;

    private int receivedMinute = 0;
    private int receivedHour = 0;

    public SerialCommunicator(ICommunicationManager manager)
    {
        LOGGER.log(Level.INFO, "Initialising communication communication");
        this.manager = manager;

        try
        {
            openCommunication(getSerialPort());
            initialiseStreams();
            initialiseListener();

            LOGGER.log(Level.INFO, "Done initialising communication communication");
        }
        catch (MissingSerialPortException e)
        {
            LOGGER.log(Level.SEVERE, "Missing communication port to communicate with", e);
            LOGGER.log(Level.SEVERE, "Failed to initialise communication communication");
            System.exit(-1);
        }
    }

    /**
     * Obtains the local serial port and tries to connect to it
     * @return The port identifier
     * @throws MissingSerialPortException if the port could not be found
     */
    private CommPortIdentifier getSerialPort() throws MissingSerialPortException
    {
        Enumeration portIdentifiers = CommPortIdentifier.getPortIdentifiers();

        while (portIdentifiers.hasMoreElements())
        {
            CommPortIdentifier currentPort = (CommPortIdentifier) portIdentifiers.nextElement();

            if (currentPort.getPortType() == CommPortIdentifier.PORT_SERIAL)
            {
                return currentPort;
            }
        }

        throw new MissingSerialPortException("Missing port for communication communication");
    }

    /**
     * Opens communication with the connected device
     * (in this case the clock)
     * @param commPortIdentifier The identifier of the port to connect through
     */
    private void openCommunication(CommPortIdentifier commPortIdentifier)
    {
        try
        {
            serialPort = (SerialPort) commPortIdentifier.open("clock", TIMEOUT);
        }
        catch (PortInUseException e)
        {
            LOGGER.log(Level.SEVERE, "Port is already in use", e);
        }
        catch (Exception e)
        {
            LOGGER.log(Level.SEVERE, "Failed to initialise port connection", e);
        }
    }

    /**
     * Initialises the data streams so they can be read when data is being transported
     */
    private void initialiseStreams()
    {
        try
        {
            inputStream = serialPort.getInputStream();
            outputStream = serialPort.getOutputStream();
        }
        catch (IOException e)
        {
            LOGGER.log(Level.SEVERE, "Something went wrong setting up one of the communication communication streams", e);
        }
    }

    /**
     * Initialises the event listener which gets triggered when an incoming dataStream is detected
     */
    private void initialiseListener()
    {
        try
        {
            serialPort.addEventListener(this);
            serialPort.notifyOnDataAvailable(true);
        }
        catch (TooManyListenersException e)
        {
            LOGGER.log(Level.SEVERE, "Too many listeners", e);
        }
    }

    /**
     * Disconnects the system of the commPort
     */
    private void disconnect()
    {
        try
        {
            serialPort.removeEventListener();

            serialPort.close();
            outputStream.close();
            inputStream.close();

        }
        catch (IOException e)
        {
            LOGGER.log(Level.SEVERE, "Something went wrong while disconnecting from the communication communication");
        }
    }

    /**
     * Writes data over the commPort
     */
    public void writeData(byte[] send)
    {
        try
        {
            outputStream.write(send);
            outputStream.flush();
        }
        catch (IOException e)
        {
            LOGGER.log(Level.SEVERE, "Something went wrong while writing serial data", e);
        }
    }

    /**
     * An event which gets triggered when an incoming message is detected
     * @param serialPortEvent the event which contains all information needed to obtain the data
     */
    @Override
    public void serialEvent(SerialPortEvent serialPortEvent)
    {
        if (serialPortEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE)
        {
            try
            {
                manager.setLatestValue(translateData(getData(inputStream)));
            }
            catch (IOException e)
            {
                LOGGER.log(Level.SEVERE, "Something went wrong while reading incoming data", e);
            }
            catch (Exception e)
            {
                LOGGER.log(Level.SEVERE, "Something went wrong while interpreting the message", e);
            }
        }
    }

    /**
     * Gets the data from an incoming dataStream
     * @param inputStream the incoming dataStream
     * @return a list of integers which symbolise the bytes that were read from the stream
     * @throws IOException if something goes wrong while reading the dataStream
     */
    private List<Integer> getData(InputStream inputStream) throws IOException
    {
        List<Integer> messageContents = new ArrayList<>();

        int currentValue = inputStream.read();
        while (currentValue != -1)
        {
            messageContents.add(currentValue);
            if (currentValue == END_MESSAGE)
            {
                break;
            }
            currentValue = inputStream.read();
        }

        return messageContents;
    }

    /**
     * Translates the data to an object
     * @param data a list of numbers representing the raw data
     * @return An Object containing the date and time or null depending if the date or time got send
     */
    private ClockDateTimeValue translateData(List<Integer> data)
    {
        if (!(data.get(0) == START_MESSAGE_BYTE_1 && data.get(1) == START_MESSAGE_BYTE_2 && data.get(data.size() - 1) == END_MESSAGE))
        {
            throw new IllegalArgumentException("incomplete message received");
        }

        trimExcessData(data);

        try
        {
            switch (CommandByte.valueOf(data.get(0)))
            {
                case DATE_CHANGED:
                    return new ClockDateTimeValue(receivedMinute, receivedHour, data.get(2), data.get(3), data.get(4));
                case TIME_CHANGED:
                    receivedMinute = data.get(2);
                    receivedHour = data.get(3);
                    break;
            }
        }
        catch (IllegalArgumentException e)
        {
            LOGGER.log(Level.WARNING, "Received message of clock with invalid commandByte", e);
        }

        return null;
    }

    private void trimExcessData(List<Integer> data)
    {
        data.remove(0);
        data.remove(0);
        data.remove(data.size() - 1);
    }
}
