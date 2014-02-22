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
define(['router/app-router'], function(AppRouter) {

	function Calipso(){}
	Calipso.prototype.getBaseUrl = function() {
		var calipsoMainScript = document.getElementById("calipso-script-main");
		// calipso in host page
		if (calipsoMainScript) { 
			var basePathEnd = calipsoMainScript.src.indexOf("/js/lib/require.js");
			return calipsoMainScript.src.substring(0, basePathEnd);
		} else {
			// calipso client
			return window.location.protocol + "//" + window.location.host;
		}
	};
	calipso = new Calipso();
	console.log("Calipso base URL: "+window.calipso.getBaseUrl());
	calipso["app"] = new AppRouter();
	Calipso._router = calipso["app"];
	
	$.ajaxSetup({
	    statusCode: {
	        401: function(){
	            // Unauthorized 
	            window.location.replace(window.calipso.getBaseUrl() + '/client/login');
	         
	        },
	        403: function() {
	            // Access denied
	            window.location.replace(window.calipso.getBaseUrl() + '/client/denied');
	        }
	    }
	});
});