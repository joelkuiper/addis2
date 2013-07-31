app.controller('AnalysesController', ['$scope', '$http', 'clinicico.tasks', function($scope, $http, tasks) {
	$scope.interventions = {};
	$scope.outcomes = {};
	$scope.analyses = {};
	$scope.outcome = "";
	
	$scope.$watch("outcome", function(newVal) {
		if (!newVal) return;
		if ($scope.analyses[newVal]) {
			$scope.consistency = $scope.analyses[newVal].consistency;
		} else {
			$scope.consistency = null;
			retrieveMeasurements(newVal);
		}
	});
	
	function retrieveData(data) {
		$scope.population = data.population;
		
		angular.forEach(data.interventions, function(intervention) {
			$scope.interventions[intervention.conceptUrl] = intervention.conceptProperties;
		});
		
		angular.forEach(data.outcomes, function(outcome) {
			$scope.outcomes[outcome.conceptUrl] = outcome.conceptProperties;
		});
	}
	
	function retrieveMeasurements(outcome) {
		if (!$scope.population || $scope.analyses[outcome]) return;
		
		$scope.analyses[outcome] = {};
		var baseUrl = config.dataUrl + "/measurements";
		var populationUrl = $scope.population.conceptUrl;
		var interventionsUrls = _.keys($scope.interventions);
		$http.get(baseUrl 
				+ "?population=" + populationUrl 
				+ "&outcome=" + outcome
				+ "&interventions=" + interventionsUrls.join())
			.success(function(data) { 
				$scope.analyses[outcome].measurements = data; 
				$scope.analyses[outcome].type = data[0].rate ? 'dichotomous' : 'continuous';
		});
	}
	
	$http.get(".").success(retrieveData);

	$scope.runGeMTC = function(outcome) {
		
		var data = _.map($scope.analyses[outcome].measurements, function(measurement) {
			var intervention = $scope.interventions[measurement.intervention];
			return { 
				study: measurement.studyId,
				treatment: intervention ? intervention.name : null,
				sampleSize: measurement['sample size'],
				"std.dev": measurement['standard deviation'],
				mean: measurement['mean'],
				responders: measurement['rate']
			};
		});
		
		data = _.reject(data, function(arm) { 
			return !arm.treatment;
		});
		
		var params = _.extend($scope.params || {}, {network: {data: data}});
		
		var task = tasks.submit("consistency", params);
		task.on("update", function(status) { 
			$scope.analyses[outcome].status = status;
		});
		task.on("error", function(status) { 
			$scope.analyses[outcome].status = status;
		});
		
		task.results.then(function(results) {
			function rewrite(data) {
				var map = {};
				_.each(data, function(element) {
					var names = element.parameter.split(".");
					if (names.length == 3) {
						if (!map[names[1]]) {
							map[names[1]] = {};
						}
							map[names[1]][names[2]] = _.omit(element, "parameter");
						}
					});
				return map;
			}
			results.body['relative_effects'] = rewrite(results.body['relative_effects']);
			
			var treatments = results.body['treatments'];
			var treatmentsNew = [];
			for(var i = 0; i < treatments.id.length; i++) { 
				treatmentsNew[i] = {id: treatments.id[i], description: treatments.description[i] };
			}
			results.body['treatments'] = treatmentsNew;
			
			results['_embedded']['_files'] =
			_.reduce(_.map(results['_embedded']['_files'], function(file) {
					return _.object([file.name], [_.omit(file, "name")]);
			}), function(memo, obj) { return _.extend(memo, obj); }, {});
			$scope.analyses[outcome].consistency = results;
		});
	};
}]);
