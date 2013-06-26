package gr.abiss.calipso.service.impl;

import gr.abiss.calipso.model.User;
import gr.abiss.calipso.repository.UserRepository;

import java.io.Serializable;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.resthub.common.service.CrudServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.security.core.context.SecurityContextHolder;


public abstract class AbstractServiceImpl<T, ID extends Serializable, R extends PagingAndSortingRepository<T, ID>> extends
		CrudServiceImpl<T, ID, R> {
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractServiceImpl.class);


	protected UserRepository userRepository;

	@Inject
	public void setUserRepository(UserRepository userRepository) {
		this.userRepository = userRepository;
	}
	
	protected User getPrincipal() {
		Object principal = null;
		if (SecurityContextHolder.getContext() != null && SecurityContextHolder.getContext().getAuthentication() != null) {
			principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		}
		User user = null;
		if(principal != null 
				&& principal instanceof org.springframework.security.core.userdetails.User){
			String username = ((org.springframework.security.core.userdetails.User) principal).getUsername();
			if(StringUtils.isNotBlank(username) && !"anonymous".equals(username)){
				user = userRepository.findByUserNameOrEmail(username);
			}
		}
		return user;
	}

}