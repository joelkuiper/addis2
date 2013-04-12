app.controller('PopulationsController', function($scope) {
	$scope.indication = {};
	$scope.indications = {	       
		initSelection : function (element, callback) {
	        var id = $(element).attr("value");
	        $.get(id, function(data) { 
		        callback({"id":id, "text": data.name});
	        });
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