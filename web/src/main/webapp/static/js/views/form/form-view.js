define([ 'chaplin', 'views/base/view', 'text!templates/form/form.hbs' ],
		function(Chaplin, View, template) {
	'use strict';

	var FormView = View.extend({
		autoRender : true,
		container: '.main-content',
		id: 'main-form',
		tagName: 'form',
	    template: template,
	    events: {
	        'submit': '_onFormSubmit',
	        'load': '_onAddedToDOM',
	    },
	    listen: {

	    },
        render: function(options) {
            View.__super__.render.apply(this, options);

            this.$el.attr('novalidate', 'novalidate');

            return this;
        },
	    _doValidate: function() {
	        var data = new FormData();

            $('.generated').remove();
            $('.control-group').removeClass('error');
            $('.control-group').removeClass('warning');

            $(':input').each(function(index, element) {
                var name = element.name;
                if (name == undefined || name == null || name == '')
                    return;

                if (element.files !== undefined && element.files != null) {
                    $.each(element.files, function(fileIndex, file) {
                        if (file != null)
                            data.append(name, file);
                    });
                } else {
                    var $element = $(element);
                    var value = $(element).val();

                    if (($element.is(':radio') || $element.is(':checkbox'))) {
                        if ($element.is(":checked")) {
                            if (value != undefined)
                                data.append(name, value);
                        }
                    } else {
                        data.append(name, value);
                    }
                }
            });

            var sectionId = $('.sections > .section:first').attr('id');
            var url = this.model.get("link") + '/' + sectionId + '.json';

            $.ajax({
                url : url,
                data : data,
                processData : false,
                contentType : false,
                type : 'POST',
                statusCode : {
                    204 : this._onFormValid,
                    400 : this._onFormInvalid,
                    default : this._onFailure,
                }
            });
	    },
	    _onAddedToDOM: function(event) {
            Chaplin.mediator.publish('formAddedToDOM');
	    },
	    _onFormSubmit: function(event) {
	        var screen = this.model.get("screen");
            var type = screen.type;

            if (type == 'wizard') {
                this._doValidate();

                return false;
            }

	        return true;
	    },
	    _onFormValid: function(data, textStatus, jqXHR) {
            var next = $(':button[type="submit"]').val();
            Chaplin.mediator.publish('!router:route', next);
	    },
	    _onFormInvalid: function(jqXHR, textStatus, errorThrown) {
            var errors = $.parseJSON(jqXHR.responseText);

            if (errors.items != null) {
                for (var i=0;i<errors.items.length;i++) {
                    var item = errors.items[i];
                    var selector = ':input[name="' + item.propertyName + '"]';
                    var $input = $(selector);
                    if ($input.is(':checkbox') || $input.is(':radio')) {
                        $input.closest('.control-group').after('<div class="generated alert alert-' + item.type + '">' + item.message + '</div>');
                    } else {
                        $input.after('<div class="generated alert alert-' + item.type + '">' + item.message + '</div>');
                    }
                    //$input.closest('.control-group').addClass(item.type);
                }
            }
	    },
	    _onFailure: function(jqXHR, textStatus, errorThrown) {
            alert('Failure!');
	    }
	});

	return FormView;
});