require([
  // Libraries
  'jquery', 'underscore', 'backbone',
  'bootstrap',
  // Main App Object
  'app',
  // Auth module 
  'auth',
  // Application routers
  'routers/MainRouter',
  //'routers/UserRouter'
],

function (
		  // Libraries
			$, _, Backbone, bootstrap, 
			// Main App Object
		  App,
		  // Auth module 
		  auth,
		  // Application routers
		  MainRouter
		  //, UserRouter
		  ) {

  // Navigation handler
	$(document).on("click",  "a", function(event) {
		var href = $(this).attr("href");
		console.log("Cought link: " + href);
		// if (href && href.match(/^\/.*/) && $(this).attr("target") !==
		// "_blank") {
		if (href && href.indexOf("/") != 0 && !event.altKey && !event.ctrlKey && !event.metaKey
				&& !event.shiftKey) {
			event.preventDefault();
			console.log("stopped link: " + href);
			Backbone.history.navigate(href, true);
		}
	});
//	
//	$('#parent').on('click', 'a', function(event) {
//		//var href = $(this).attr("href");
//		//console.log("Cought link: " + href);
//		event.preventDefault();
//		// do the rest of your stuff
//	});
//

  // Start the app
  var options = {
    routers: {
      main   : MainRouter,
      //user   : UserRouter
    }
  };
  
  App.start(options);

});
