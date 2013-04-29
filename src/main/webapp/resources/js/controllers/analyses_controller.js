app.controller('AnalysesController', function($scope, $http) {
	$scope.measurements = [];
	$scope.interventions = {};
	
	function retrieveMeasurements(data) { 
		var populationUrl = data.population.conceptUrl; 
		var outcomeUrls = _.pluck(data.outcomes, 'conceptUrl');
		var interventionsUrls = _.pluck(data.interventions, 'conceptUrl');
		
		angular.forEach(data.interventions, function(intervention) {
			$scope.interventions[intervention.conceptUrl] = intervention.conceptProperties;
		});
		
		var baseUrl = config.dataUrl + "/measurements";
		$http.get(baseUrl 
				+ "?population=" + populationUrl 
				+ "&outcome=" + outcomeUrls[0]
				+ "&interventions=" + interventionsUrls.join())
			.success(function(data) { $scope.measurements = data; });
	}
	
	$http.get(".").success(retrieveMeasurements);
});
