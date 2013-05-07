requirejs.config({
    baseUrl: 'static/js',
    paths: {
    	backbone: 'vendor/backbone-amd',
    	bootstrap: '../lib/bootstrap/js/bootstrap',
    	chaplin: 'vendor/chaplin',
    	css: 'vendor/css',
    	handlebars: 'vendor/handlebars',
    	jquery: 'vendor/jquery',
    	less: 'vendor/less',
    	normalize: 'vendor/normalize',
    	text: 'vendor/require-text-2.0.3',
        underscore: 'vendor/underscore-amd'
    },
    shim: {
    	'backbone':{deps: ['underscore','jquery'], exports: 'Backbone'},
        'bootstrap':{deps: ['jquery']},
        'handlebars':{exports: 'Handlebars'},
        'underscore':{exports: '_'}
    }
});

require(['applications/process-designer', 'routes/process-routes', 'bootstrap'], function(ProcessDesigner, routes, Bootstrap) {
    (new ProcessDesigner).initialize();
});