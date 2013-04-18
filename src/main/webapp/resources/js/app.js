var app = angular.module('addis', ['ui']);

app.run(function($rootScope) {
	$rootScope.alerts = [];
});