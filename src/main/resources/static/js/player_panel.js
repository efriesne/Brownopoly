function setupPlayerPanel(numPlayers) {
	for (var i = 0; i < numPlayers; i++) {
		var playerTag = "player_" + i;
		var tab = document.createElement("div");
		tab.className = "player_tab";

		var player = document.getElementById(playerTag);
		var url = "url(\"" + $(player).data().imgurlpath + "\")";
		$(tab).css("content", url);

		var tab_panel = document.getElementById("player_tab_panel");
		tab_panel.appendChild(tab);
	}
}



$(".player_tab").bind('click', function() {
	alert("CLICKED");
	var tab = $(this);
	var key = tab.text().trim();
	// get the name
	var player_name = "Marley";

	var postParameters = {line: JSON.stringify(player_name)};  
	$.post("/loadPlayer", postParameters, function(responseJSON){
		var responseObject = JSON.parse(responseJSON);
		var player = responseObject.player;

		var player_properties = player.properties;
		var prop_table = document.getElementById("properties_table");
		prop_table.innerHTML = "";
		for (var i = 0; i < player_properties.length; i++) {
			var property = player_properties[i];
			var row = prop_table.insertRow(i);
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
	});
});


// $("#player_tab_panel").bind("click", function() { alert("CLICKED"); });

