package gr.abiss.calipso.friends.binding;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;

import gr.abiss.calipso.friends.model.FriendshipId;

@ControllerAdvice
public class WebBindingInitializer {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(WebBindingInitializer.class);
	
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(FriendshipId.class, new FriendshipIdPropertyEditor());
    }
}
