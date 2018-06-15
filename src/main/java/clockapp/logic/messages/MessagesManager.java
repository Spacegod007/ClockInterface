package clockapp.logic.messages;

import clockapp.logic.models.ClockDateTimeValue;
import org.springframework.stereotype.Component;

import java.util.logging.Level;
import java.util.logging.Logger;

//todo add observer pattern functionality over web

@Component
public class MessagesManager implements ICommunicationManager, IRetrievableMessageManager
{
    private static final Logger LOGGER = Logger.getLogger(MessagesManager.class.getName());

    private ClockDateTimeValue latestValue;
    private final Object sync;

    public MessagesManager()
    {
        sync = new Object();
        latestValue = null;
    }

    @Override
    public void setLatestValue(ClockDateTimeValue clockDateTimeValue)
    {
        if (clockDateTimeValue == null)
        {
            return;
        }

        LOGGER.log(Level.INFO, "Setting new clock value to: " + clockDateTimeValue);

        synchronized (sync)
        {
            latestValue = clockDateTimeValue;
            System.out.println(clockDateTimeValue);

            //todo inform subscribers
        }
    }

    @Override
    public ClockDateTimeValue getLatestMessage()
    {
        synchronized (sync)
        {
            return latestValue;
        }
    }
}
