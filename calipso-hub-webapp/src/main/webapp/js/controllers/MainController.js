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
define(['calipso', 'underscore', 'backbone', 'marionette'],

function(Calipso, _, Backbone, Marionette) {


	var MainController = Calipso.controller.AbstractController.extend({

		home : function() {
			console.log("AbstractController#home");
			if (!Calipso.session.isAuthenticated()) {
				Backbone.history.navigate(Calipso.app.config.contextPath + "client/login", {
					trigger : true
				});
				return false;
			}
			var modelForRoute = Calipso.model.UserModel.create({id: "search"});
			
			modelForRoute.wrappedCollection = new Calipso.collection.GenericCollection([], {
				model : Calipso.model.UserModel,
				url : Calipso.session.getBaseUrl() + "/api/rest/users",
				data: {foobar: true}
				
			});
			console.log("Tryiung to show home with client layout");
			var LayoutType = Calipso.model.UserModel.prototype.getLayoutViewType();
			Calipso.vent.trigger('app:show', new LayoutType({
				model: modelForRoute,
				hideSidebarOnSearched: true
			}));
		}

	});
	return MainController;
});
	