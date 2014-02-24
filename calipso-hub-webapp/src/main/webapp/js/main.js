/*
 * Copyright (c) 2007 - 2013 www.Abiss.gr
 *
 * This file is part of Calipso, a software platform by www.Abiss.gr.
 *
 * Calipso is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Calipso is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Calipso. If not, see http://www.gnu.org/licenses/agpl.html
 */
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
        'backbone-bootstrap-modal': {
            deps: ['jquery', 'underscore', 'backbone', 'bootstrap'/*, 'css!lib/backgrid/backgrid'*/],
            exports: 'Backbone.BootstrapModal'
        },
        'backbone-forms': {
            deps: ['jquery', 'underscore', 'backbone'/*, 'css!lib/backgrid/backgrid'*/],
            exports: 'Backbone.Form'
        },
        'backbone-forms-bootstrap3': {
            deps: ['jquery', 'underscore', 'backbone', 'backbone-forms', 'backbone-forms'],
        },
        /*'backbone-forms-editor-markdown': {
            deps: ['backbone-forms'],
            exports: 'Backbone.Form.editors.Markdown'
        },*/
        'backgrid-paginator': {
            deps: ['backgrid', 'backbone-pageable'],
            exports: 'Backgrid.Extension.Paginator'
        },
        'bootstrap': {
            deps: [
                'jquery'
            ]
        },
        'bootstrap-markdown': {
        	deps: ['jquery'],
            exports: 'Markdown'
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
        'backbone-bootstrap-modal': 'lib/backbone-bootstrap-modal',
        'backbone-forms': 'lib/backbone-forms',
        'backbone-forms-bootstrap3': 'lib/backbone-forms-bootstrap3',
        'bootstrap-markdown': 'lib/bootstrap-markdown',
        
        'backbone-validation': 'lib/resthub/backbone-validation-ext',
        'handlebars-orig': 'lib/handlebars',
        'handlebars': 'lib/resthub/handlebars-helpers',
        'backbone-queryparams': 'lib/backbone-queryparams',
//        'backbone-datagrid': 'lib/backbone-datagrid',
//        'backbone-paginator': 'lib/backbone-paginator',
//        'backbone-relational': 'lib/backbone-relational',
        'backbone-pageable'  : 'lib/backbone-pageable',
        //'backbone-forms-editor-markdown'  : 'component/backbone-forms-editor-markdown',
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
