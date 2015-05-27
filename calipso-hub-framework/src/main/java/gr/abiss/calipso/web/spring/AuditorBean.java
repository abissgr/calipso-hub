package gr.abiss.calipso.web.spring;

import javax.inject.Named;

import gr.abiss.calipso.model.User;
import gr.abiss.calipso.userDetails.util.SecurityUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.AuditorAware;

@Named(value="auditorBean")
public class AuditorBean implements AuditorAware<User> {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(AuditorBean.class);

	private User currentAuditor;

	@Override
	public User getCurrentAuditor() {
		if(currentAuditor == null){
			currentAuditor = (User) SecurityUtil.getPrincipal().getUser();
		}
		else{
			LOGGER.debug("getCurrentAuditor returns cached result");
		}
		return currentAuditor;
	}

	public void setCurrentAuditor(User currentAuditor) {
		this.currentAuditor = currentAuditor;
	}

}