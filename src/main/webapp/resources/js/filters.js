/* Filters */
angular.module('app.filters', []).
filter('precision', function() {
	return function(input, decimals) {
		function isNumber(n) {
			return !isNaN(n) && isFinite(n);
		}
		if(isNumber(input)) {
			return input.toFixed(decimals);
		} else {
			return input;
		}
	};
});
