/******************
SAVING
*******************/

$("#save_button").on('click', function() {
	//post to backend to see if file is already saved or not
	$.post("/checkSaved", function(responseJSON) {
		var response = JSON.parse(responseJSON);
		var alreadyExists = response.exists;
		if (alreadyExists) {
			console.log("entered");
			//save(true);
			$("#popup_pause").hide(0);
			confirmOverwrite(response.filename);
		} else {
			$("#save_as_button").trigger('click');
			// //set popup save text for saving game
			// $("#popup_save center strong").text("To save your game, please enter an alphanumeric filename.");
			// $("#popup_save").show(0);
			// //reset buttons
			// $("#save_cancel").off().on('click', function() {
			// 	$("#popup_pause").show(0);
			// 	$("#popup_save").hide(0);
			// 	$("#save_filename").val("");
			// });
			// $("#save_submit").off().on('click', function() {
			// 	checkGameFilename();
			// });
		}
	});
});

$("#save_as_button").on('click', function() {
	$("#popup_pause").hide(0);
	//set popup save text for saving game
	$("#popup_save center strong").text("To save your game, please enter an alphanumeric filename.");
	$("#popup_save").show(0);
	//reset buttons
	$("#save_cancel").off().on('click', function() {
		$("#popup_pause").show(0);
		$("#popup_save").hide(0);
		$("#save_filename").val("");
	});
	$("#save_submit").off().on('click', function() {
		checkGameFilename();
	});
});


function checkGameFilename() {
	console.log("check called");
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
	console.log("confirm called");
	customizeAndShowPopup(
	{
		message: "A file with the name '" + name + "' already exists. Are you sure you want to overwrite?",
		okText: "Yes"
	}, {
		noHandler: function() {
			$("#popup_pause").show(0);
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
	console.log("exists: " + exists + ", name: " + filename);
	var params = {exists: JSON.stringify(exists)};
	if (filename != undefined) {
		params['file'] = filename;
	}
	$.post("/save", params, function(responseJSON) {
		var response = JSON.parse(responseJSON);
		if (response.error) {
			customizeAndShowPopup({
				showNoButton: false,
				message: "Unexpected error occurred while saving"
			}, {
				okHandler: function() {
					$("#popup_pause").show(0);
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
						//$("#popup_pause").show(0);
						$("#popup_resume").trigger('click');
					}
				});
			} else {
				customizeAndShowPopup({
					showNoButton: false,
					titleText: "SUCCESS",
					message: "You successfully saved the game as " + name
				}, {
					okHandler: function() {
						//$("#popup_pause").show(0);
						$("#popup_resume").trigger('click');
					}
				});
			}
		}
	});
}