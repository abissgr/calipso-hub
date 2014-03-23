define([ 'underscore', 'marionette', 'hbs!template/generic-search-layout', 'view/generic-collection-grid-view' ], 
		function(_, Marionette, tmpl, GenericCollectionGridView) {
	return Marionette.Layout.extend({
		className : 'calipso-generic-search-layout',
		template : tmpl,// _.template(templates.applayout),
		regions : {
			searchFormRegion : "#calipsoSearchFormRegion",
			searchResultsRegion : "#calipsoSearchResultsRegion"
		},
		onShow : function() {
			var searchResultsView = new GenericCollectionGridView({   model: this.model});
	      this.searchResultsRegion.show(searchResultsView);
		}
	});
});