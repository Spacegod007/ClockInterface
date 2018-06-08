package clockapp.logic.messages;


import clockapp.logic.models.IReadableMessage;

public interface ICommunicationManager
{
    void addMessage(IReadableMessage message);
}
