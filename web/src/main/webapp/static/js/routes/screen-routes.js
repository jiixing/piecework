define(['controllers/screen-designer-controller'], function() {
  'use strict';

  // The routes for the application. This module returns a function.
  // `match` is match method of the Router
  return function(match) {
    match('', 'screen-designer#index');
  };
});
