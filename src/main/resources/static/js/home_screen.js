$("#game_settings").hide(0);


var num_players = 2;

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
		inputBox.name = "player_name_" + num_players;
		cell2.appendChild(inputBox);

		var cell3 = row.insertCell(3);
		var humanButt = document.createElement("input");
		humanButt.type = "radio";
		humanButt.name = "player_type_" + num_players;
		humanButt.value = "ai";
		cell3.appendChild(humanButt);
		var label = document.createTextNode(" Human");
		cell3.appendChild(label);

		var cell4 = row.insertCell(4);
		var aiButt = document.createElement("input");
		aiButt.type = "radio";
		aiButt.name = "player_type_" + num_players;
		aiButt.value = "ai";
		cell4.appendChild(aiButt);
		label = document.createTextNode(" AI");
		cell4.appendChild(label);

		if(num_players > 2) {
			addXs();
		}
	}
});

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

function addXs() {
	$("#player_creation_table tr td:first-child").each(function() {
		$(this).text("✖");
	});
}

function removeXs() {
	$("#player_creation_table tr td:first-child").each(function() {
		$(this).text("");
	});
}

$("#home_quit").bind('click', function() {
	close();
});

$("#home_newgame").bind('click', function() {
	$("#home_options").fadeOut(100);
	$("#game_settings").delay(100).fadeIn(200);
});


$("#play_button").bind('click', function() {
	$("#home_screen").slideUp(500);
});