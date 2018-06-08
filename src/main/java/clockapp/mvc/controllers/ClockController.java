package clockapp.mvc.controllers;

import clockapp.logic.messages.IRetrievableMessageManager;
import clockapp.logic.models.Message;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ClockController
{
    private final IRetrievableMessageManager manager;

    public ClockController(IRetrievableMessageManager manager)
    {
        this.manager = manager;
    }

    @RequestMapping(value = "/getLatestMessage", method = RequestMethod.POST)
    public Message getLatestMessage()
    {
        return manager.getLatestMessage();
    }


}
