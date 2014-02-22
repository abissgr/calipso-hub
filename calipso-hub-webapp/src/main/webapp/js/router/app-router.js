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
define(['backbone', 'view/about-view', 'view/generic-collection-grid-view', 'collection/generic-collection', 'model/host', 'model/text', 'model/user'], 
function (Backbone, AboutView, GenericCollectionGridView, GenericCollection, HostModel, TextModel, UserModel) {
	// Override Backbone.sync to use X-HTTP-Method-Override
    Backbone.emulateHTTP = true;
    //Backbone.emulateJSON
    var AppRouter = Backbone.Router.extend({
        initialize: function() {
            Backbone.history.start({ pushState: true, root: "/" });
        },
        routes:{
            '':'home',
            'client/home':'home',
            'client/hosts':'hosts',
            'client/text':'text',
            'client/users':'users',
            'client/about':'about'
        },
        home:function () {
            //new SamplesView({root:$('#main')});
        },
        hosts:function () {
        	this.genericGridView($('#main'), HostModel, "/api/rest/host/");
        },
        text:function () {
        	this.genericGridView($('#main'), TextModel, "/api/rest/cms/text/");
        },
        users:function () {
        	this.genericGridView($('#main'), UserModel, "/api/rest/user/");
        },
        about:function () {
            new AboutView({root:$('#main')});
        },
        genericGridView:function (viewRoot, viewModel, viewRoute) {
        	var viewCollection = new GenericCollection([], {
        		model: viewModel,
        		url: window.calipso.getBaseUrl() + viewRoute
        	});
            new GenericCollectionGridView({root:viewRoot, collection:viewCollection}).render();
        },
    });
    return AppRouter;
});