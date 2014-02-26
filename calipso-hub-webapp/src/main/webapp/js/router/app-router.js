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
define(['calipso-app', 
		'controller/calipso-app-controller', 
        'backbone', 'view/about-view', 'view/home-view', 
        'view/generic-collection-grid-view', 'collection/generic-collection', 
        'model/host', 'model/text', 
        'model/user'], 
        

function (CalipsoApp, calipsoController, 
		HostModel, TextModel, 
		UserModel) {
	// Override Backbone.sync to use X-HTTP-Method-Override
    Backbone.emulateHTTP = true;
    //Backbone.emulateJSON
    var CalipsoAppRouter = Marionette.AppRouter.extend({
//        initialize: function() {
//           // Backbone.history.start({ pushState: true, root: "/" });
//        },
	    modelsMap : {
	    		'hosts' : HostModel,
	    		'texts' : TextModel,
	    		'users' : UserModel,
	    },
        routes : {
            '':'home',
            'client/:mainNavigationTab':'mainNavigationRoute',
        },
        appRoutes : {
            '':'initialize',
            'initialize':'initialize',
        },
        mainNavigationRoute : function (mainNavigationTab) {
        	console.log("main, mainNavigationTab: "+mainNavigationTab);
        	// sync main menu state
        	this.syncMainNavigationState(mainNavigationTab);

        	// begin with layout 
        	//-------------------
//        	var layout = new ClientLayout();
//        	layout.render();
        	
        	// then with regions
        	//-------------------
        	
        	// content region
        	//===================
        	
        	var contentRegionView;
        	// is an explicit route available?
        	if(typeof this[mainNavigationTab] == 'function'){
            	// proceed with actual route
        		this[mainNavigationTab]();
        		return;
        	}
        	else if(this.modelsMap[mainNavigationTab]){
        		contentRegionView = this.genericMainNavigationView($('#main'), mainNavigationTab);
        	}
        	else{
        		window.alert("No route found!");
        	}

        	console.log("mainNavigationRoute, contentRegionView: " + contentRegionView);
        	//layout.contentRegion.show(contentRegionView);
        	CalipsoApp.mainRegion.show(contentRegionView);
        	
        	
        	
        	/*
        	
        	*/
        },
        home:function () {
        	CalipsoApp.mainRegion.show(new HomeView());
        },
        about:function () {
        	CalipsoApp.mainRegion.show(new AboutView());
        },
       
    });
    return new CalipsoAppRouter({controller: calipsoController});
});