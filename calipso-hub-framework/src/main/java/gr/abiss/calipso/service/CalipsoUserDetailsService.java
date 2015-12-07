package gr.abiss.calipso.service;

import gr.abiss.calipso.userDetails.service.impl.UserDetailsServiceImpl;

import javax.inject.Named;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Named("userDetailsService")
@Transactional(readOnly = true)
public class CalipsoUserDetailsService extends UserDetailsServiceImpl {
// no need to override anything 
}
