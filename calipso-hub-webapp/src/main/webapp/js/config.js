require.config({
	// main.js is the application entry point
	//deps : [ 'main' ],
	baseUrl : '/calipso/js/',
	paths : {//lib/calipso/view/md-search-layout
		// folders
//		libs : 'lib',
		// tmpl: '../../tmpl',

		// libs: no application code here
		'cookie' : 'lib/jquery.cookie',
		'jquery' : 'lib/jquery',
		'q' : 'lib/q',
		'metis-menu' : 'lib/metisMenu/jquery.metisMenu',
		'eve':          'lib/raphael/eve.0.3.4',
		'raphael.core': 'lib/raphael/raphael.2.1.0.core',
		'raphael.svg':  'lib/raphael/raphael.2.1.0.svg',
		'raphael.vml':  'lib/raphael/raphael.2.1.0.vml',
		'raphael':      'lib/raphael/raphael.2.1.0.amd',
		'morris' : 'lib/morris/morris',
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
		'bootstrap-markdown' : 'lib/bootstrap-markdown',
		'backbone-validation' : 'lib/resthub/backbone-validation-ext',
		'marionette' : 'lib/backbone.marionette',
		'handlebars-orig' : 'lib/handlebars',
		'handlebars' : 'lib/resthub/handlebars-helpers',
		'backbone-queryparams' : 'lib/backbone-queryparams',
		// 'backbone-datagrid': 'lib/backbone-datagrid', lib/calipso/view
		// 'backbone-paginator': 'lib/backbone-paginator',
		// 'backbone-relational': 'lib/backbone-relational',
		'backbone-pageable' : 'lib/backbone-pageable',
		'async' : 'lib/async',
		'keymaster' : 'lib/keymaster',
		'hbs' : 'lib/resthub/require-handlebars',
		'moment' : 'lib/moment',
		'template' : '../template',
		'json2' : 'lib/json2',
		'console' : 'lib/resthub/console',
		'backgrid' : 'lib/backgrid/backgrid',
		"backgrid-paginator" : 'lib/backgrid/extensions/paginator/backgrid-paginator',
		"calipso" : 'lib/calipso',
		'supermodel' : 'lib/supermodel',
		// Mocha testing
		'mocha' : 'lib/mocha/mocha',
		'chai' : 'lib/chai/chai',
		//'chai-jquery' : 'lib/chai/chai-jquery',
		'sinon' : 'lib/sinon/chai-jquery',
			
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
		'backbone' : {
			deps : [ 'underscore', 'underscore-string', 'jquery' ],
			exports : 'Backbone'
		},
		'marionette' : {
			deps : [ 'jquery', 'underscore', 'backbone' ],
			exports : 'Marionette'
		},
		'supermodel' : {
			deps : [ 'underscore', 'backbone' ],
			exports : 'Supermodel'
		},
		'backbone-pageable' : {
			deps : [ 'backbone' ],
			exports : 'Backbone.PageableCollection'
		},
		'backgrid' : {
			deps : [ 'jquery', 'backbone', 'underscore'],
			exports : 'Backgrid'
		},
		'backbone-bootstrap-modal' : {
			deps : [ 'jquery', 'underscore', 'backbone', 'bootstrap'],
			exports : 'Backbone.BootstrapModal'
		},
		'backbone-forms' : {
			deps : [ 'jquery', 'underscore', 'backbone'],
			exports : 'Backbone.Form'
		},
		'backbone-forms-bootstrap3' : {
			deps : [ 'jquery', 'underscore', 'backbone', 'backbone-forms' ]
		},
		'backgrid-paginator' : {
			deps : [ 'backgrid', 'backbone-pageable' ],
			exports : 'Backgrid.Extension.Paginator'
		},
		'bootstrap' : {
			deps : [ 'jquery' ]
		},
		'bootstrap-markdown' : {
			deps : [ 'jquery' ],
			exports : 'Markdown'
		},
		'metis-menu' : {
			deps : [ 'jquery', 'bootstrap' ],
			exports : 'jquery'
		},
		'morris' : {
			deps : [ 'jquery', 'raphael' ],
			exports : 'jquery'
		},
		'raphael' : {
			deps : [ 'eve' ]
		},
		// 'backbone-relational': {
		// deps: [
		// 'backbone'
		// ]
		// },
		'keymaster' : {
			exports : 'key'
		},
		'async' : {
			exports : 'async'
		},
		'calipso' : {
			deps : [ 'q', 'backbone', 'marionette','backbone-forms', 'backbone-forms-bootstrap3', 'backgrid', 'backgrid-paginator', 'supermodel', 'metis-menu', 'morris' ],
			exports : 'calipso'
		},
		'cookie': { 
			deps: ['jquery']
		},
		'chai-jquery': { 
			deps: ['jquery', 'chai']
		}
	}

});
