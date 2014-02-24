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
define(['backbone', 'view/about-view', 'view/generic-collection-grid-view', 
        'collection/generic-collection', 'model/host', 
        'model/text', 'model/user'], 
function (Backbone, AboutView, 
		GenericCollectionGridView, GenericCollection, 
		HostModel, TextModel, 
		UserModel) {
	// Override Backbone.sync to use X-HTTP-Method-Override
    Backbone.emulateHTTP = true;
    //Backbone.emulateJSON
    var AppRouter = Backbone.Router.extend({
        initialize: function() {
            Backbone.history.start({ pushState: true, root: "/" });
        },
	    modelsMap : {
	    		'host' : HostModel,
	    		'text' : TextModel,
	    		'user' : UserModel,
	    },
        routes : {
            '':'home',
            'client/:mainNavigationTab':'mainNavigation',
        },
        mainNavigation:function (mainNavigationTab) {
        	console.log("main, mainNavigationTab: "+mainNavigationTab);
        	// sync main menu state
        	this.syncMainNavigationState(mainNavigationTab);
        	
        	// is an explicit route avaiable?
        	if(typeof this[mainNavigationTab] == 'function'){
            	// proceed with actual route
            	this[mainNavigationTab]();
        	}
        	else{
        		this.genericMainNavigationView($('#main'), mainNavigationTab);
        	}
        },
        home:function () {
        	new HomeView({root:$('#main')});
        },
        about:function () {
            new AboutView({root:$('#main')});
        },
        genericMainNavigationView:function (viewRoot, mainNavigationTab) {
        	var viewModel = this.modelsMap[mainNavigationTab];
        	console.log("genericGridView, viewModel: "+viewModel);
    		var viewRoute = "/api/rest/"+mainNavigationTab+"/";
        	console.log("genericGridView, viewRoute: "+viewRoute);
        	var viewCollection = new GenericCollection([], {
        		model: viewModel,
        		url: window.calipso.getBaseUrl() + viewRoute
        	});
            new GenericCollectionGridView({root:viewRoot, collection:viewCollection}).render();
        },
        syncMainNavigationState: function(mainNavigationTab){
        	$('.navbar-nav li.active').removeClass('active');
			$('#mainNavigationTab-'+mainNavigationTab).addClass('active');
        }
    });
    return AppRouter;
});