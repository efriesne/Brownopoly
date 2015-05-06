/******************
SAVING
*******************/

var saveOn = false;

$("#save_button").on('click', function() {
	//post to backend to see if file is already saved or not
	$.post("/checkSaved", function(responseJSON) {
		var response = JSON.parse(responseJSON);
		var alreadyExists = response.exists;
		if (alreadyExists) {
			$("#popup_pause").hide(0);
			$("#paused_screen").hide(0);
			confirmOverwrite(response.filename);
		} else {
			$("#save_as_button").trigger('click');
		}
	});
});

$("#save_as_button").on('click', function() {
	saveOn = true;
	$("#popup_pause").hide(0);
	$("#paused_screen").hide(0);
	//set popup save text for saving game
	$("#popup_save center strong").text("To save your game, please enter an alphanumeric filename.");
	$("#popup_save").show(0);
	//reset buttons
	$("#save_cancel").off().on('click', function() {
		$("#popup_pause").show(0);
		$("#paused_screen").show(0);
		$("#popup_save").hide(0);
		$("#save_filename").val("");
		saveOn = false;
	});
	$("#save_submit").off().on('click', function() {
		checkGameFilename();
	});
});


function checkGameFilename() {
	//post to backend check for valid name
	var name = $("#save_filename").val();
	$("#save_filename").val("");
	var params = {
		name: name,
		isGame: true
	};
	$.post("/checkFilename", params, function(responseJSON) {
		var resp = JSON.parse(responseJSON);
		//if name invalid, tell them why
		if (!resp.valid) {
			saveOn = true;
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
			confirmOverwrite(name);
			$("#popup_save").hide(0);
		} else if (resp.valid) {
			save(false, name);
			$("#popup_save").hide(0);
		}
	});
}

function confirmOverwrite(name) {
	saveOn = true;
	customizeAndShowPopup(
	{
		message: "A file with the name '" + name + "' already exists. Are you sure you want to overwrite?",
		okText: "Yes"
	}, {
		noHandler: function() {
			$("#popup_pause").show(0);
			$("#paused_screen").show(0);
			saveOn = false;
		},
		okHandler: function(event) {
			save(event.data.exists, event.data.filename);
		},
		okHandlerData: {
			exists: false,
			filename: name
		}
	});
}

function save(exists, filename) {
	var params = {exists: JSON.stringify(exists)};
	if (filename != undefined) {
		params['file'] = filename;
	}
	$.post("/save", params, function(responseJSON) {
		saveOn = true;
		var response = JSON.parse(responseJSON);
		if (response.error) {
			customizeAndShowPopup({
				showNoButton: false,
				message: "Unexpected error occurred while saving"
			}, {
				okHandler: function() {
					saveOn = false;
					$("#popup_pause").show(0);
					$("#paused_screen").show(0);
				}
			});
		} else {
			var name = response.filename;
			if (exists) {
				customizeAndShowPopup({
					showNoButton: false,
					titleText: "SUCCESS",
					message: "You successfully overwrote the old version of " + name
				}, {
					okHandler: function() {
						saveOn = false;
						$("#resume_button").trigger('click');
					}
				});
			} else {
				customizeAndShowPopup({
					showNoButton: false,
					titleText: "SUCCESS",
					message: "You successfully saved the game as " + name
				}, {
					okHandler: function() {
						saveOn = false;
						$("#resume_button").trigger('click');
					}
				});
			}
		}
	});
}