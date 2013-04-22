var app = angular.module('addis', ['ui']).config(function($httpProvider) {
	$httpProvider.defaults.headers.post['X-CSRFToken'] = $('input[name=X-CSRFToken]').val();
});

app.run(function($rootScope) {
	$rootScope.alerts = [];
});