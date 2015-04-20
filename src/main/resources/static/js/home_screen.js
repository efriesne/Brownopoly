$("#game_settings").hide(0);
$("#screen").hide(0);

var num_players = 2;

/* creates a new row on the player creation table. */
$("#add_player_button").bind('click', function() {
	if (num_players < 6) {
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

		num_players--; 
		if (num_players < 3) {
			removeXs();
		}	
	} 

});

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

/* "Done" is clicked, data inputted should be read in,
 	the backend turns it into game settings, then the first turn is called */
$("#play_button").bind('click', function() {
	var playerList = [];
	var numAI = 0;
	var numHuman = 0;

	for (var i = 0; i < num_players; i++) {
		nameBoxID = "player_name_" + i;
		typeButtonID = "player_type_" + i;

		var nameBox = document.getElementById(nameBoxID);
		var name = nameBox.value;

		var selectedTypeButtonName = "input:radio[name=" + typeButtonID + "]:checked";
		var type = $(selectedTypeButtonName).val();


		var p = [name, type];
		playerList.push(p);

		if (type == "human") {
			numHuman++;
		} else if (type == "ai") {
			numAI++;
		}
	}

	var game_play = $("input:radio[name=game_play]:checked").val();

	var postParameters = {players: JSON.stringify(playerList), 
							numHuman: JSON.stringify(numHuman), 
							numAI: JSON.stringify(numAI),
							gamePlay: JSON.stringify(game_play)};

	$.post("/createGameSettings", postParameters, function(responseJSON){

	});
  

	$("#screen").show(0);
	$("#home_screen").slideUp(500);
	setupPlayerPanel(num_players);
	// startTurn();
});


/* Transitions from home screen to new game */
$("#home_newgame").bind('click', function() {
	$("#home_options").fadeOut(100);
	$("#game_settings").delay(100).fadeIn(200);
});