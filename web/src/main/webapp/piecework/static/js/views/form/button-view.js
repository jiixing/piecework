define([ 'chaplin', 'views/base/view', 'text!templates/form/button.hbs' ],
		function(Chaplin, View, template) {
	'use strict';

	var ButtonView = View.extend({
		autoRender : true,
	    template: template,
	});

	return ButtonView;
});