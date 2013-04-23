app.controller('StudiesController', function($scope, $http) {
	$scope.studies = [];
	
	function retrieveStudies(data) { 
		var populationUrl = data.population.conceptUrl; 
		var outcomeUrls = _.pluck(data.outcomes, 'conceptUrl');
		var interventionsUrls = _.pluck(data.interventions, 'conceptUrl');
		var baseUrl = config.dataUrl + "/studies";
		$http.get(baseUrl 
				+ "?population=" + populationUrl 
				+ "&outcomes=" + outcomeUrls.join()
				+ "&interventions=" + interventionsUrls.join())
			.success(function(data) { $scope.studies = data; });
	}
	
	$http.get(".").success(retrieveStudies);
});