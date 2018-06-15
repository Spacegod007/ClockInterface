package clockapp.logic.messages;


import clockapp.logic.models.ClockDateTimeValue;

public interface ICommunicationManager
{
    void setLatestValue(ClockDateTimeValue clockDateTimeValue);
}
