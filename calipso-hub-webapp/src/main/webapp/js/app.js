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
define(function(require) {
	var Backbone = require('backbone'),
	Marionette = require('marionette'),
	HeaderView = require('view/HeaderView'),
	FooterView = require('view/FooterView');
	

  var app = new Marionette.Application();
  console.log("Backbone.Marionette.Application constructor returns: "+app);
  // application configuration
  app.config = {
    // you can provide an absolute URL like http://api.yourserver.com/v1
    apiUrl: 'apiUrlManos'
  };

  //custom region that shows a view in bootstrap modal
  var ModalRegion = Marionette.Region.extend({
    el: "#modal",

    onShow: function(view) {
      view.on("close", this.hideModal, this);
      this.$el.modal('show');
    },
    
    hideModal: function() {
      this.$el.modal('hide');
    }
  });

  // main regions, check index.html 
  app.addRegions({
	  headerRegion: "#calipsoRegionHeader",
	  mainRegion: "#calipsoRegionMain",
	  modal:   ModalRegion,
	  footerRegion: "#calipsoRegionFooter"
  });

  //---------------
  // app events
  //---------------
  // initialize header, footer, history
  app.on("initialize:after", function() {
 	 console.log("app event initialize:after");
		 app.headerRegion.show(new HeaderView());
		 app.footerRegion.show(new FooterView());
		 Backbone.history.start({ pushState: true });
  });
  app.vent.on('app:show', function(appView) {
	 	 console.log("vent event app:show");
	    app.mainRegion.show(appView);
	  });
  app.vent.on('nav-menu:change', function(modelkey) {
	 	 console.log("vent event nav-menu:change");
	  
	  $('.navbar-nav li.active').removeClass('active');
	  $('#mainNavigationTab-' + modelkey).addClass('active');
  });
  app.vent.on('modal:show', function(view) {
	 	 console.log("vent event modal:show");
    app.modal.show(view);
  });
  app.vent.on('modal:close', function() {
	 	 console.log("vent event modal:close");
    app.modal.hideModal();
  });
  

  app.addInitializer(function(options) {
    // we neeed to override loadTemplate because Marionette expect to recive only the template ID
    // but actually it's the full template html (require + text plugin)
//    Backbone.Marionette.TemplateCache.prototype.loadTemplate = function (templateId) {
//      var template = templateId;
//      // remove this comment if you want to make sure you have a template before trying to compile it
//      /*
//      if (!template || template.length === 0) {
//        var msg = "Could not find template: '" + templateId + "'";
//        var err = new Error(msg);
//        err.name = "NoTemplateError";
//        throw err;
//      }*/
//
//      return template;
//    };
    
    // init ALL app routers
    _(options.routers).each(function(routerClass) {
   	 console.log("initialize router type: "+routerClass);
      var router = new routerClass();
  	 console.log("initialized router: "+router);
    });

  });
  
  app.getCalipsoAppBaseUrl = function() {
		var calipsoMainScript = document.getElementById("calipso-script-main");
		// calipso in host page
		if (calipsoMainScript) { 
			var basePathEnd = calipsoMainScript.src.indexOf("/js/lib/require.js");
			return calipsoMainScript.src.substring(0, basePathEnd);
		} else {
			// calipso client
			return window.location.protocol + "//" + window.location.host;
		}
	};
  return app;
});
