/*
 * Copyright (c) 2007 - 2013 www.Abiss.gr
 *
 * This file is part of Calipso, a software platform by www.Abiss.gr.
 *
 * Calipso is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Calipso is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Calipso. If not, see http://www.gnu.org/licenses/agpl.html
 */

define([ 'calipso', 'modules-config'],
	function( Calipso, modulesConfig) {


	//////////////////////////////////
	// Global backbone error handling
	//////////////////////////////////
	Backbone.ajax = function() {
      // Invoke $.ajaxSetup in the context of Backbone.$
      Backbone.$.ajaxSetup.call(Backbone.$, {
      	// use traditional HTTP params
      	traditional: true,
      	// handle status codes 
         statusCode: {
              401: function(){
        			console.log("Backbone.$.ajaxSetup 401");
                  // Redirect the to the login page.
                 Backbone.history.navigate(Calipso.getConfigProperty("contextPath") + "client/login", true);
              },
              403: function() {
          			console.log("Backbone.$.ajaxSetup 403");
                  // 403 -- Access denied
                 Backbone.history.navigate(Calipso.getConfigProperty("contextPath") + "client/login", true);
              }
	      
        }
      });
      return Backbone.$.ajax.apply(Backbone.$, arguments);
	}
	
	
	// initialize/configure application
	Calipso.initializeApp({
		contextPath: "calipso/",
	});
  
  return Calipso.app;});
