define([ 'calipso', 'hbs!template/HomeLayout'],
		function(Calipso, template) {
	var LandingLayout = Calipso.view.ModelDrivenBrowseLayout.extend({
		className : "",
		template : template,
		events:{
			"click a.howTo" : "toggleHowTo",
			"click a.howTo-collapse" : "toggleHowTo",
		},
		regions : {
		},

		toggleHowTo : function(e) {
			var _this = this;
			console.log("toggle how to");
			Calipso.stopEvent(e);
			var $howTo = _this.$el.find(".howTo-content").first();
			if (!$howTo.hasClass("howToOpen")) {
				$howTo.slideDown().addClass("howToOpen");
				$("html, body").animate({
		          scrollTop: $howTo.offset().top + 200 -$(window).height()
		        }, 500);
			} else {
				$howTo.slideUp().removeClass("howToOpen");
				$("html, body").animate({
		          scrollTop: 0
		        }, 500);
			}
		},
		
		containerClassHeader : "container",
		containerClassMain : "container",
		containerClassFooter : "container",
		onShow : function() {
			var _this = this;
			console.log("HomeLayout#onShow");
		}
	}, {
		getTypeName : function() {
			return "LandingLayout";
		}
	});

	return LandingLayout;
});
