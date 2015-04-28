var gameState;
function setUpTrade(trade) {
/* Opens the trade center on a click of the trade button */
	/* visually indicate that the trade button has been clicked */
	var button = $("#trade_button");
	button.css("background", "rgba(209, 251, 228, .7)");
	button.css("box-shadow", "0px 0px 7px #D1FBE4");

	$.post("/tradeSetUp", function(responseJSON) {
		var responseObject = JSON.parse(responseJSON);
		gameState = responseObject.state;

		var select = document.getElementById("select_recipient");
		$(select).html("");
		for (var i = 0; i < gameState.players.length; i++) {
			var player = gameState.players[i];
			if (player.id != currPlayer.id) {
				var option = document.createElement("option");
				option.value = player.id;
				$(option).html(player.name);
				select.appendChild(option);
			}
		}

        var p_ID;
        if (trade != null) {
            p_ID = trade.recipient.id;
        } else {
		    p_ID = $("#select_recipient option:selected").val();
		}

		var postParameters = {playerID: JSON.stringify(p_ID)};
		$.post("/loadPlayer", postParameters, function(responseJSON){
			var responseObject = JSON.parse(responseJSON);
			var player = responseObject.player;
			$("#trader_panel_current_recipient").text(player.name);
			$("#recip_player_wealth").text("Cash: $" + player.balance);

			setUpTable("trade_recip_monopolies", player.monopolies, true);
			setUpTable("trade_recip_oProperties", player.properties, false);
			setUpTable("trade_recip_railroads", player.railroads, false);
			setUpTable("trade_recip_utilities", player.utilities, false);

			addCheckBoxes("trade_recip_body", trade.recipProps);

			document.getElementById("recipient_wealth_box").max = player.balance;
			if (trade != null) {
                document.getElementById("recipient_wealth_box").value = trade.recipMoney;
            }
		});
	});


	/* set up the iniator tab */
	$("#trade_init_header").html('Trade initiated by: ' + currPlayer.name);

	$("#trader_panel_initiator").text(currPlayer.name);
	$("#initiator_wealth").text("Cash: $" + currPlayer.balance);

	/* tables */
	setUpTable("trade_init_monopolies", currPlayer.monopolies, true);
	setUpTable("trade_init_oProperties", currPlayer.properties, false);
	setUpTable("trade_init_railroads", currPlayer.railroads, false);
	setUpTable("trade_init_utilities", currPlayer.utilities, false);

	/* check boxes */
	addCheckBoxes("trade_init_body", trade.initProps);
	document.getElementById("initiator_wealth_box").max = currPlayer.balance;
    if (trade != null) {
    	document.getElementById("initiator_wealth_box").value = trade.initMoney;
    }
	//$("#initiator_wealth_box").numeric();

	$("#trade_accept").hide(0);
	$("#trade_counter").hide(0);
	$("#trade_decline").hide(0);

	$("#trade_center").fadeIn(200);

	$("#screen").css("opacity", ".2");
	$(".button").css("cursor", "default");
	$(".trade_button").css("cursor", "pointer");
	$("#paused_screen").show(0);
});

$("#select_recipient").on("change", function() {
	// console.log(this.text());
	var p_ID = $("#select_recipient option:selected").val();
	var postParameters = {playerID: JSON.stringify(p_ID)};
	$.post("/loadPlayer", postParameters, function(responseJSON){
		var responseObject = JSON.parse(responseJSON);
		var player = responseObject.player;

		$("#trader_panel_current_recipient").text(player.name);
		$("#recip_player_wealth").text("Cash: $" + player.balance);

		setUpTable("trade_recip_monopolies", player.monopolies, true);
		setUpTable("trade_recip_oProperties", player.properties, false);
		setUpTable("trade_recip_railroads", player.railroads, false);
		setUpTable("trade_recip_utilities", player.utilities, false);

		addCheckBoxes("trade_recip_body", null);
		document.getElementById("recipient_wealth_box").max = player.balance;


	});
});

function endTrade() {
	var button = $("#trade_button");
	$("#trade_center").fadeOut(200);
	$("#screen").css("opacity", "1");
	button.css("background", "");
	button.css("box-shadow", "");
	pauseOn = false;
	$(".button").css("cursor", "pointer");
	$("#paused_screen").hide(0);
	loadPlayer(currPlayer);
}

$("#trade_cancel").on("click", function() {
	endTrade();
});

$("#trade_propose").on("click", function() {
	var initProps = getCheckedProperties("trade_init_body");
	var recipProps = getCheckedProperties("trade_recip_body");
	var initMoney = 0;
	if (document.getElementById("initiator_wealth_checkbox").checked) {
		initMoney = document.getElementById("initiator_wealth_box").value;
	}
	var recipMoney = 0;
	if (document.getElementById("recipient_wealth_checkbox").checked) {
		recipMoney = document.getElementById("recipient_wealth_box").value;
	}

	var recipientID = $("#select_recipient option:selected").val();
	var recipientName = $("#select_recipient option:selected").text();
	var trade = true;
	if (!currPlayer.isAI) {
		trade = confirm(recipientName + ", Do you accept this trade?");
	}
	console.log(trade);
	if (trade) {
		var postParameters = {recipient: recipientID, initProps: JSON.stringify(initProps), initMoney: initMoney, recipProps: JSON.stringify(recipProps), recipMoney: recipMoney};
		$.post("/trade", postParameters, function(responseJSON){
			var responseObject = JSON.parse(responseJSON);
			currPlayer = responseObject.initiator;
			if (responseObject.accepted) {
				alert(recipientName + " accepted the trade!");
				endTrade();
			} else {
				alert(recipientName + " rejected the trade.");
			}
		});
	} else {
		alert(recipientName + " rejected the trade.");
	}
});

function getCheckedProperties(div) {
	var tables = $(document.getElementById(div)).find("table");
	var properties = new Array();
	tables.each(function() {
		var rows = this.rows;
		var arr = new Array();
		$(rows).children('td:first-child').each(function() {
			var td = $(this);
			var checked = td.find("input").is(":checked");
			var id = td.parent().data().id;
			if (checked) {
				arr.push(id);
			}
		});
		properties.push(arr);
    });
	return properties;
}

function tradeContainsProp(properties, prop) {
    for (var i = 0; i < properties.length; i++) {
        var list = properties[i];
        for (var j = 0; j < list.length; j++) {
            if (list[j] == prop) {
                return true;
            }
        }
    }
    return false;
}

function addCheckBoxes(div, properties) {
	var tables = $(document.getElementById(div)).find("table");

	tables.each(function() {
		var rows = this.rows;
		for (var r = 0; r < rows.length; r++) {
			var row = rows[r];
			var cell = row.insertCell(0);
			$(cell).html('<input type="checkbox"'
							+ 'name="initiator_selections"'
							+ 'onclick="highlightRow(this);">');
			if ((properties != null) && tradeContainsProp(properties, row.data().id)) {
                $(cell).prop('checked', true);
            }
		}
	});
}

function highlightRow(checkbox) {
	var row = $(checkbox).closest("tr");
	if ($(checkbox).is(":checked")) {
		row.css("background", "rgba(255, 255, 255, 1)");
	} else {
		row.css("background", "rgba(255, 255, 255, 0)");
	}
}