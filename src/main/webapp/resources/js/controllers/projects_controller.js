app.controller('ProjectsController', function($scope, $http) {
	$scope.project = {};

	function getContentForConcept(concept) { 
		var names = _.pluck(concept, 'name');
		var links = _.flatten(_.pluck(concept, 'links'));
		var concepts = _.pluck(_.filter(links, function(link) { return link.rel === 'self'; }), 'href');
		
		return _.reduce(_.zip(names, concepts), function(acc, obj) {
			return _.union(acc, (_.object(['text', 'id'], obj)));
		}, []);
    }

	
	function createSelectOptions(url, contentCallback) { 
		return { 
			ajax : {
				url : url,
				data : function(term, page) {
					return {q: term};
				},
		
				results : function(data, page) {				
					return {
						results : contentCallback(data)
					};
				}
			}
		};
	}
	
	$scope.populationSelect = 
		createSelectOptions(
			config.dataUrl + "/indications", 
			function(data) { return getContentForConcept(data.content); }
		);
	
	function createMultipleSelect(type) { 
		return createSelectOptions(
			function() { return config.dataUrl + "/" + type +  "?indication=" + $scope._population.id; },
			function(data) { return getContentForConcept(data); });
	};
	
	$scope.interventionsSelect = createMultipleSelect("treatments");
	$scope.outcomesSelect = createMultipleSelect("variables");
		
	var setProject = function(data) {
		$scope.project = data;
		
		$scope._population = {
			fresh: true,
			id: data.population.conceptUrl,
			text: data.population.conceptProperties ? data.population.conceptProperties.name : ""
		};
		
		$scope._interventions = [];
		angular.forEach(data.interventions, function(intervention) {
			$scope._interventions.push({id: intervention.conceptUrl, text: intervention.conceptProperties.name});
		});
		
		$scope._outcomes = [];
		angular.forEach(data.outcomes, function(outcome) {
			$scope._outcomes.push({id: outcome.conceptUrl, text: outcome.conceptProperties.name});
		});
	};
		
    $http.get(".").success(setProject);
    
	$scope.$watch('_population', function() {
		if($scope._population && !$scope._population.fresh) { 
			$scope._interventions = [];
			$scope._outcomes = [];
		}
	});
	
	$scope.save = function() {
		$scope.project.population = {conceptUrl: $scope._population.id};
		$scope.project.interventions = [];
		$scope.project.outcomes = [];

		angular.forEach($scope._interventions, function(intervention) { 
			$scope.project.interventions.push({conceptUrl: intervention.id});
		});
		angular.forEach($scope._outcomes, function(outcome) { 
			$scope.project.outcomes.push({conceptUrl: outcome.id});
		});
		$http.post(".", $scope.project)
		.success(function(data) { 
		    $scope.alerts.push({ type: 'alert-success', msg: 'Updated project!' }); 
			setProject(data);
		})
		.error(function(data, status, header) { 
		    console.error(data);
		    $scope.alerts.push({ type: 'alert-error', msg: 'Failed to update the project. HTTP status: ' + status });
		});
	};
});