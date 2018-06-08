package clockapp.logic.messages;

import clockapp.logic.models.IReadableMessage;
import org.springframework.stereotype.Component;

import java.util.PriorityQueue;
import java.util.Queue;

@Component
public class MessagesManager implements ICommunicationManager, IRetrievableMessageManager
{
    private final Queue<IReadableMessage> messages;
    private final Object sync;

    public MessagesManager()
    {
        sync = new Object();
        messages = new PriorityQueue<>();
    }

    @Override
    public void addMessage(IReadableMessage message)
    {
        synchronized (sync)
        {
            messages.add(message);
        }
    }

    @Override
    public String getLatestMessage()
    {
        synchronized (sync)
        {
            return messages.poll().getContents();
        }
    }
}