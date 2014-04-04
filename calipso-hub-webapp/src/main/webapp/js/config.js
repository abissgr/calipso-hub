require.config({
	// main.js is the application entry point
	deps : [ 'main' ],

	paths : {
		// folders
		libs : 'lib',
		// tmpl: '../../tmpl',

		// libs: no application code here
		cookie : 'lib/jquery.cookie',
		jquery : 'lib/jquery',
		underscore : 'lib/underscore',
		'underscore-string' : 'lib/underscore-string',
		'underscore-inflection' : 'lib/underscore-inflection',
		backbone : 'lib/backbone',
		resthub : 'lib/resthub/resthub',
		localstorage : 'lib/localstorage',
		text : 'lib/text',
		i18n : 'lib/i18n',
		pubsub : 'lib/resthub/pubsub',
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
		// 'backbone-datagrid': 'lib/backbone-datagrid',
		// 'backbone-paginator': 'lib/backbone-paginator',
		// 'backbone-relational': 'lib/backbone-relational',
		'backbone-pageable' : 'lib/backbone-pageable',
		async : 'lib/async',
		keymaster : 'lib/keymaster',
		hbs : 'lib/resthub/require-handlebars',
		moment : 'lib/moment',
		template : '../template',
		json2 : 'lib/json2',
		console : 'lib/resthub/console',
		backgrid : 'lib/backgrid/backgrid',
		"backgrid-paginator" : 'lib/backgrid/extensions/paginator/backgrid-paginator',
		supermodel : 'lib/supermodel'
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
		marionette : {
			deps : [ 'jquery', 'underscore', 'backbone' ],
			exports : 'Marionette'
		},
		supermodel : {
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
			deps : [ 'jquery', 'underscore', 'backbone', 'backbone-forms', 'backbone-forms' ],
		},
		/*
		 * 'backbone-forms-editor-markdown': { deps: ['backbone-forms'], exports:
		 * 'Backbone.Form.editors.Markdown' },
		 */
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
		cookie: { 
			deps: ['jquery']
		}
	}

// development only: cache bust
 //urlArgs: "bust=" + (new Date()).getTime());

});
