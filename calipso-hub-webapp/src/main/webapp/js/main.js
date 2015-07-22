require.config({
	// main.js is the application entry point
	deps : [ 'main' ],
	waitSeconds: 200,

	paths : {
		'cookie' : 'lib/jquery.cookie',
		'jquery' : 'lib/jquery',
		'jquery-color' : 'lib/jquery.color.plus-names',
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
		'backbone-validation' : 'lib/resthub/backbone-validation-ext',
		'marionette' : 'lib/backbone.marionette',
		'handlebars-orig' : 'lib/handlebars',
		'handlebars' : 'lib/resthub/handlebars-helpers',
		'backbone-queryparams' : 'lib/backbone-queryparams',
		'backbone.paginator': 'lib/backbone.paginator',
		'async' : 'lib/async',
		'keymaster' : 'lib/keymaster',
		'moment' : 'lib/moment',
		'template' : '../template',
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

	},
	wrapShim: true,
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
		'keymaster' : {
			exports : 'key'
		},
		'async' : {
			exports : 'async'
		},
		'calipso' : {
			deps : [ 'underscore', 'handlebars', 'calipso-hbs', 
			         'backbone', 'backbone.paginator', 'backbone-forms', 'backbone-forms-bootstrap3', 'backbone-forms-select2',  
			         'marionette', 
			         'backgrid', 'backgrid-moment', 'backgrid-text', 'backgrid-responsive-grid', 'backgrid-paginator', 
			         'bloodhound', 'typeahead', 'bootstrap-datetimepicker', 
			         'jquery-color', 'q', 'chart'],
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
		
	}

});

//Load our app module and pass it to our definition function
require(['app']);