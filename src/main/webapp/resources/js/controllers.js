app.controller('PopulationsController', function($scope, $http) {
	$scope.project = {};
	$scope.interventionUrl = "";
	$scope.interventionUrlFn = function() { return $scope.interventionUrl; };

	function getContentForConcept(concept) { 
		var names = _.pluck(concept, 'name');
		var links = _.flatten(_.pluck(concept, 'links'));
		var concepts = _.pluck(_.filter(links, function(link) { return link.rel === 'self'; }), 'href');
		
		return _.reduce(_.zip(names, concepts), function(acc, obj) {
			return _.union(acc, (_.object(['text', 'id'], obj)));
		}, []);
    }

	$scope.populationSelect = {	       
		ajax : {
			url : config.dataUrl + "/indications",
			data : function(term, page) {
				return {q: term};
			},
	
			results : function(data, page) {				
				return {
					results : getContentForConcept(data.content)
				};
			}
		}
	};
	
	$scope.interventionsSelect = {
		multiple: true,
		ajax : {
			url : $scope.interventionUrlFn,
			data : function(term, page) {
				return {q: term};
			},
	
			results : function(data, page) {
				var results = getContentForConcept(data);
				return {
					results : results
				};
			}
		}
	};
		
    $http.get(".").success(function(data) { 
		$scope.project = data;

	$scope._interventions = [];
		$scope._population = {id: data.population.conceptUrl, text: data.population.conceptProperties.name};
		angular.forEach(data.interventions, function(intervention) {
			$scope._interventions.push({id: intervention.conceptUrl, text: intervention.conceptProperties.name});
		});
	});
    
	$scope.$watch('_population', function() {
		if($scope.interventionUrl !== "") { 
			$scope._interventions = [];
		}
		if($scope._population && $scope._population.id) {  
			var url = config.dataUrl + "/treatments?indication=" + $scope._population.id;
			$scope.interventionUrl = url;
		}
	});
	
	$scope.save = function() { 
		$scope.project.population = {conceptUrl: $scope._population.id};
		$scope.project.interventions = [];

		angular.forEach($scope._interventions, function(intervention) { 
			$scope.project.interventions.push({conceptUrl: intervention.id});
		});

		$http.post(".", $scope.project).success(function(data, status, headers) { 
			console.log(data);
		});
	};
	
});