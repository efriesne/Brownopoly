$("#customize_screen").hide(0);

var defaultColors;
var defaultNames;

var customColors;
var customNames;

$.post("/loadDefaults", function(responseJSON){
	var responseObject = JSON.parse(responseJSON);
	defaultColors = responseObject.defaultColors;
	defaultNames = responseObject.defaultNames;

	customColors = defaultColors.slice(0);
	customNames = defaultNames.slice(0);

	assembleCustomization();
});

function assembleCustomization() {
	var inputList = document.getElementsByClassName("cust_input");
	var inputsFilled = 0;
	var pickersFilled = 0;
	var prevColor = "";
	for (var i = 0; i < defaultColors.length; i++) {
		var input = inputList[inputsFilled];
		var curr_color = defaultColors[i];
		if (curr_color != null) {
			var red = curr_color[0];
			var green = curr_color[1];
			var blue = curr_color[2];
			var color = "rgb("+ red + "," + green + "," + blue + ")";
			if (prevColor != color) {
				var picker_id = "picker_" + pickersFilled;
				var picker = document.getElementById(picker_id);
				$(picker).spectrum({
					color: color,
					showInitial: true
				});
				pickersFilled++;
			}
			prevColor = color;
			input.className = input.className + " monopoly_" + (pickersFilled-1);

			$(input).css("border-left", "5px solid " + color);
			inputsFilled++;

			$(input).text("");
			$(input).attr("placeholder", correctCapitalization(defaultNames[i]));
			$(input).data("boardIDX", i);
		}
	}

	var RR_UTIL_inputList = document.getElementsByClassName("cust_ru");
	for (var i = 0; i < RR_UTIL_inputList.length; i++) {
		var input = RR_UTIL_inputList[i];
		var boardIDX = $(input).data().id;
		$(input).data("boardIDX", boardIDX);
		$(input).text("");
		$(input).attr("placeholder", correctCapitalization(defaultNames[boardIDX]));
	}
}

function correctCapitalization(string) {
	var split = string.split(" ");
	var rebuilt = "";
	for (var i = 0; i < split.length; i++) {
		var word = split[i];
		var lowerWord = word.toLowerCase();
		var endWord = lowerWord.substring(1, lowerWord.length);
		var first  = lowerWord.charAt(0).toUpperCase();
		rebuilt += first + endWord + " ";
	}

	return rebuilt.substring(0, rebuilt.length-1);
}


$(".picker").on('change.spectrum', function(e, tinycolor) { 
	var picker = this;
	var selected = tinycolor.toRgb();
	var red = selected.r;
	var green = selected.g;
	var blue = selected.b;
	var color = "rgb("+ red + "," + green + "," + blue + ")";
	var colorArr = [red, green, blue];

	var id = $(picker).data().id;
	var monopoly_id = "monopoly_" + id;

	var inputList = document.getElementsByClassName(monopoly_id);
	for (var i = 0; i < inputList.length; i++) {
		var input = inputList[i];
		$(input).css("border-left", "5px solid " + color);
		var boardIDX = $(input).data().boardIDX;
		customColors[boardIDX] = colorArr;
	}
});


$("#cust_save_button").on("click", function() {
	var inputList = document.getElementsByClassName("cust_input");
	for (var i = 0; i < inputList.length; i++) {
		var input = inputList[i];
		var input_text = $(input).text();
		if (input_text.trim() != "") {
			var boardIDX = $(input).data().boardIDX;
			customNames[boardIDX] = input_text.trim();
		}
	}

	/* make a post request to send the lists to the backend */
});

$("#cust_cancel_button").on("click", function(){
	$("#customize_screen").hide(0);
	assembleCustomization();
	$("#monopoly_logo").fadeIn(200);
	$("#home_options").fadeIn(200);
});



