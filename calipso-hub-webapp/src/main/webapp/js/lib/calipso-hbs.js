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

define([
'hbs!template/modal-layout', 
'hbs!template/header', 
'hbs!template/header-menuitem', 
'hbs!template/md-browse-layout', 
'hbs!template/md-search-layout', 
'hbs!template/md-report-layout', 
'hbs!template/notfound', 
'hbs!template/footer', 
'hbs!template/md-collection-grid-view', 
'hbs!template/md-report-view', 
'hbs!template/MainContentNavView',
'hbs!template/md-form-view', 
'hbs!template/generic-crud-layout',
'hbs!template/login', 
'hbs!template/applayout', 
'hbs!template/tab-label', 
'hbs!template/loginRegistered', 
'hbs!template/headerNotificationsCollectionView',  
'hbs!template/headerNotificationsItemView', 
'hbs!template/templateBasedItemView',  
'hbs!template/templateBasedCollectionView' ]
,

function(
		modal_layout, 
		header, 
		header_menuitem, 
		md_browse_layout, 
		md_search_layout, 
		md_report_layout, 
		not_found, 
		footer, 
		md_collection_grid_view, 
		md_report_view, 
		MainContentNavView,
		md_form_view, 
		generic_crud_layout,
		login, 
		applayout, 
		tab_label, 
		loginRegistered, 
		headerNotificationsCollectionView,  
		headerNotificationsItemView, 
		templateBasedItemView,  
		templateBasedCollectionView		
) {
	return {

	"modal-layout":modal_layout, 
	"header":header, 
	"header-menuitem":header_menuitem, 
	"md-browse-layout":md_browse_layout, 
	"md-search-layout":md_search_layout, 
	"md-report-layout":md_report_layout, 
	"notfound":not_found, 
	"footer":footer, 
	"md-collection-grid-view":md_collection_grid_view, 
	"md-report-view":md_report_view, 
	"MainContentNavView":MainContentNavView,
	"md-form-view":md_form_view, 
	"generic-crud-layout":generic_crud_layout,
	"login":login, 
	"applayout":applayout, 
	"tab-label":tab_label, 
	"loginRegistered":loginRegistered, 
	"headerNotificationsCollectionView":headerNotificationsCollectionView,  
	"headerNotificationsItemView":headerNotificationsItemView, 
	"templateBasedItemView":templateBasedItemView,  
	"templateBasedCollectionView":templateBasedCollectionView
	
	};

});
