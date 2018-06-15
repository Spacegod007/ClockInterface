package clockapp.logic.messages;

import clockapp.logic.models.ClockDateTimeValue;

public interface IRetrievableMessageManager
{
    ClockDateTimeValue getLatestMessage();
}
