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

	//save the default theme so it is available for the user
	//saveTheme("Default");
});

function assembleCustomization() {
	var inputList = document.getElementsByClassName("cust_input");
	var inputsFilled = 0;
	var pickersFilled = 0;
	var prevColor = "";
	for (var i = 0; i < defaultColors.length; i++) {
		var input = inputList[inputsFilled];
		var curr_color = defaultColors[i];
		if (curr_color.length > 0) {
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

			$(input).val("");
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
	var picker = $(this);
	var selected = tinycolor.toRgb();
	var red = selected.r;
	var green = selected.g;
	var blue = selected.b;
	var color = "rgb("+ red + "," + green + "," + blue + ")";
	var colorArr = [red, green, blue];

	var id = picker.data().id;
	var monopoly_id = "monopoly_" + id;

	var inputList = document.getElementsByClassName(monopoly_id);
	for (var i = 0; i < inputList.length; i++) {
		var input = inputList[i];
		$(input).css("border-left", "5px solid " + color);
		var boardIDX = $(input).data().boardIDX;
		customColors[boardIDX] = colorArr;
	}
});

//button functions assigned depending on whether Customize Board was clicked from Home Screen or Game Options

function gatherCustomNames() {
	var inputList = document.getElementsByClassName("cust_input");
	for (var i = 0; i < inputList.length; i++) {
		var input = inputList[i];
		var input_text = $(input).val().toUpperCase();
		if (input_text.trim() != "") {
			var boardIDX = $(input).data().boardIDX;
			customNames[boardIDX] = input_text.trim();
		}
	}
}

function checkThemeFilename() {
	//post to backend check for valid name
	var name = $("#save_filename").val();
	$("#save_filename").val("");
	var params = {
		name: name,
		isGame: false
	};
	$.post("/checkFilename", params, function(responseJSON) {
		var resp = JSON.parse(responseJSON);
		//if name invalid, tell them why
		if (!resp.valid) {
			$("#popup_save").hide(0);
			customizeAndShowPopup(
				{
					message: "Looks like you gave an invalid filename. Allowed characters: A-Z, a-z, 0-9, -, _, and spaces.",
					showNoButton: false
				}, {
					okHandler: function() {
						$("#popup_save").show(0);
					}
				});
		} else if (resp.exists) {
			//if file already exists, confirm they want to overwrite
			customizeAndShowPopup(
			{
				message: "This filename already exists. Are you sure you want to overwrite?",
				okText: "Yes"
			}, {
				okHandler: function() {
					saveTheme(name)
				}
			});
			$("#popup_save").hide(0);
		} else if (resp.valid) {
			saveTheme(name);
			$("#popup_save").hide(0);
		}
	});
}

function saveTheme(filename) {
	//saves the theme currently on the screen
	gatherCustomNames();
	var theme = {
		names: customNames,
		colors: customColors
	};
	var params = {
		file: filename,
		theme: JSON.stringify(theme)
	};
	$.post("/saveTheme", params, function(responseJSON){
		var resp = JSON.parse(responseJSON);
		if (resp.error) {
			customizeAndShowPopup({
				showNoButton: false,
				message: resp.error
			});
		} else {
			var name = resp.filename;
			customizeAndShowPopup({
				titleText: "SUCCESS",
				message: "You successfully saved the theme as " + name,
				showNoButton: false
			});
		}
	});
}



