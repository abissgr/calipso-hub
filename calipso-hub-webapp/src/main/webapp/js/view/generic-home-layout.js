define([ 'app', 'underscore', 'marionette', 'hbs!template/generic-home-layout', 'view/GenericFormView', 'collection/generic-collection', 'view/generic-collection-grid-view' ], 
		function(CalipsoApp, _, Marionette, tmpl, GenericFormView, GenericCollection, GenericCollectionGridView) {
	var GenericHomeLayout = Marionette.Layout.extend({
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
			else{
				var searchModel = this.model.searchModel ? this.model.searchModel : this.model;
				this.searchResultsCollection = new GenericCollection([], {
					model : searchModel,
					url : CalipsoApp.getCalipsoAppBaseUrl() + searchModel.apiUrlSegment
				});
			}
	  },
		regions : {
			searchCriteriaRegion : "#calipsoSearchCriteriaRegion",
			searchResultsRegion : "#calipsoSearchResultsRegion"
		},
		onShow : function() {
			var searchModel = this.model.searchModel ? this.model.searchModel : this.model;
			console.log("GenericHomeLayout#onShow, searchModel: " + searchModel.className + ", searchModel apiUrlSegment: "+ searchModel.apiUrlSegment);
			var searchFormView = new GenericFormView({ formSchemaKey: "search",  model: searchModel, searchResultsCollection: this.searchResultsCollection});

			console.log("GenericHomeLayout.onShow, model: "+searchModel.className+", searchResultsCollection: "+this.searchResultsCollection);
			//this.searchCriteriaRegion.show(searchFormView);
//			var searchResultsView = new GenericCollectionGridView({   model: this.model});
//	      this.searchResultsRegion.show(searchResultsView);

			$('#tab-label-'+this.model.get("id")).tab('show');
		}
	},
	// static members
	{
		className: "GenericHomeLayout",
	});
	return GenericHomeLayout;
});