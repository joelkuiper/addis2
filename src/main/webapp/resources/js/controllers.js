app.controller('PopulationsController', function($scope, $http) {
	$scope.indication = {};
	var id = document.getElementById("population.indicationConceptUrl").getAttribute("value");
    if(id !== "") { 
    	$http.get(id).success(function(data) {
        	$scope.indication = {"id":id, "text": data.name};
    	});
    }
    
	$scope.indications = {	       
		initSelection : function (element, callback) {
			  callback($(element).data('$ngModelController').$modelValue);
	    },
		ajax : {
			url : config.dataUrl + "/indications",
			data : function(term, page) {
				return {};
			},

			results : function(data, page) {
				var content = data.content;
				var names = _.pluck(content, 'name');
				var links = _.flatten(_.pluck(content, 'links'));
				var concepts = _.pluck(_.filter(links, function(link) { return link.rel === 'self'; }), 'href');
				
				var data = _.reduce(_.zip(names, concepts), function(acc, obj) {
					return _.union(acc, (_.object(['text', 'id'], obj)));
				}, []);
				
				return {
					results : data
				};
			}
		}
	};
});