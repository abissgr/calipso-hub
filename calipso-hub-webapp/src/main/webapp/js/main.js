require([
  // Libraries
  'jquery', 'underscore', 'underscore-inflection', 'backbone',
  'bootstrap',
  // Main App Object
  'app',
  // Application routers
  'routers/MainRouter'
],

function (
		  // Libraries
			$, _, _inflection, Backbone, bootstrap, 
			// Main App Object
		  CalipsoApp,
		  // Application routers
		  MainRouter
		  ) {

	    function createNamedConstructor(name, constructor) {

	        var fn = new Function('constructor', 'return function ' + name + '()\n'
	            + '{\n'
	            + '    // wrapper function created dynamically for "' + name + '" constructor to allow instances to be identified in the debugger\n'
	            + '    constructor.apply(this, arguments);\n'
	            + '};');
	        return fn(constructor);
	    }

	    // set up named constructors
	    var originalExtend = Backbone.View.extend; // Model, Collection, Router and View shared the same extend function
	    var nameProp = 'getTypeName';
	    var newExtend = function (protoProps, classProps) {
	        if (protoProps && protoProps.hasOwnProperty(nameProp)) {
	            // TODO - check that name is a valid identifier
	            var name = protoProps[nameProp]();
	            // wrap constructor from protoProps if supplied or 'this' (the function we are extending)
	            var constructor = protoProps.hasOwnProperty('constructor') ? protoProps.constructor : this;
	            protoProps = _.extend(protoProps, {
	                constructor: createNamedConstructor(name, constructor)
	            });
	        }
	        return originalExtend.call(this, protoProps, classProps);
	    };

	    Backbone.Model.extend = Backbone.Collection.extend = Backbone.Router.extend = Backbone.View.extend = newExtend;

	 	//////////////////////////////////
	 	// intercept links
	 	//////////////////////////////////
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
	//////////////////////////////////
	// Bootstrap: enable tooltips
	//////////////////////////////////
	$(document).ready(function () {
		$(document.body).tooltip({ selector: "[data-toggle=tooltip]", html : true });
	    
	});
	//////////////////////////////////
	// SB Admin 2 js
	//////////////////////////////////
	$(function() {

	    $('#side-menu').metisMenu();

	});

	//Loads the correct sidebar on window load,
	//collapses the sidebar on window resize.
	$(function() {
	    $(window).bind("load resize", function() {
	        width = (this.window.innerWidth > 0) ? this.window.innerWidth : this.screen.width;
	        if (width < 768) {
	            $('div.sidebar-collapse').addClass('collapse')
	        } else {
	            $('div.sidebar-collapse').removeClass('collapse')
	        }
	    })
	})

	//////////////////////////////////
	// Start the app
	//////////////////////////////////
  var options = {};
  options.routers = {};
  options.routers.main = MainRouter;
  
  CalipsoApp.start(options);

});
