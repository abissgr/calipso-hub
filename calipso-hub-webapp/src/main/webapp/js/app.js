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

define([
  'jquery', 'underscore', 'underscore-inflection', 'backbone', 'bootstrap',
  'calipso', 'backbone', 'modules-config', 'routers/MainRouter' ],
function(
	$, _, _inflection, Backbone, bootstrap,
	Calipso, Backbone, modulesConfig, MainRouter) {


	//////////////////////////////////
	// Global backbone error handling
	//////////////////////////////////
	Backbone.ajax = function() {
		// Invoke $.ajaxSetup in the context of Backbone.$
		Backbone.$.ajaxSetup.call(Backbone.$, Calipso.getDefaultFetchOptions());
		return Backbone.$.ajax.apply(Backbone.$, arguments);
	};

	//////////////////////////////////
	// intercept links
	//////////////////////////////////
	$(document).on("click", "a", function(event) {

		var $a = $(this);
		var href = $a.attr("href");

		if (href && href.match(/^\/.*/) && !$(this).attr("target")) {
			Calipso.stopEvent(event);

			if ($a.hasClass("triggerCollapseMenu")) {
				// mobile, collapse hide
				if ($(window).width() < 768) {
					$a.closest(".navbar-collapse").collapse('hide');
				}
				// desktop-ish, close dropdown
				else{
					$a.closest(".dropdown").removeClass('open');
				}
			}

			Backbone.history.navigate(href, true);
		}
	});

	//////////////////////////////////
	// Bootstrap: enable tooltips
	//////////////////////////////////
	$(document).ready(function() {
		$(document.body).tooltip({
			selector : "[data-toggle=tooltip]",
			html : true
		});

	});
    //////////////////////////////////
    // Use POST instead of PUT/PATCH/DELETE
    //////////////////////////////////
    Backbone.emulateHTTP = true;

	//////////////////////////////////
	// Start the app
	//////////////////////////////////


  var initOptions = {
    contextPath: "calipso/",
  };

	var startOptions = {};
	startOptions.routers = {};
	startOptions.routers.main = MainRouter;
	startOptions.menu = [ {
		label: "Users",
		url: "users"
	},{
		label: "Hosts",
		url: "hosts"
	} ];

  Calipso.start(initOptions, startOptions);

	return Calipso;
});
