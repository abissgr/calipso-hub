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
	CalipsoApp = require('app'),
	session = require('session'),
	vent = require('vent'),
	AbstractController = require('controllers/abstract-controller'),
	AppLayoutView = require('view/AppLayoutView'),
	HomeView = require('view/HomeView'),
	NotFoundView = require('view/NotFoundView'),
	LoginView = require('view/LoginView'),
	MainContentNavView = require('view/MainContentNavView'),
	TabLayout = require('view/generic-crud-layout'),
	GenericCollectionGridView = require('view/generic-collection-grid-view'),
	GenericFormView = require('view/GenericFormView'),
	GenericCollection = require('collection/generic-collection'),
	GenericModel = require('model/generic-model'),
	GenericCollectionWrapperModel = require('model/generic-collection-wrapper-model'),
	LoginModel = require('model/LoginModel'),
	UserModel = require('model/user'),
	TextModel = require('model/text'),
	HostModel = require('model/host');


	var MainController = AbstractController.extend({
//		constructor: function(options){
//			options.layout : new AppLayoutView({
//				model : session
//			});
//			AbstractController.prototype.constructor.call(this, options);
//		},


	});
	return MainController;
});
	