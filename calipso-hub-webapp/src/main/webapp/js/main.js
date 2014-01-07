// Set the require.js configuration for your application.
require.config({

    shim: {
        'underscore': {
            exports: '_'
        },
        'underscore-string': {
            deps: [
                'underscore'
            ]
        },
        'handlebars-orig': {
            exports: 'Handlebars'
        },
        'backbone': {
            deps: [
                'underscore',
                'underscore-string',
                'jquery'
            ],
            exports: 'Backbone'
        },
        'backbone-pageable': {
            deps: [
                'backbone'
            ],
            exports: 'Backbone.PageableCollection'
        },
        'backgrid': {
            deps: ['jquery', 'backbone', 'underscore'/*, 'css!lib/backgrid/backgrid'*/],
            exports: 'Backgrid'
        },
        'backgrid-paginator': {
            deps: ['backgrid', 'backbone-pageable'],
            exports: 'Backgrid.Extension.Paginator'
        },
        'bootstrap': {
            deps: [
                'jquery'
            ]
        },
//        'backbone-relational': {
//            deps: [
//                'backbone'
//            ]
//        },
        'keymaster': {
            exports: 'key'
        },
        'async': {
            exports: 'async'
        }
    },
//    map: {
//        '*': {
//            'css': 'plugins/requirecss/css'
//        }
//    },
    // Libraries
    paths: {
        jquery: 'lib/jquery',
        underscore: 'lib/underscore',
        'underscore-string': 'lib/underscore-string',
        backbone: 'lib/backbone',
        resthub: 'lib/resthub/resthub',
        localstorage: 'lib/localstorage',
        text: 'lib/text',
        i18n: 'lib/i18n',
        pubsub: 'lib/resthub/pubsub',
        'bootstrap': 'lib/bootstrap',
        'backbone-validation-orig': 'lib/backbone-validation',
        'backbone-validation': 'lib/resthub/backbone-validation-ext',
        'handlebars-orig': 'lib/handlebars',
        'handlebars': 'lib/resthub/handlebars-helpers',
        'backbone-queryparams': 'lib/backbone-queryparams',
//        'backbone-datagrid': 'lib/backbone-datagrid',
//        'backbone-paginator': 'lib/backbone-paginator',
//        'backbone-relational': 'lib/backbone-relational',
        'backbone-pageable'  : 'lib/backbone-pageable',
        async: 'lib/async',
        keymaster: 'lib/keymaster',
        hbs: 'lib/resthub/require-handlebars',
        moment: 'lib/moment',
        template: '../template',
		json2: 'lib/json2',
        console: 'lib/resthub/console',
        backgrid: 'lib/backgrid/backgrid',
        //"backgrid/select-all":	'lib/backgrid/extensions/select-all/backgrid-select-all',
        "backgrid-paginator":	'lib/backgrid/extensions/paginator/backgrid-paginator',
    }
});

// Load our app module and pass it to our definition function
require(['console', 'app']);
