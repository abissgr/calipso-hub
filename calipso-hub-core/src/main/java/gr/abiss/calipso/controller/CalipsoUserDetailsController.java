package gr.abiss.calipso.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import gr.abiss.calipso.userDetails.controller.UserDetailsController;

//@Controller
@RequestMapping(value = "/api-auth", produces = { "application/json", "application/xml" })
public class CalipsoUserDetailsController extends UserDetailsController {

         // no need to override anything unless you wish to change 
         // the default method RequestMappings/URLs
 }