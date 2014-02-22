define([ 'backbone', 'backbone-bootstrap-modal', 'backbone-forms', 'backgrid', 'model/generic-model' ],
		function(Backbone, BackboneBootstrapModal, BackboneForm, Backgrid, GenericModel) {
			var HostModel = GenericModel.extend({
				// urlRoot: function() {
				// console.log("HostModel#urlRoot");
				// return window.calipso.getBaseUrl() + "/api/rest/host";
				// },
				
				schemaComplete : function() {
					return {//
						domain : {
							"search": 'Text',
							"default": {
								type: 'Text',
								validators : [ 'required' ]
							}
						},
					};
				},

				
			
			});

			var HostEditCell = Backgrid.Cell.extend({
				template : _.template('<button class="btn btn-warning">edit</button>'),
				events : {
					"click" : "editRow"
				},
				editRow : function(e) {
					console.log("Hello");
					e.preventDefault();
					var host = this.model;
					var hostSchema = host.isNew() ? host.schema("create") : host.schema("update");
					var form = new Backbone.Form({
						model : host,
						schema : hostSchema,
					});

					var modal = new Backbone.BootstrapModal({
						title : 'My form',
						content : form,
						okBtn : 'save'
					}).open();

					modal.on('ok', function() {
						var errs = form.commit();
						if (errs){
							return modal.preventClose();
						}
						var sUrl = host.url+"/"+host.get("id");
					    //(base.charAt(sUrl.length - 1) === '/' ? '' : '/') + encodeURIComponent(this.id);
						host.save({}, {url: sUrl});
						//host.save();
					});
				},
				render : function() {
					this.$el.html(this.template());
					this.delegateEvents();
					return this;
				}
			});

			HostModel.prototype.getDefaultSchemaForGrid = function() {
				return [
				{
					name : "domain",
					label : "domain",
					cell : "string"
				}, {
					name : "edit",
					label : "edit",
					editable : false,
					cell : HostEditCell
				}];
			}
			console.log("HostModel done");
			return HostModel;
		});