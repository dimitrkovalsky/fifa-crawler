var fifaApp = angular.module('fifaApp', ['ngResource']);

fifaApp.factory('Monitoring', function($resource) {
    return $resource('/monitoring/');
});