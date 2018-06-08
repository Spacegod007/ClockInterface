package clockapp.logic.messages;

import clockapp.logic.models.Message;
import org.springframework.stereotype.Component;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

//todo add observer pattern functionality over web

@Component
public class MessagesManager implements ICommunicationManager, IRetrievableMessageManager
{
    private static final Logger LOGGER = Logger.getLogger(MessagesManager.class.getName());

    private final BlockingQueue<Message> messages;
    private final Object sync;

    public MessagesManager()
    {
        sync = new Object();
        messages = new ArrayBlockingQueue<>(16);
    }

    @Override
    public void addMessage(Message message)
    {
        synchronized (sync)
        {
            try
            {
                messages.put(message);
                System.out.println(message);
            }
            catch (InterruptedException e)
            {
                LOGGER.log(Level.SEVERE, "Interrupted while waiting to put message", e);
            }

            //todo inform subscribers
        }
    }

    @Override
    public Message getLatestMessage()
    {
        synchronized (sync)
        {
            return messages.poll();
        }
    }
}
