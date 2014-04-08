define(function(require) {
		var _ = require('underscore'),
			Marionette = require('marionette'),
			ModelDrivenBrowseLayout = require('view/md-browse-layout'),
			GenericFormView = require('view/GenericFormView'),
			tmpl = require('hbs!template/md-search-layout'),
			vent = require('vent');
	var ModelDrivenSearchLayout = ModelDrivenBrowseLayout.extend({
		tagName : 'div',
		id: "calipsoModelDrivenSearchLayout",
		template : tmpl,
		regions : {
			sidebarRegion : "#calipsoModelDrivenSearchLayout-sideBarRegion",
			contentRegion : "#calipsoModelDrivenSearchLayout-contentRegion"
		},
		onShow: function(){
			this.showSidebar(this.model);
			this.showContent(this.model);
		},
		showSidebar: function(routeModel){
			var _this = this;
			// create the search form view
			var formView = new GenericFormView({formSchemaKey: "search", model: routeModel});
			
			// bind to events coresponding to successful
			// retreival of search results
			vent.on("genericFormSearched", function(wrapperModel) {
					console.log("ModelDrivenSearchLayout caught 'genericFormSearched' event: search form notified of retreived search results");
					
					// pick up the right view to render the 
					// search results collection
					var CollectionViewType = wrapperModel.getCollectionViewType();
					
					// create the search results view instance
					var searchResultsView = new CollectionViewType({model: wrapperModel});
					
					// show the results
					// TODO: bind to collection item selection events (view/edit)
					_this.contentRegion.show(searchResultsView);
			});
			
			// show the search form
			this.sidebarRegion.show(formView);
		}
	},
	// static members
	{
 		getTypeName: function(){return "ModelDrivenSearchLayout"}
	});
	return ModelDrivenSearchLayout;
});