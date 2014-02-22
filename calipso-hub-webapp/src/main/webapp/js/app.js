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
	
	console.log("app.js done");
	
});