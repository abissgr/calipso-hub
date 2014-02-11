define(['router/app-router'], function(AppRouter) {

	function Calipso(){}
	Calipso.prototype.getBaseUrl = function() {
		var calipsoMainScript = document.getElementById("calipso-script-main");
		if (calipsoMainScript) { // host page
			var basePathEnd = calipsoMainScript.src.indexOf("/js/lib/require.js");
			return calipsoMainScript.src.substring(0, basePathEnd);
		} else {
			return window.location.protocol + "//" + window.location.host;
		}
	};
	calipso = new Calipso();
	console.log("Calipso.prototype.getBaseUrl: "+window.calipso.getBaseUrl());
	calipso["app"] = new AppRouter();
	Calipso._router = calipso["app"];
	
	$.ajaxSetup({
	    statusCode: {
	        401: function(){
	            // Redirec the to the login page.
	            window.location.replace('/#login');
	         
	        },
	        403: function() {
	            // 403 -- Access denied
	            window.location.replace('/#denied');
	        }
	    }
	});
	
	console.log("app.js done");
	
});