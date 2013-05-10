app.controller('AnalysesController', function($scope, $http, Jobs) {
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
	
	
	$scope.$on('completedAnalysis', function(e, job) {
		var results = job.results.results.consistency;
		results = _.object(_.map(results, function(x) { return x.name; }), results);
		function rewrite(data) {
			var map = {};
			_.each(data, function(element, name) {
				var names = name.split(".");
				if (names.length == 3) {
					if (!map[names[1]]) {
						map[names[1]] = {};
					}
					map[names[1]][names[2]] = element;
				}
			});
			return map;
		}
		results['relative_effects'].data = rewrite(results['relative_effects'].data);
		$scope.consistency = results;
	});

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
		
		var run = function(type) {
			var params = _.extend($scope.params, {network: {data: data}});
			$.ajax({
				url: config.gemtcUrl + type.toLowerCase(),
				type: 'POST',
				data: JSON.stringify(params),
				dataType: "json",
				contentType: 'application/json',
				success: function(responseJSON, textStatus, jqXHR) {
					var job = Jobs.add({
						data: responseJSON,
						type: 'run' + type,
						analysis: 1,
						broadcast: 'completedAnalysis'
					});
					$scope.analysis.job = job;
				}
			});
		};
		run('Consistency');
	};
});
