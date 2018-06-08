package clockapp.serialcommunication;

import clockapp.logic.messages.ICommunicationManager;
import clockapp.logic.models.Message;
import gnu.io.*;
import org.springframework.stereotype.Component;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

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


    private boolean isConnected = false;

    public SerialCommunicator(ICommunicationManager manager)
    {
        this.manager = manager;

        try
        {
            openCommunication(getSerialPort());
            initialiseStreams();
            initialiseListener();
        }
        catch (MissingSerialPortException e)
        {
            LOGGER.log(Level.SEVERE, "Missing serial port to communicate with", e);
        }
    }

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

        throw new MissingSerialPortException("Missing port for serial communication");
    }

    private void openCommunication(CommPortIdentifier commPortIdentifier)
    {
        try
        {
            serialPort = (SerialPort) commPortIdentifier.open("clock", TIMEOUT);
            isConnected = true;
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

    private boolean initialiseStreams()
    {
        try
        {
            inputStream = serialPort.getInputStream();
            outputStream = serialPort.getOutputStream();
            return true;
        }
        catch (IOException e)
        {
            LOGGER.log(Level.SEVERE, "Something went wrong setting up one of the serial communication streams", e);
            return false;
        }
    }

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

    private void disconnect()
    {
        try
        {
            serialPort.removeEventListener();

            serialPort.close();
            outputStream.close();
            inputStream.close();

            isConnected = false;
        }
        catch (IOException e)
        {
            LOGGER.log(Level.SEVERE, "Something went wrong while disconnecting from the serial communication");
        }
    }

    public void writeData() //todo add parameters for sending data
    {

    }

    @Override
    public void serialEvent(SerialPortEvent serialPortEvent)
    {
        if (serialPortEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE)
        {
            try
            {
                manager.addMessage(translateData(getData(inputStream)));
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

    private Message translateData(List<Integer> data)
    {
        if (!(data.get(0) == START_MESSAGE_BYTE_1 && data.get(1) == START_MESSAGE_BYTE_2 && data.get(data.size()) == END_MESSAGE))
        {
            throw new IllegalArgumentException("incomplete message received");
        }

        CommandByte commandByte = CommandByte.valueOf(data.get(0));

        switch (commandByte)
        {
            case DATE_CHANGED:
                return new Message(data.get(1), data.get(2), data.get(3));
            case TIME_CHANGED:
                return new Message(data.get(1), data.get(2));
            default:
                throw new NotImplementedException();
        }
    }
}
