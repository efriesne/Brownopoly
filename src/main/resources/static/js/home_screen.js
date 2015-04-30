$("#game_settings").hide(0);
$("#screen").hide(0);
$("#load_screen").hide(0);
// $("#home_screen").hide(0);

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

/*User can customize board, or load a previously saved theme*/
$("#customize_board_button").on('click', function(){

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
		gamePlay: JSON.stringify(game_play)/*,
		theme: {
			names: JSON.stringify(customNames),
			colors: JSON.stringify(customColors)
		}*/
	};

	$.post("/createGameSettings", postParameters, function(responseJSON){
		var responseObject = JSON.parse(responseJSON);
		var board = responseObject.board;
		var players = responseObject.state.players;
		//players is in correct turn order
		resetVariables();
		createBoard(board);
		setupPlayerPanel(players);
		num_players = players.length;
		for (var i = num_players; i < 6; i++) {
			var playerID = "#player_" + i;
			$(playerID).hide(0);
		}

		$("#screen").show(0);
		$("#home_screen").slideUp(500, startTurn());
		loadDeeds();
	});
});


/* Transitions from home screen to new game */
$("#home_newgame").bind('click', function() {
	$("#home_options").fadeOut(100);
	$("#game_settings").delay(100).fadeIn(200);
});

/************
  LOAD GAME
************/

$("#home_load").bind('click', function() {
	$.post("/getSavedGames", function(responseJSON) {
		var response = JSON.parse(responseJSON);
		if (response.error) {
			return;
		}
		var gameNames = response.games;
		$("#home_options").fadeOut(100, function() {
			$("#load_screen").fadeIn(100);
		});
		createSavedGames(gameNames);
	});
});

$("#load_screen").on("click", "tr", function(){
	$(this).parent().children("tr").removeClass("selected");
	$(this).addClass("selected");
});

//not working
$("#load_screen").find("tr").hover(function(){
	console.log("triggered");
	$(this).css("background", "#D1FBE4");
}, function() {
	$(this).css("background", $(this).parent().css("background"));
});

function createSavedGames(names) {
	var table = document.getElementById("saved_games_table");
	$(table).html("");
	names = names == undefined ? [] : names;
	for (var i = 0; i < names.length; i++) {
		var row = table.insertRow(i);
		var cell = row.insertCell(0);
		$(cell).text(names[i]);
	}
}

$("#load_game_button").on("click", function() {
	var selected = $("#saved_games_table tr.selected");
	if (selected.length) {
		var filename = selected.children().first().text();
		var params = {file: filename};
		$.post("/loadGame", params, function(responseJSON){
			var responseObject = JSON.parse(responseJSON);
			if (responseObject.error) {
				console.log(responseObject.error);
				return;
			}
			var board = responseObject.board;
			var players = responseObject.state.players;
			fastPlay = responseObject.fastPlay;
			//players is in correct turn order
			resetVariables();
			createBoard(board);
			setupPlayerPanel(players);
			num_players = players.length;
			//THIS FOR LOOP MAY CAUSE PROBLEMS
			for (var i = num_players; i < 6; i++) {
				var playerID = "#player_" + i;
				$(playerID).hide(0);
			}

			$("#screen").show(0);
			$("#home_screen").slideUp(500, startTurn());
			loadDeeds();
		});
	}
});

$("#load_clear").on("click", function(){
	$.post("/deleteSavedGames", function(responseJSON) {
		var resp = JSON.parse(responseJSON);
		if (resp.error) {
			console.log("unexpected error while deleting saved files");
		} else {
			createSavedGames([]);
		}
	});
});

$("#load_cancel").on("click", function() {
	$("#load_screen").fadeOut(100, function() {
			$("#home_options").fadeIn(100);
	});
});

/***************
 ERROR POPUP
***************/

//initiliaze error popup with its default values
//see function in utils.js
customizePopup();

$("#home_customize").on("click", function() {
	/*
	$("#monopoly_logo").fadeOut(100);
	$("#home_options").fadeOut(100);
	$("#customize_screen").delay(100).fadeIn(200);
	*/
	customizePopup({
		showNoButton: false,
		titleText: "Check back soon!",
		message: "Feature is still being developed."
	});
	$("#popup_error").show(0);
});


