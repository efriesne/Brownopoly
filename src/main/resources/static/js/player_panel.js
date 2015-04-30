function setupPlayerPanel(players) {
	$("#player_tab_panel").html("");

	for (var i = 0; i < players.length; i++) {
		var playerID = players[i].id;
		var tab = document.createElement("div");
		tab.className = "player_tab";
		tab.id = "tab_" + playerID;

		var player_icon = document.getElementById(playerID);
		var url = "url(\"" + $(player_icon).data().imgurlpath + "\")";
		$(tab).css("content", url);

		secondMove = true;
		movePlayer(players[i], players[i].position, 0);
		secondMove = false;

		$(tab).data("color", $(player_icon).data().color);
		$(tab).data("playerID", playerID);

		var tab_panel = document.getElementById("player_tab_panel");
		tab_panel.appendChild(tab);
	}
	currPlayer = players[0];
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

	$("#player_panel_current_name").text(player.name);
	$("#player_wealth").data("cash", player.balance);
	$("#player_wealth").text("Cash: $" + player.balance);

	setUpTable("monopolies_table", player.monopolies, true);
	setUpTable("oProperties", player.properties, false);
	setUpTable("railroads", player.railroads, false);
	setUpTable("utilities", player.utilities, false);
}

function setUpTable(tableID, ownables, isMonopolies) {
	if (isMonopolies) {
		var newList = [];
		for (var i = 0; i < ownables.length; i++) {
			newList = newList.concat(ownables[i].members);
		}
		ownables = newList;
	}
	
	var table = document.getElementById(tableID);
	$(table).html("");
	if (ownables.length == 0) {
		$(table).hide(0);
	} else {
		$(table).show(0);
	}

	for (var i = 0; i < ownables.length; i++) {
		var o = ownables[i];
		var row = table.insertRow(i);
		$(row).data("id", o.id);
		row.className = "infoable";

		if (isMonopolies) {
			//give the row a house cost value
			$(row).data("cost", o.houseCost);
		}

		var cell0 = $(row.insertCell(0));
		if (o.mortgaged) {
			cell0.html("M");
		}

		var cell1 = $(row.insertCell(1));

		if (isMonopolies) {
			cell1.html('<div class="mtable_noOF">' + o.name + '</div>');
			for (var h = 0; h < 5; h++) {
				var cell = $(row.insertCell(2 + h));
				if (h < o.numHouses) {
					cell.html("H");
				}
			}
		} else {
			cell1.html('<div class="ptable_noOF">' + o.name + '</div>');
		}

		if (o.color != undefined) {
			var color = o.color;
			var red = color[0];
			var green = color[1];
			var blue = color[2];
			cell1.children("div").css("border-left", "4px solid rgb(" + red + "," + green + "," + blue + ")");
			cell1.children("div").css("padding-left", "8px");
		}
	}
}

function removeBottomHighlights() {
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

function removePlayer(player) {
	var tab_panel = document.getElementById("player_tab_panel");
	var tabID = "tab_" + player.id;
	var tab = document.getElementById(tabID);
	console.log(tab);
	tab_panel.removeChild(tab);
	var piece = document.getElementById(player.id);
	var board = document.getElementById("board");
	board.removeChild(piece);
}

