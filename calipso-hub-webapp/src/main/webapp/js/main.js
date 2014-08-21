require([
// Libraries
'jquery', 'underscore', 'underscore-inflection', 'backbone', 'bootstrap',
// Main App Object
'app',
// Application routers
'routers/MainRouter' ],

function(
// Libraries
$, _, _inflection, Backbone, bootstrap,
// Main App Object
CalipsoApp,
// Application routers
MainRouter) {

	function createNamedConstructor(name, constructor) {

		var fn = new Function('constructor', 'return function ' + name + '()\n' + '{\n' + '    // wrapper function created dynamically for "' + name + '" constructor to allow instances to be identified in the debugger\n' + '    constructor.apply(this, arguments);\n' + '};');
		return fn(constructor);
	}

	// set up named constructors
	var originalExtend = Backbone.View.extend; // Model, Collection, Router and View shared the same extend function
	var nameProp = 'getTypeName';
	var newExtend = function(protoProps, classProps) {
		if (protoProps && protoProps.hasOwnProperty(nameProp)) {
			// TODO - check that name is a valid identifier
			var name = protoProps[nameProp]();
			// wrap constructor from protoProps if supplied or 'this' (the function we are extending)
			var constructor = protoProps.hasOwnProperty('constructor') ? protoProps.constructor : this;
			protoProps = _.extend(protoProps, {
				constructor : createNamedConstructor(name, constructor)
			});
		}
		return originalExtend.call(this, protoProps, classProps);
	};

	Backbone.Model.extend = Backbone.Collection.extend = Backbone.Router.extend = Backbone.View.extend = newExtend;

	//////////////////////////////////
	// intercept links
	//////////////////////////////////
	$(document).on("click", "a", function(event) {
		var href = $(this).attr("href");
		console.log("Cought link: " + href);
		// if (href && href.match(/^\/.*/) && $(this).attr("target") !==
		// "_blank") {
		if (href && href.indexOf("/") != 0 && !event.altKey && !event.ctrlKey && !event.metaKey && !event.shiftKey) {
			event.preventDefault();
			console.log("stopped link: " + href);
			Backbone.history.navigate(href, true);
		}
	});

	/*
	# Globally capture clicks. If they are internal and not in the pass 
	# through list, route them through Backbone's navigate method.
	$(document).on "click", "a[href^='/']", (event) ->

	href = $(event.currentTarget).attr('href')

	# chain 'or's for other black list routes
	passThrough = href.indexOf('sign_out') >= 0

	# Allow shift+click for new tabs, etc.
	if !passThrough && !event.altKey && !event.ctrlKey && !event.metaKey && !event.shiftKey
	event.preventDefault()

	# Remove leading slashes and hash bangs (backward compatablility)
	url = href.replace(/^\//,'').replace('\#\!\/','')

	# Instruct Backbone to trigger routing events
	App.router.navigate url, { trigger: true }

	return false

	 */
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
	// SB Admin 2 js
	//////////////////////////////////
	$(function() {

		$('#side-menu').metisMenu();

	});

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

	CalipsoApp.start(options);

});
