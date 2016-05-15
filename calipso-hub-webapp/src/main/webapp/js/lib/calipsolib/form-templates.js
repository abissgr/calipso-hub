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
[ "lib/calipsolib/util", 'backbone-forms-bootstrap3'],
function(Calipso, BackboneFormsBootstrap) {

	Calipso.util.formTemplates = {
		horizontal : {
			formClassName : "form-horizontal",
			form :  _.template('\
			<form class="form-horizontal" role="form">\
		    <div data-fieldsets></div>\
				<% if (fieldsInitiallyShown) { %>\
				<% } %>\
				<% if (submitButton) { %>\
				<button type="submit" class="submit btn btn-primary"><%= submitButton %></button>\
				<% } %>\
		  </form>'),
			field : _.template('\
		    <div class="form-group field-<%= key %>">\
		      <label class="col-xs-12  col-sm-12  col-md-3 col-lg-3 control-label" for="<%= editorId %>">\
		        <% if (titleHTML){ %><%= titleHTML %>\
		        <% } else { %><%- title %><% } %>\
		      </label>\
		      <div class="col-xs-12  col-sm-12  col-md-9 col-lg-9" data-editor></div>\
		      <p class="col-sm-12" class="help-block" data-error></p>\
		    </div>\
		  '),
		},
		vertical : {
			formClassName : "",
			form :  _.template('\
			<form>\
		    <div  data-fieldsets></div>\
				<% if (fieldsInitiallyShown) { %>\
					<button class="btn btn-info btn-sm addLazyField" data-field="firstName">add<button>\
				<% } %>\
				<% if (submitButton) { %>\
				<button type="submit" class="submit btn btn-primary"><%= submitButton %></button>\
				<% } %>\
		  </form>'),
			field : _.template('\
		    <div class="form-group field-<%= key %>">\
		      <label class="control-label" for="<%= editorId %>">\
		        <% if (titleHTML){ %><%= titleHTML %>\
		        <% } else { %><%- title %><% } %>\
		      </label>\
		      <div data-editor></div>\
		      <p class="col-sm-12" class="help-block" data-error></p>\
		    </div>\
		  '),
		},
		inline : {
			formClassName : "form-inline",
			form :  _.template('\
			<form class="form-inline">\
		    <span data-ffieldsets></span>\
				<% if (fieldsInitiallyShown) { %>\
				<% } %>\
				<% if (submitButton) { %>\
				<span class="form-group bmd-form-group">\
				<button type="submit" class="submit btn btn-primary btn-sm"><%= submitButton %></button>\
				</span>\
				<% } %>\
		  </form>'),
			field : _.template('\
		    <div class="form-group field-<%= key %>">\
		      <label class="control-label" for="<%= editorId %>">\
		        <% if (titleHTML){ %><%= titleHTML %>\
		        <% } else { %><%- title %><% } %>\
		      </label>\
		      <span data-editor></span>\
		      <p class="col-sm-12" class="help-block" data-error></p>\
		    </div>\
		  '),
			fieldset : _.template('\
		    <span data-fields>\
		    </span>\
		  '),
		},
	};
	return Calipso;
});
