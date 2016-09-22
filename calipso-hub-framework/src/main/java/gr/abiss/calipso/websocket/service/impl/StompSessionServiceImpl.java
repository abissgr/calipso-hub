package gr.abiss.calipso.websocket.service.impl;


import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;

import gr.abiss.calipso.model.User;
import gr.abiss.calipso.tiers.service.AbstractModelServiceImpl;
import gr.abiss.calipso.userDetails.model.ICalipsoUserDetails;
import gr.abiss.calipso.websocket.model.StompSession;
import gr.abiss.calipso.websocket.repository.StompSessionRepository;
import gr.abiss.calipso.websocket.service.StompSessionService;


@Named(StompSessionService.BEAN_ID)
@Transactional(readOnly = true)
public class StompSessionServiceImpl extends AbstractModelServiceImpl<StompSession, String, StompSessionRepository> implements StompSessionService {


	private static final Logger LOGGER = LoggerFactory.getLogger(StompSessionServiceImpl.class);

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly = false)
	@PreAuthorize(StompSession.PRE_AUTHORIZE_CREATE)
	public StompSession create(StompSession resource) {
		validateUser(resource);
		resource = super.create(resource);
		this.repository.addUserStompSession(resource.getUser().getInactivationReason());
		return resource;
	}


	@Override
	@Transactional(readOnly = false)
	@PreAuthorize(StompSession.PRE_AUTHORIZE_UPDATE)
	public StompSession update(StompSession resource) {
		validateUser(resource);
		return super.update(resource);
	}
	
    /**
     * {@inheritDoc}
     */
	@Override
	@Transactional(readOnly = false)
	@PreAuthorize(StompSession.PRE_AUTHORIZE_DELETE)
	public void delete(@P("resource") StompSession resource) {
		// TODO Auto-generated method stub
		super.delete(resource);
		this.repository.removeUserStompSession(resource.getUser().getInactivationReason());
	}

    /**
     * {@inheritDoc}
     */
	@Override
	@Transactional(readOnly = false)
	@PreAuthorize(StompSession.PRE_AUTHORIZE_DELETE)
	public void delete(String id) {
		if(this.repository.exists(id)){
			this.delete(this.repository.findOne(id));
		}
	}


	public void validateUser(StompSession resource) {
		ICalipsoUserDetails ud = this.getPrincipal();
		LOGGER.info("userDetails: {}", ud);
		if(resource.getUser() == null){
			resource.setUser(new User(ud.getId()));
		}
		else if(!ud.getId().equals(resource.getUser().getId())){
			throw new IllegalArgumentException("Session user does not match current principal");
		}
	}
	
}

	

