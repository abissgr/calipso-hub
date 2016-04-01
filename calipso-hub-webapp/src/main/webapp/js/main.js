// set console api if missing
if(!window.console) {console={}; console.log = function(){};}
//establish a locale
function initLocale(){
	// get "remembered" locale if exists
	var locale = localStorage.getItem('locale');
	
	// guess and set remembered otherwise
	if(!locale && navigator){
		var browserLanguagePropertyKeys = ['languages', 'language', 'browserLanguage', 'userLanguage', 'systemLanguage'];
		var prop;
		for (var i = 0; !locale && i < browserLanguagePropertyKeys.length; i++) {
		    prop = navigator[browserLanguagePropertyKeys[i]];
		    // pick first if array
		    if(prop && prop.constructor === Array){
		   	 prop = prop[0];
		    }
		    // no wildcards
		    if(prop && prop != "*"){
		   	  // set it
		   	 locale = prop;
		    }
		}

		if(!locale){
			locale = "en";
		}
	  	 // no sub-locale
		else if(locale.length > 2){
			locale = locale.substring(0,2);
	  	}
		localStorage.setItem('locale', locale.toLowerCase());
	}
}
initLocale();


require.config({
	// main.js is the application entry point
	deps : [ 'main' ],
	waitSeconds: 0,

	paths : {
		'cookie' : 'lib/jquery.cookie',
		'jquery' : 'lib/jquery',
		'jquery-color' : 'lib/jquery.color.plus-names',
		"intlTelInput" : 'lib/intlTelInput',
		"intlTelInputUtil" : 'lib/intlTelInputUtil',
		'q' : 'lib/q',
		'underscore' : 'lib/underscore',
		'underscore-string' : 'lib/underscore-string',
		'underscore-inflection' : 'lib/underscore-inflection',
		'backbone' : 'lib/backbone',
		'resthub' : 'lib/resthub/resthub',
		'localstorage' : 'lib/localstorage',
		'text' : 'lib/text',
		'i18n' : 'lib/i18n',
		'pubsub' : 'lib/resthub/pubsub',
		'bootstrap' : 'lib/bootstrap',
		'backbone-validation-orig' : 'lib/backbone-validation',
		'backbone-bootstrap-modal' : 'lib/backbone-bootstrap-modal',
		'backbone-forms' : 'lib/backbone-forms',
		'backbone-forms-bootstrap3' : 'lib/backbone-forms-bootstrap3',
		'bootstrap-datetimepicker' : 'lib/bootstrap-datetimepicker',
		'bootstrap-markdown' : 'lib/bootstrap-markdown',
		'bootstrap-switch' : 'lib/bootstrap-switch',
		'backbone-validation' : 'lib/resthub/backbone-validation-ext',
		'marionette' : 'lib/backbone.marionette',
		'handlebars-orig' : 'lib/handlebars',
		'handlebars' : 'lib/resthub/handlebars-helpers',
		'backbone-queryparams' : 'lib/backbone-queryparams',
		'backbone.paginator': 'lib/backbone.paginator',
		'async' : 'lib/async',
		'keymaster' : 'lib/keymaster',
		'moment' : 'lib/moment',
		'json2' : 'lib/json2',
		'console' : 'lib/resthub/console', 
		'backgrid' : 'lib/backgrid/backgrid',
		"backgrid-paginator" : 'lib/backgrid/extensions/paginator/backgrid-paginator',
		"backgrid-moment" : 'lib/backgrid/extensions/moment/backgrid-moment-cell',
		"backgrid-text" : 'lib/backgrid/extensions/text/backgrid-text-cell',
		'backgrid-responsive-grid' : 'lib/backgrid/extensions/responsive-grid/responsive-grid',
		"calipso" : 'lib/calipso',
		"calipso-hbs" : 'lib/calipso-hbs',
		// Mocha testing
		'mocha' : 'lib/mocha/mocha',
		'chai' : 'lib/chai/chai',
		//'chai-jquery' : 'lib/chai/chai-jquery',
		'sinon' : 'lib/sinon/chai-jquery',
		'calendar' : 'lib/calendar',
      'select2' : 'lib/select2',
      'raty' : 'lib/jquery.raty-fa',
      'bootstrap-fileInput' : 'lib/fileinput',
      'backbone-forms-select2' : 'lib/backbone-forms-select2',
      'typeahead' : 'lib/typeahead.jquery',
      'bloodhound': 'lib/bloodhound',
      'google-maps-loader' : 'lib/google-maps-loader',
      'humanize' : 'lib/humanize-duration',
      'chart': 'lib/Chart',
		'template' : '../template',

	},
	wrapShim: true,
   packages: [{
	   // Include hbs as a package, so it will find hbs-builder when needed
	   name: "hbs",
	   location: "lib/hbs",
	   main: "hbs",
	}],
	hbs: {
		templateExtension: ".hbs",
   },
	shim : {

		'underscore' : {
			exports : '_'
		},
		'underscore-string' : {
			deps : [ 'underscore' ]
		},
		'underscore-inflection' : {
			deps : [ 'underscore' ]
		},
		'handlebars-orig' : {
			exports : 'Handlebars'
		},

		'backbone': {
			deps: ['underscore'],
			exports: function() {
				return this.Backbone;
			}
		},
		'marionette' : {
			deps : [ 'jquery', 'underscore', 'backbone' ],
			exports : 'Marionette'
		},
	    'backgrid': {
	       deps: ['jquery', 'underscore', 'backbone', 'backbone.paginator'],
	       exports: 'Backgrid',
	       init: function(jQuery, underscore, Backbone, PageableCollection){
	      	 Backbone.PageableCollection = PageableCollection;
	       }
	     },
		'backbone-bootstrap-modal' : {
			deps : [ 'jquery', 'underscore', 'backbone', 'bootstrap'],
			exports : 'Backbone.BootstrapModal'
		},
		'backbone-forms' : {
			deps : [ 'jquery', 'underscore', 'backbone'],
			exports : 'Backbone.Form'
		},
		'backbone.paginator' : {
			deps : [ 'underscore', 'backbone'],
			exports : 'PageableCollection'
		},
		'backbone-forms-bootstrap3' : {
			deps : [ 'jquery', 'underscore', 'backbone', 'backbone-forms' ]
		},
		'backgrid-paginator' : {
			deps : [ 'underscore', 'backbone', 'backgrid', 'backbone.paginator' ],
			exports : 'Backgrid.Extension.Paginator'
		},
		'backgrid-moment' : {
			deps : [ 'backgrid', 'moment' ],
			exports : 'Backgrid.Extension.Moment'
		},
		'backgrid-text' : {
			deps : [ 'backgrid' ],
			exports : 'Backgrid.Extension.Text'
		},
		'backgrid-responsive-grid' : {
			deps : [ 'jquery', 'underscore', 'backbone', 'backgrid' ],
			exports : 'Backgrid.Extension.ResponsiveGrid'
		},
		'bootstrap' : {
			deps : [ 'jquery' ]
		},
		'calendar': {
			deps : [ 'jquery' ]
		},
		'bootstrap-markdown' : {
			deps : [ 'jquery' ],
			exports : 'Markdown'
		},
		'bootstrap-switch' : {
			deps : [ 'jquery' ],
		},
		'keymaster' : {
			exports : 'key'
		},
		'async' : {
			exports : 'async'
		},
		'calipso' : {
			deps : [ "i18n!nls/labels", 'underscore', 'handlebars', 'calipso-hbs', 'moment',
			         'backbone', 'backbone.paginator', 'backbone-forms', 'backbone-forms-bootstrap3', 'backbone-forms-select2',  
			         'marionette', 
			         'backgrid', 'backgrid-moment', 'backgrid-text', 'backgrid-responsive-grid', 'backgrid-paginator', 
			         'bloodhound', 'typeahead', 'bootstrap-datetimepicker', 'bootstrap-switch', 
			         'jquery-color', 'intlTelInput', 'q', 'chart'],
			exports : 'calipso',
		},
		
		'cookie': { 
			deps: ['jquery']
		},
		'chai-jquery': { 
			deps: ['jquery', 'chai']
		},
      'jquery-color': {
         deps: ['jquery'],
         exports: 'jQuery.Color'
     },

     'intlTelInputUtil': {
   	  deps:['jquery']
     },
     'intlTelInput': {
   	  deps:['intlTelInputUtil']
     },
	}

});

// r.js does only reads the above config. this one is 
// merged with the first while on the browser by requirejs. 
require.config({
	waitSeconds: 0,
   config: {
       i18n: {
      	 locale: localStorage.getItem('locale') || "de"
       }
   },
});
//Load our app module and pass it to our definition function
require(['app']);