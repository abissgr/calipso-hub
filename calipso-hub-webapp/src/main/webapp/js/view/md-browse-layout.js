define(function(require) {
		var _ = require('underscore'),
			Marionette = require('marionette'),
			vent = require('vent'),
			tmpl = require('hbs!template/md-browse-layout');
	var ModelDrivenBrowseLayout = Marionette.Layout.extend({
		tagName : 'div',
		id: "calipsoModelDrivenBrowseLayout",
		template : tmpl,// _.template(templates.applayout),
		regions : {
			contentRegion : "#calipsoModelDrivenBrowseLayout-contentRegion"
		},
		onShow: function(){
			this.showContent(this.model);
		},
//		initialize: function(options){
//
//			Marionette.Layout.prototype.initialize.apply(this, arguments);
//			if(options.model){
//				this.model = options.model;
//			}
//			if(this.options.searchResultsCollection){
//				this.searchResultsCollection = options.searchResultsCollection;
//			}
//			console.log("GenericSearchLayout.initialize, model: " + (this.model ? this.model.getTypeName() : this.model));
//			console.log("GenericSearchLayout.initialize, searchResultsCollection: " + (this.searchResultsCollection ? this.searchResultsCollection.length : this.searchResultsCollection));
//	  },
		showContent: function(routeModel){
			var _this = this;
			console.log("ModelDrivenBrowseLayout.showContent: "+routeModel.get("id"));
			// get the model collection view type
			var ContentViewType = routeModel.getCollectionViewType();
			// create a new collection instance
			var contentView = new ContentViewType({model: routeModel});
			// register for grid item events
			vent.on("ViewInTabCell:viewGridRow", function(itemModel) {
				//  get item view type for model
         	var ItemViewType = itemModel.getItemViewType();
         	console.log("ModelDrivenBrowseLayout on itemView:openGridRowInTab, ItemViewType: "+ItemViewType.getTypeName());
         	// create new item view instance with model
         	var itemView = new ItemViewType({formSchemaKey: "view", model: itemModel});
         	// TODO: register for close and return to results
         	
         	// show item view
         	_this.contentRegion.show(itemView);
         });
			//TODO reuse active view if of the same type
			this.contentRegion.show(contentView);
		}
	},
	// static members
	{
 		getTypeName: function(){return "ModelDrivenBrowseLayout"}
	});
	return ModelDrivenBrowseLayout;
});