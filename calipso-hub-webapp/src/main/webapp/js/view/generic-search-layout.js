define([ 'underscore', 'marionette', 'hbs!template/generic-search-layout', 'view/GenericFormView', 'view/generic-collection-grid-view' ], 
		function(_, Marionette, tmpl, GenericFormView, GenericCollectionGridView) {
	var GenericSearchLayout = Marionette.Layout.extend({
		tagName : 'div',
		className : "tab-pane active",
		id: "tab-Search",
		template : tmpl,// _.template(templates.applayout),
		regions : {
			searchCriteriaRegion : "#calipsoSearchCriteriaRegion",
			searchResultsRegion : "#calipsoSearchResultsRegion"
		},
		onShow : function() {
			
			var searchFormView = new GenericFormView({ schemaAction: "search",  model: this.model});
	      this.searchCriteriaRegion.show(searchFormView);
			var searchResultsView = new GenericCollectionGridView({   model: this.model});
	      this.searchResultsRegion.show(searchResultsView);

			$('#tab-label-'+this.model.get("id")).tab('show');
		}
	},
	// static members
	{
		className: "GenericSearchLayout",
	});
	return GenericSearchLayout;
});