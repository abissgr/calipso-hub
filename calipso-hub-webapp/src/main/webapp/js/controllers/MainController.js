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
//this.layout.contentRegion.show(new HomeLayout());

	var MainController = Calipso.controller.AbstractController.extend({

		home : function() {
			console.log("MainController#home");
			if (!Calipso.session.isAuthenticated()) {
				this._redir("login");
				return false;
			}
			
			Calipso.navigate('users', {trigger:true});
		}

	});
	return MainController;
});
	