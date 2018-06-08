package clockapp.logic.messages;

import clockapp.logic.models.Message;

public interface IRetrievableMessageManager
{
    Message getLatestMessage();
}
