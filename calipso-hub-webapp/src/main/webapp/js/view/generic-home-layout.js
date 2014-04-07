define([ 'session', 'vent', 'underscore', 'marionette', 'hbs!template/generic-home-layout', 'view/GenericFormView', 'view/home-view', 
         'collection/generic-collection', 'view/md-browse-layout', 'view/md-collection-grid-view',
         'model/user'], 
		function(session, vent, _, Marionette, tmpl, GenericFormView, HomeView, 
				GenericCollection, ModelDrivenBrowseLayout, GenericCollectionGridView,
				UserModel) {
	var GenericHomeLayout = ModelDrivenBrowseLayout.extend({
		
		tagName : 'div',
		className : "row",
		template: tmpl,
		regions : {
			sidebarRegion : "#calipsoModelDrivenBrowseLayout-",
			contentRegion : "#calipsoModelDrivenBrowseLayout-ContentRegion"
		},
		onShow : function() {
			var _this = this;
			this.searchResultsCollection = new GenericCollection([], {
				model : UserModel,
				url : session.getBaseUrl() + "/api/rest/users"
			});
			var searchFormView = new GenericFormView({ formSchemaKey: "search", model: new UserModel(), searchResultsCollection: this.searchResultsCollection});
			
			this.sidebarRegion.show(searchFormView);
			var homeView = new HomeView();
	      this.searchResultsRegion.show(homeView);
	      
	      searchFormView.on("search:retreivedResults", function(searchResultsCollection){
	      	console.log("ModelDrivenBrowseLayout, searchFormView on search:retreivedResults"+searchResultsCollection.getTypeName());
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
		getTypeName: function(){return "GenericHomeLayout"}
	});
	return GenericHomeLayout;
});