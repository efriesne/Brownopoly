/*************
  MAIN MENU
**************/

$("#home_help").on('click', function() {
	$("#help_center").fadeIn(200);
	helpOn = true;
});

/* Transitions from home screen to new game */
$("#home_newgame").on('click', function() {
	$("#home_options").fadeOut(100, function() {
		//$("#current_theme_label").hide(0);
		updateGamePlayDescription();
		$("#game_settings").fadeIn(200);
	});
});

$("#home_customize").on("click", function() {
	$("#monopoly_logo").fadeOut(100);
	$("#home_options").fadeOut(100, function() {
		$("#customize_screen").fadeIn(200);
	});

	$("#cust_back_button").off().on("click", function(){
		$("#customize_screen").hide(0);
		assembleCustomization();
		$("#monopoly_logo").fadeIn(200);
		$("#home_options").fadeIn(200);
	});
});


$("#home_load").on('click', function() {
	//change the title text
	$("#load_screen").children("strong").first().text("Please select a game to load:");
	loadingGames = true;
	//hide create new and use default buttons (only for theme load screen)
	$("#create_new").hide(0);
	$("#use_default_button").hide(0);
	//reset button handlers to handle loading games rather than loading themes
	$("#load_clear").off().on("click", function(){
		deleteAllSavedData(true);
	});
	$("#load_data_button").off().on("click", function() {
		loadGame();
	});
	$("#load_cancel").off().on("click", function() {
		//deselect everything
		$("#load_screen tr").removeClass("selected");
		//hide load screen, show home options
		$("#load_screen").fadeOut(100, function() {
			$("#home_options").fadeIn(100);
		});
	});
	//load saved games
	loadData(true);
	//hide options, show saved games
	$("#home_options").fadeOut(100, function() {
		$("#load_screen").fadeIn(100);
	});
});

/*************
 GAME SETTINGS
*************/

function clearGameSettings() {
	//remove Xs
	removeXs();
	//clear player table
	num_players = 2;
	resetIDs();
	$("#player_creation_table").find("tr").each(function(index) {
		//clear other players' rows
		if (index > 1) {
			$(this).remove();
			return;
		}
		//empty the name
		$(this).find("input:text").val("");
		//select human
		$(this).find("input[value='human']").prop('checked', true);
	});
	//select normal
	$("#game_settings").find("input[name='game_play'][value='normal']").prop('checked', true);
	//clear/hide current theme label
	$("#current_theme_label").children("span").text("");
	$("#current_theme_label").hide(0);

}

/* creates a new row on the player creation table. */
$("#add_player_button").bind('click', function() {
	if (num_players < MAX_PLAYERS) {
		num_players++;

		var player_table = document.getElementById("player_creation_table");
		var num_rows = player_table.rows.length;

		var row = player_table.insertRow(num_rows);
		var cell0 = row.insertCell(0);

		var cell1 = row.insertCell(1);
		cell1.innerHTML = "Player:";

		var cell2 = row.insertCell(2);
		var inputBox = document.createElement("input");
		inputBox.type = "text";
		inputBox.id = "player_name_" + (num_players - 1);
		cell2.appendChild(inputBox);

		var cell3 = row.insertCell(3);
		var humanButt = document.createElement("input");
		humanButt.type = "radio";
		humanButt.name = "player_type_" + (num_players - 1);
		humanButt.value = "human";
		humanButt.checked = "checked";
		cell3.appendChild(humanButt);
		var label = document.createTextNode(" Human");
		cell3.appendChild(label);

		var cell4 = row.insertCell(4);
		var aiButt = document.createElement("input");
		aiButt.type = "radio";
		aiButt.name = "player_type_" + (num_players - 1);
		aiButt.value = "ai";
		cell4.appendChild(aiButt);
		label = document.createTextNode(" AI");
		cell4.appendChild(label);

		if(num_players > 2) {
			addXs();
		}
	}
});

$("input[name=game_play]:radio").on("change", function () {
	updateGamePlayDescription();
});

function updateGamePlayDescription() {
	var game_play = $("input:radio[name=game_play]:checked").val();
	if (game_play == "fast") {
		$("#game_play_description").html("Players start off with 3 random " +
			"properties, must post bail immediately when sent to jail, " +
		"and may build a hotel after only 3 houses (instead of 4). " +
		"As soon as one player is bankrupt, the player with the highest " +
		"cumulative wealth (cash, property mortgage values, and house/hotel selling values) wins.");
	} else {
		$("#game_play_description").html("Normal play follows the Monopoly rules listed in the \"HELP\" section of the main menu.");
	}
}


/* When there is a click on the player_creation_table, if it's the 
   td that corresponds to an x, delete the player. */
$("#player_creation_table").delegate("td", "click", function() {
	var td = $(this);
	if (td.index() == 0 && td.text().trim() == "✖") {
		var row_idx = td.closest('tr').index()
		document.getElementById("player_creation_table").deleteRow(row_idx);
		resetIDs();
		num_players--; 
		if (num_players < 3) {
			removeXs();
		}	
	} 
});

function resetIDs() {
	$("#player_creation_table tr td").each(function() {
		var td = $(this);
		var row_idx = td.closest('tr').index()
		if (td.index() == 2) {
			var inputBox = td.find('input');
			inputBox.attr("id", "player_name_" + row_idx);
		} else if (td.index() == 3) {
			var typeButt = td.find('input');
			typeButt.attr("name", "player_type_" + row_idx);
		} else if (td.index() == 4) {
			var typeButt = td.find('input');
			typeButt.attr("name", "player_type_" + row_idx);
		}
	});
}

/* Adds the Xs to be clicked on for the player creation */
function addXs() {
	$("#player_creation_table tr td:first-child").each(function() {
		$(this).text("✖");
	});
}

/* Removes Xs if there are only 2 players left */
function removeXs() {
	$("#player_creation_table tr td:first-child").each(function() {
		$(this).text("");
	});
}

/*User wants to go back to the main menu*/
$("#settings_back_button").on('click', function() {
	$("#game_settings").fadeOut(100, function() {
		$("#home_options").fadeIn(200);
	});
});

/*User can customize board, or load a previously saved theme*/
$("#customize_board_button").on('click', function(){
	//change the title text
	$("#load_screen").children("strong").first().text("Please select a theme to load:");
	loadingGames = false;
	//show create new and use default buttons (only for theme load screen)
	$("#create_new").show(0);
	$("#use_default_button").show(0);
	//reset button handlers to handle loading themes rather than loading games
	$("#load_clear").off().on("click", function(){
		deleteAllSavedData(false);
	});
	$("#load_data_button").off().on("click", function() {
		loadTheme();
	});
	$("#load_cancel").off().on("click", function() {
		//deselect everything
		$("#load_screen tr").removeClass("selected");
		//hide load screen, show game settings
		$("#load_screen").fadeOut(100, function() {
			$("#game_settings").fadeIn(100);
		});
	});
	//load saved themes
	loadData(false);
	//hide options, show saved games
	$("#game_settings").fadeOut(100, function() {
		$("#load_screen").fadeIn(100);
	});
});

/* "Done" is clicked, data inputted should be read in,
 	the backend turns it into game settings, then the first turn is called */
$("#play_button").on('click', function() {
	var playerList = [];
	for (var i = 0; i < num_players; i++) {
		nameBoxID = "player_name_" + i;
		typeButtonID = "player_type_" + i;

		var nameBox = document.getElementById(nameBoxID);
		var name = nameBox.value;

		var selectedTypeButtonName = "input:radio[name=" + typeButtonID + "]:checked";
		var type = $(selectedTypeButtonName).val();

		var p = [name, type];
		playerList.push(p);
	}

	var game_play = $("input:radio[name=game_play]:checked").val();

	var postParameters = {
		players: JSON.stringify(playerList),
		gamePlay: JSON.stringify(game_play),
		theme: JSON.stringify({
			names: customNames,
			colors: customColors
		})
	};

	$.post("/createGameSettings", postParameters, function(responseJSON){
		var responseObject = JSON.parse(responseJSON);
		var board = responseObject.board;
		var players = responseObject.state.players;
		//players is in correct turn order
		resetVariables();
		fastPlay = responseObject.state.fastPlay;
		housesForHotel = responseObject.state.numHousesForHotel;
		createBoard(board);
		setupPlayerPanel(players);
		num_players = players.length;
		for (var i = num_players; i < MAX_PLAYERS; i++) {
			var playerID = "#player_" + i;
			$(playerID).hide(0);
		}
		enableAll();
		$("#screen").show(0);
		$("#home_screen").slideUp(500, startTurn());
		loadDeeds();
	});
});

/***********************
LOADING GAMES AND THEMES
************************/

$("#load_screen").on("click", "tr", function(){
	$(this).parent().children("tr").removeClass("selected");
	$(this).addClass("selected");
});



function createSavedData(names) {
	var table = document.getElementById("saved_games_table");
	$(table).html("");
	names = names == undefined ? [] : names;
	for (var i = 0; i < names.length; i++) {
		var row = table.insertRow(i);
		var cell = row.insertCell(0);
		$(cell).text(names[i]);
	}
}

$("#create_new").on('click', function() {
	//open up customize page
	$("#monopoly_logo").fadeOut(100);
	$("#load_screen").fadeOut(100, function() {
		$("#customize_screen").fadeIn(200);
	});
	//reset button handlers
	//make sure clicking back/cancel/save brings back to game settings, not home screen
	$("#cust_back_button").off().on("click", function(){
		loadData(false);
		$("#customize_screen").hide(0);
		assembleCustomization();
		$("#monopoly_logo").fadeIn(200);
		$("#load_screen").fadeIn(200);
	});
});

$("#use_default_button").on('click', function() {
	customNames = defaultNames.slice(0);
	customColors = defaultColors.slice(0);
	$("#current_theme_label").hide(0);
	$("#load_cancel").trigger("click");
});

$("#cust_save_button").on("click", function() {
	//set popup save text for saving game
	$("#popup_save center strong").text("To save your theme, please enter an alphanumeric filename.");
	$("#popup_save").show(0);
	//reset buttons
	$("#save_cancel").off().on('click', function() {
		$("#popup_save").hide(0);
		$("#save_filename").val("");
	});
	$("#save_submit").off().on('click', function() {
		checkThemeFilename();
	});
});

function loadData(isGames) {
	$.post("/getSavedData", {isGames: JSON.stringify(isGames)}, function(responseJSON) {
		var response = JSON.parse(responseJSON);
		if (response.error) {
			var data = isGames ? "games." : "themes.";
			customizeAndShowPopup({
				message: "Unexpected error occurred while retrieving saved " + data,
				showNoButton: false
			});
			return;
		}
		var dataNames = response.names;
		createSavedData(dataNames);
	});
}

function deleteData(filename, isGame) {
	var params = {
		isGame: isGame,
		filename: filename
	}
	$.post("/deleteData", params, function (responseJSON) {
		var resp = JSON.parse(responseJSON);
		if (resp.error) {
			customizeAndShowPopup({
				message: "Unexpected error occurred while deleting " + filename,
				showNoButton: false
			});
			return;
		}
		var dataNames = resp.names;
		createSavedData(dataNames);
	});
}

function deleteAllSavedData(isGames) {
	$.post("/clearSavedData", {isGames: JSON.stringify(isGames)}, function(responseJSON) {
		var resp = JSON.parse(responseJSON);
		if (resp.error) {
			var data = isGames ? "games." : "themes.";
			customizeAndShowPopup({
				message: "Unexpected error occurred while deleting saved " + data,
				showNoButton: false
			});
		} else {
			createSavedData([]);
		}
	});
}

function loadGame() {
	var selected = $("#saved_games_table tr.selected");
	if (selected.length) {
		var filename = selected.children().first().text();
		var params = {file: filename};
		$.post("/loadGame", params, function(responseJSON){
			var responseObject = JSON.parse(responseJSON);
			if (responseObject.error) {
				customizeAndShowPopup({
					showNoButton: false,
					message: responseObject.error
				});
				return;
			}
			resetVariables();
			var board = responseObject.board;
			var players = responseObject.state.players;
			housesForHotel = responseObject.state.numHousesForHotel;
			fastPlay = responseObject.state.fastPlay;
			//players is in correct turn order
			createBoard(board);
			setupPlayerPanel(players);
			num_players = players.length;
			for (var i = num_players; i < MAX_PLAYERS; i++) {
				var playerID = "#player_" + i;
				$(playerID).hide(0);
			}
			enableAll();
			$("#screen").show(0);
			$("#home_screen").slideUp(500, startTurn());
			loadDeeds();
		});
	}
}

function loadTheme() {
	var selected = $("#saved_games_table tr.selected");
	if (selected.length) {
		var filename = selected.children().first().text();
		var params = {file: filename};
		$.post("/loadTheme", params, function(responseJSON){
			var resp = JSON.parse(responseJSON);
			if (resp.error) {
				customizeAndShowPopup({
					showNoButton: false,
					message: resp.error
				});
				return;
			}
			customColors = resp.theme.colors;
			customNames = resp.theme.names;
			$("#current_theme_label").children("span").text(filename);
			$("#current_theme_label").show(0);
			$("#load_cancel").trigger("click");
		});
	}
}

/***************
 ERROR POPUP
***************/

//initiliaze error popup with its default values
//see function in utils.js
customizePopup();

$("#home_customize").on("click", function() {
	$("#monopoly_logo").fadeOut(100);
	$("#home_options").fadeOut(100);
	$("#customize_screen").delay(100).fadeIn(200);
});



