/*
 * Copyright (c) 2007 - 2014 www.Abiss.gr
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


define(
		[  'underscore', 'backbone', 'marionette', 'lib/calipsolib/controller' ],
		function(_, Backbone, Marionette,  Calipso) {

	/**
	 * Base class for your Application Router
	 */
	Calipso.AppRouter = Marionette.AppRouter.extend({

		//controller : new Calipso.Controller(),

		appRoutes : {

			'' : 'home',
			'_=_' : 'toHome',
			'#_=_' : 'toHome',
			'home' : 'home',

			'logout' : 'logout',

			'userDetails/:useCaseKey' : 'showUserDetailsView',
			'userDetails/:useCaseKey/' : 'showUserDetailsView',
			'userDetails/:useCaseKey?*queryString' : 'showUserDetailsView',
			'userDetails/:useCaseKey/?*queryString' : 'showUserDetailsView',

			'page/:templateName' : 'templatePage',
			'useCases/:pathFragment' : 'showEntitySearch',
			'useCases/:pathFragment/' : 'showEntitySearch',
			'useCases/:pathFragment/?*queryString' : 'showEntitySearch',
			'useCases/:pathFragment?*queryString' : 'showEntitySearch',

			'useCases/:pathFragment/:modelId' : 'showEntityView',
			'useCases/:pathFragment/:modelId/:useCaseKey' : 'showUseCaseView',

			'*path':  'notFoundRoute'
		}

	},
	{
	// static members
		getTypeName: function(){return "Calipso.AppRouter"}
	});

	// override the extend function to mere own and child routes for new type
	Calipso.AppRouter.extend = function(child) {
		var router = Marionette.AppRouter.extend.apply(this, arguments);
		router.prototype.appRoutes = _.extend({}, this.prototype.appRoutes, child.appRoutes);
		return router;
	};

	return Calipso;

});
