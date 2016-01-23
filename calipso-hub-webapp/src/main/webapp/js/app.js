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

define([
  'jquery', 'underscore', 'underscore-inflection', 'backbone', 'bootstrap',
  'calipso', 'backbone', 'modules-config', 'routers/MainRouter' ],
function(
	$, _, _inflection, Backbone, bootstrap,
	Calipso, Backbone, modulesConfig, MainRouter) {


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
	};
	
	// initialize/configure application 
	Calipso.initializeApp({
		contextPath: "calipso/",
	});


	//////////////////////////////////
	// intercept links
	//////////////////////////////////
	$(document).on("click", "a", function(event) {

		var $a = $(this);
		var href = $a.attr("href");

		if (href && href.match(/^\/.*/) && !$(this).attr("target")) {
			Calipso.stopEvent(event);
			
			// Close dropdown menus (desktop) or nav collapse (mobile)
			// get up to 5th level parent
			var ancestorScope = $a;
			for(i=0; i < 5 && ancestorScope.parentNode; i++){
				ancestorScope = ancestorScope.parentNode;
			}
			// mobile, collapse hide
			if ($(window).width() < 768) {
				$a.parentsUntil(ancestorScope, ".navbar-collapse:first").collapse('hide');
			}
			// desktop-ish, close dropdown
			else{
				$a.parentsUntil(ancestorScope, ".dropdown:first").removeClass('open');
			}

			Backbone.history.navigate(href, true);
		}
	});

	//////////////////////////////////
	// Bootstrap: enable tooltips
	//////////////////////////////////
	$(document).ready(function() {
		$(document.body).tooltip({
			selector : "[data-toggle=tooltip]",
			html : true
		});

	});
    //////////////////////////////////
    // Use POST instead of PUT/PATCH/DELETE
    //////////////////////////////////
    Backbone.emulateHTTP = true;

	//////////////////////////////////
	// Start the app
	//////////////////////////////////
	var options = {};
	options.routers = {};
	options.routers.main = MainRouter;
	options.menu = [ {
		label: "Users",
		url: "users"
	},{
		label: "Hosts",
		url: "hosts"
	} ];
    
	Calipso.app.start(options);
});
