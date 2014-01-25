define(['router/app-router'], function(AppRouter) {

	function Calipso(){}
	Calipso.prototype.getBaseUrl = function() {
		if (document.getElementById("fforaMainScript")) { // host page
			var basePathEnd = document.getElementById("fforaMainScript").src.indexOf("/js/lib/require.js");
			return document.getElementById("fforaMainScript").src.substring(0, basePathEnd);
		} else { // manager area
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
	
	
});