define([ 'underscore', 'marionette', 'hbs!template/generic-search-layout', 'view/GenericFormView', 'view/generic-collection-grid-view' ], 
		function(_, Marionette, tmpl, GenericFormView, GenericCollectionGridView) {
	var GenericSearchLayout = Marionette.Layout.extend({
		tagName : 'div',
		className : "tab-pane active",
		searchResultsCollection : null,
		id: "tab-Search",
		template : tmpl,// _.template(templates.applayout),

		initialize: function(options){

			Marionette.Layout.prototype.initialize.apply(this, arguments);
			if(this.options.searchResultsCollection){
				this.searchResultsCollection = options.searchResultsCollection;
			}
			console.log("GenericSearchLayout.initialize, searchResultsCollection: " + (this.searchResultsCollection ? this.searchResultsCollection.length : this.searchResultsCollection));
	  },
		regions : {
			searchCriteriaRegion : "#calipsoSearchCriteriaRegion",
			searchResultsRegion : "#calipsoSearchResultsRegion"
		},
		onShow : function() {
			var searchFormView = new GenericFormView({ formSchemaKey: "search",  model: this.model, searchResultsCollection: this.searchResultsCollection});

			console.log("GenericSearchLayout.onShow, model: "+this.model.className+", searchResultsCollection: "+this.searchResultsCollection);
			this.searchCriteriaRegion.show(searchFormView);
			var searchResultsView = new GenericCollectionGridView({   model: this.model});
	      this.searchResultsRegion.show(searchResultsView);

			$('#tab-label-'+this.model.get("id")).tab('show');
		}
	},
	// static members
	{
		typeName: "GenericSearchLayout",
	});
	return GenericSearchLayout;
});