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
	/*
	<div class="form-group field-username">
	<label class="control-label" for="c14_username">        Username      </label>
	<span class="col-sm-10" data-editor=""><input id="c14_username" class="form-control" name="username" type="text"></span>            </div>


	*/
		"field-horizontal" : _.template('\
	    <div class="form-group field-<%= key %>">\
	      <label class="col-xs-12  col-sm-12  col-md-3 col-lg-3 control-label" for="<%= editorId %>">\
	        <% if (titleHTML){ %><%= titleHTML %>\
	        <% } else { %><%- title %><% } %>\
	      </label>\
	        <span class="col-xs-12  col-sm-12  col-md-9 col-lg-9" data-editor></span>\
	        <p class="col-sm-12" class="help-block" data-error></p>\
	    </div>\
	  '),
		horizontal : _.template('\
		<form class="form-horizontal">\
	    <div data-fieldsets></div>\
	    <% if (submitButton) { %>\
	    <button type="submit" class="btn"><%= submitButton %></button>\
	    <% } %>\
	  </form>'),
		// TODO: fix or remove each entry
		nav : _.template('\
		<nav class="navbar navbar-default">\
		<form autocomplete=\"off\" class="navbar-form navbar-left" role="form">\
		<span data-fields="*"></span>\
		<% if (submitButton) { %>\
		<button type="submit" class="submit btn btn-primary"><%= submitButton %></button>\
		<% } %>\
		</form>\
		</nav>'),
		inline : _.template('\
		<form autocomplete=\"off\" class="form-inline" role="form">\
		<span data-fields="*"></span>\
		<% if (submitButton) { %>\
		<div class="form-group"><button type="submit" class="submit btn btn-primary"><%= submitButton %></button></div>\
		<% } %>\
		</form>'),
		vertical : _.template('\
		<form autocomplete=\"off\" role="form">\
		<div data-fieldsets></div>\
		<% if (submitButton) { %>\
		<button type="submit" class="submit btn btn-primary"><%= submitButton %></button>\
		<% } %>\
		</form>'),
		auth : _.template('\
		<form autocomplete=\"off\" role="form">\
		<div data-fieldsets></div>\
		<% if (submitButton) { %>\
		<button type="submit" class="submit btn btn-primary"><%= submitButton %></button>\
		<span class="pull-right">\
			<% if (submitButton.indexOf("Reg") == -1) { %>\
		   <small>Need an account?</small>\
		   <a title="Click to register" class="btn btn-success" href="/register">Register</a>\
			<% } %>\
		   <small>or sign-in with</small>\
		    <div role="group" class="btn-group">\
		        <a class="btn btn-default btn-social-login btn-social-login-facebook">\
		            <i class="fa fa-facebook-f"></i><!-- &#160;facebook -->\
		        </a>\
		        <a class="btn btn-default btn-social-login btn-social-login-linkedin">\
		            <i class="fa fa-linkedin"></i><!-- &#160;linkedin  -->\
		        </a>\
		        <!--a class="btn btn-default btn-social-login btn-social-login-twitter">\
		            <i class="fa fa-twitter"></i><!-- &#160;twitter -->\
		        <a class="btn btn-default btn-social-login btn-social-login-google">\
		            <i class="fa fa-google-plus"></i><!-- &#160;google+ -->\
		        </a>\
		    </div>\
		</span>\
		<% } %>\
		</form>'),
	};
	return Calipso;
});
