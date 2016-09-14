package gr.abiss.calipso.websocket.service.impl;


import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import gr.abiss.calipso.tiers.service.AbstractModelServiceImpl;
import gr.abiss.calipso.websocket.model.StompSession;
import gr.abiss.calipso.websocket.repository.StompSessionRepository;
import gr.abiss.calipso.websocket.service.StompSessionService;


@Named(StompSessionService.BEAN_ID)
@Transactional(readOnly = true)
public class StompSessionServiceImpl extends AbstractModelServiceImpl<StompSession, String, StompSessionRepository> implements StompSessionService {


	private static final Logger LOGGER = LoggerFactory.getLogger(StompSessionServiceImpl.class);
	
}

	

