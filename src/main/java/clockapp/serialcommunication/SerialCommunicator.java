package clockapp.serialcommunication;

import gnu.io.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.TooManyListenersException;
import java.util.logging.Level;
import java.util.logging.Logger;

//todo make modular for multiple classes
//todo improve algorithm
//todo improve code quality

@Component
public class SerialCommunicator implements SerialPortEventListener
{
    public static void main(String[] args)
    {
        new SerialCommunicator();
    }
    private static final Logger LOGGER = Logger.getLogger(SerialCommunicator.class.getName());

    private static final int TIMEOUT = 2000;

    private SerialPort serialPort;
    private InputStream inputStream;
    private OutputStream outputStream;


    private boolean isConnected = false;

    public SerialCommunicator()
    {
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
                int value = inputStream.read();
                while (value != -1)
                {
                    System.out.println(value);
                    value = inputStream.read();
                }

                //todo do something with incoming data
            }
            catch (IOException e)
            {
                LOGGER.log(Level.SEVERE, "Something went wrong while reading incoming data");
            }
        }
    }
}
