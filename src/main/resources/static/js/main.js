//can initialize stuff (widths, etc)

$(".results").hide();
$("#path").hide();
var hasText = false;

$(".userinput").on('keyup', function(event) {
	var element = $(this);
	var text = element.val();
	var results = element.next();

	//Error thrown when posting empty strings, so added this if statement
	if (!text) {
		hasText = false;
		results.find("p").text(function(index) {
			return "";
		});
		//.next() will give the results table for the corresponding textarea
		results.hide();
		$("#path").show();
	} else {
		hasText = true;
		$.post("/bacon", {input:JSON.stringify(text)}, function(response) {
			responseObject = JSON.parse(response);
			var sug = responseObject.suggestions;
			results.find("p").html(function(index) {
				return sug[index];
			});
			if (isSuggestionsEmpty(sug)) {
				hasText = false;
				results.hide();
				$("#path").show();
			} else {
				hasText = true;
				results.show();
				$("#path").hide();
			}
		});	
	}
});

$("#submitbutton").on('click', function() {
	$(".results").hide();
	var name1 = $("#start").val();
	var name2 = $("#end").val();
	if (!name1 || !name2) {
		return;
	} else {
		$.post("/route", {name1:JSON.stringify(name1), name2:JSON.stringify(name2)}, function(response) {
			responseObject = JSON.parse(response);
			var pathText = responseObject.path;
			$("#path").html(pathText);
			$("#path").show();
		});
	}

});

function isSuggestionsEmpty(suggestions) {
	for (var i = 0; i < suggestions.length; i++) {
		if (suggestions[i] != "") {
			return false;
		}
	}
	return true;
}
