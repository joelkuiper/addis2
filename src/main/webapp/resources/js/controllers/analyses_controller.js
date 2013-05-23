app.controller('AnalysesController', ['$scope', '$http', 'clinicico.tasks', function($scope, $http, tasks) {
	$scope.measurements = [];
	$scope.interventions = {};
	$scope.type = '';
	$scope.analysis = {};

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
			.success(function(data) { 
				$scope.measurements = data; 
				$scope.type = data[0].rate ? 'dichotomous' : 'continuous';
		});
	}
	
	$http.get(".").success(retrieveMeasurements);

	$scope.runGeMTC = function() {
		
		var data = _.map($scope.measurements, function(measurement) {
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
			$scope.status = status;
		});
		task.on("error", function(status) { 
			$scope.status = status;
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
			console.log(results);
			$scope.consistency = results;
		});
	};
}]);
