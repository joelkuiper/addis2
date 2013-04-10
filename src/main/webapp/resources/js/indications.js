$.get(dataUrl + "/indications", function(data)  {
	var indications = data.content;
	$(indications).each(function(idx, indication) { 
		$("#indications").append("<option value='" + indication.links[0].href + "'>" + indication.name + "</option>");
	});
});