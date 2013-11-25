// not (yet?) used
define(['backbone', 'resthub', 'view/user-form-view', 'hbs!template/user'], function(Backbone, Resthub, UserFormView, userTemplate) {

  var UserView = Resthub.View.extend({
    root: '.user-list',
    strategy: 'append',
    template: taskTemplate,
    tagName: 'li',
    className: 'user',
    events: {
      click: 'toggleDetails',
      dblclick: 'edit'
    },
    initialize: function() {      
      this.listenTo(this.model, 'sync', this.render);
      this.listenTo(this.model, 'change', this.render);
      this.listenTo(this.model, 'destroy', this.remove);
    },
    edit: function() {
    	console.log("user-view edit");
      var userFormView = new UserFormView({root: this.$el, model: this.model});
      userFormView.render();
    },
    toggleDetails: function() {
      this.$('p').slideToggle();
    }
  });

  return UserView;
});
