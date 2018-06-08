package clockapp.serialcommunication;

import clockapp.logic.messages.ICommunicationManager;
import com.pi4j.wiringpi.Serial;
import org.springframework.stereotype.Component;

import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class SerialManager
{
    private static final Logger LOGGER = Logger.getLogger(SerialManager.class.getName());
    private static final int BAUD_RATE = 9600;

    private final ICommunicationManager communicationLogic;

    public SerialManager(ICommunicationManager communicationLogic)
    {
        this.communicationLogic = communicationLogic;
    }

    public void readSerial()
    {
        int serialPort = Serial.serialOpen(Serial.DEFAULT_COM_PORT, BAUD_RATE);

        if (serialPort == -1)
        {
            LOGGER.log(Level.WARNING, "Port is closed");
            return;
        }

        while (Serial.serialDataAvail(serialPort) > 0)
        {
            decryptMessage(Serial.serialGetByte(serialPort));
        }

        Serial.serialClose(serialPort);
    }

    private void decryptMessage(byte input)
    {
        int i = input;
        System.out.println(i);
    }
}
