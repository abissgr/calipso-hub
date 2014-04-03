define([ 'app', 'underscore', 'marionette', 'hbs!template/generic-home-layout', 'view/GenericFormView', 'view/home-view', 'collection/generic-collection', 'view/generic-search-layout' ], 
		function(CalipsoApp, _, Marionette, tmpl, GenericFormView, HomeView, GenericCollection, GenericSearchLayout) {
	var GenericHomeLayout = GenericSearchLayout.extend({
		
		onShow : function() {
			var searchFormView = new GenericFormView({ formSchemaKey: "search",  model: this.model, searchResultsCollection: this.searchResultsCollection});

			console.log("GenericSearchLayout.onShow, model: "+this.model.className+", searchResultsCollection: "+this.searchResultsCollection);
			this.searchCriteriaRegion.show(searchFormView);
			var homeView = new HomeView();
	      this.searchResultsRegion.show(homeView);

			$('#tab-label-'+this.model.get("id")).tab('show');
		}
	},
	// static members
	{
		className: "GenericHomeLayout",
	});
	return GenericHomeLayout;
});