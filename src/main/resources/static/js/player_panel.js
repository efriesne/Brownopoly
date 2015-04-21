function setupPlayerPanel(players) {
	for (var i = 0; i < players.length; i++) {
		var playerID = players[i].id;
		var tab = document.createElement("div");
		tab.className = "player_tab";
		tab.id = "tab_" + playerID;

		var player_icon = document.getElementById(playerID);
		var url = "url(\"" + $(player_icon).data().imgurlpath + "\")";
		$(tab).css("content", url);

		$(tab).data("color", $(player_icon).data().color);
		$(tab).data("playerID", playerID);

		var tab_panel = document.getElementById("player_tab_panel");
		tab_panel.appendChild(tab);
	}
	loadPlayer(players[0]);
}


$("#player_tab_panel").on("click", "div.player_tab", function() {
	var tab = $(this);

	var playerID = tab.data().playerID;

	var postParameters = {playerID: JSON.stringify(playerID)};  
	$.post("/loadPlayer", postParameters, function(responseJSON){
		var responseObject = JSON.parse(responseJSON);
		var player = responseObject.player;

		loadPlayer(player);
	});
});

function loadPlayer(player) {

	var tab = findTabByPlayerID(player.id);
	removeBottomHighlights();
	tab.css("border-bottom", "4px solid " + tab.data().color);
	tab.css("padding-bottom", "-1px");

	$(player_panel_current_name).text(player.name);
	$(player_wealth).text("Cash: $" + player.balance);

	/* #### SET UP THE MONOPOLIES #### */
	var monopoly_table = document.getElementById("monopolies_table");
	monopoly_table.innerHTML = "";
	var monopolies = player.monopolies;

	if (monopolies.length == 0) {
		$(monopoly_table).hide(0);
	} else {
		$(monopoly_table).show(0);
	}

	var propsAdded = 0;
	for (var i = 0; i < monopolies.length; i++) {
		for (var j = 0; j < monopolies[i].members.length; j++) {
			var property = monopolies[i].members[j];
			var row = monopoly_table.insertRow(propsAdded);
			var cell0 = row.insertCell(0);
			if (property.mortgaged) {
				cell0.innerHTML = "M";
			}

			var cell1 = row.insertCell(1);
			cell1.innerHTML = property.name;

			for (var h = 0; h < 5; h++) {
				var cell = row.insertCell(2 + h);
				if (h < property.numHouses) {
					cell.innerHTML = "H";
				}
			}
		}
	}

	/* #### SET UP THE OTHER PROPERTIES #### */

	var properties_table = document.getElementById("oProperties");
	properties_table.innerHTML = "";
	var properties = player.properties;
	if (properties.length == 0) {
		$(properties_table).hide(0);
	} else {
		$(properties_table).show(0);
	}

	for (var i = 0; i < properties.length; i++) {
		var property = properties[i];
		var row = properties_table.insertRow(i);
		var cell0 = row.insertCell(0);
		if (property.mortgaged) {
			cell0.innerHTML = "M";
		}

		var cell1 = row.insertCell(1);
		cell1.innerHTML = property.name;
		console.log(cell1);
		if (property.color != null) {
			var color = property.color;
			var red = color[0];
			var green = color[1];
			var blue = color[2];
			$(cell1).css("border-left", "4px solid rgb(" + red + "," + green + "," + blue + ")");
			$(cell1).css("padding-left", "8px");
		}
	}

	/* #### SET UP THE RAILROADS #### */

	var railroads_table = document.getElementById("railroads");
	railroads_table.innerHTML = "";
	var railroads = player.railroads;
	if (railroads.length == 0) {
		$(railroads_table).hide(0);
	} else {
		$(railroads_table).show(0);
	}

	for (var i = 0; i < railroads.length; i++) {
		var property = railroads[i];
		var row = railroads_table.insertRow(i);
		var cell0 = row.insertCell(0);
		if (property.mortgaged) {
			cell0.innerHTML = "M";
		}

		var cell1 = row.insertCell(1);
		cell1.innerHTML = property.name;
	}

	/* #### SET UP THE UTILITIES #### */

	var utilities_table = document.getElementById("utilities");
	utilities_table.innerHTML = "";
	var utilities = player.utilities;
	if (utilities.length == 0) {
		$(utilities_table).hide(0);
	} else {
		$(utilities_table).show(0);
	}

	for (var i = 0; i < utilities.length; i++) {
		var property = utilities[i];
		var row = utilities_table.insertRow(i);
		var cell0 = row.insertCell(0);
		if (property.mortgaged) {
			cell0.innerHTML = "M";
		}

		var cell1 = row.insertCell(1);
		cell1.innerHTML = property.name;
	}
}

function removeBottomHighlights() {
	/*
	$("#player_tab_panel div.player_tab").each(function() {
		tab = $(this);
		tab.css("border-bottom", "1px solid black");
		tab.css("padding-bottom", "3px");
	});
	*/
	$("#player_tab_panel div.player_tab").css("border-bottom", "1px solid black").css("padding-bottom", "3px");
	
}

function findTabByPlayerID(playerID) {
	var all_tabs = $(".player_tab");
	for (var i = 0; i < all_tabs.length; i++) {
		var tab = all_tabs[i];
		if ($(tab).data("playerID") == playerID) {
			return $(tab);
		}
	}
	return undefined;
}

