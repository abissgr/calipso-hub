define([ 'app', 'underscore', 'marionette', 'hbs!template/generic-home-layout', 'view/GenericFormView', 'view/home-view', 
         'collection/generic-collection', 'view/generic-search-layout', 'view/generic-collection-grid-view',
         'model/generic-collection-wrapper-model', 'model/client'], 
		function(CalipsoApp, _, Marionette, tmpl, GenericFormView, HomeView, 
				GenericCollection, GenericSearchLayout, GenericCollectionGridView,
				GenericCollectionWrapperModel, ClientModel) {
	var GenericHomeLayout = GenericSearchLayout.extend({
		
		tagName : 'div',
		className : "row",
		template: tmpl,
		onShow : function() {
			var _this = this;
			this.searchResultsCollection = new GenericCollection([], {
				model : ClientModel,
				url : CalipsoApp.getCalipsoAppBaseUrl() + "/api/rest/clients"
			});
			var searchFormView = new GenericFormView({ formSchemaKey: "search", model: new ClientModel(), searchResultsCollection: this.searchResultsCollection});
			
			this.searchCriteriaRegion.show(searchFormView);
			var homeView = new HomeView();
	      this.searchResultsRegion.show(homeView);
	      
	      searchFormView.on("search:retreivedResults", function(searchResultsCollection){
	      	console.log("GenericSearchLayout, searchFormView on search:retreivedResults"+searchResultsCollection.className);
	      	var wrapperModel = new GenericCollectionWrapperModel({
					modelClass : searchResultsCollection.model,
					wrappedCollection : searchResultsCollection
				});
	      	var searchResultsView = new GenericCollectionGridView({ model: wrapperModel});
	      	_this.searchResultsRegion.show(searchResultsView);
		      $("#calipsoSearchCriteriaRegion").hide();
		      $("#calipsoSearchResultsRegion").removeClass("col-sm-9");
		      $("#calipsoSearchResultsRegion").addClass("col-sm-12");
			});
	      
			$('#tab-label-'+this.model.get("id")).tab('show');
		}
	},
	// static members
	{
		typeName: "GenericHomeLayout",
	});
	return GenericHomeLayout;
});