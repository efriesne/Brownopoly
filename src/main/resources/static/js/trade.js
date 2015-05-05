var gameState;

function getPlayerFromId(id) {
	for (var i = 0; i < gameState.players.length; i++) {
    	var player = gameState.players[i];
    	if (player.id == id) {
    		return player;
    	}
    }
    return null;
}

var aiTrade = false;
var recipientID;
function setUpTrade(trade) {
	aiTrade = false;
	/* Opens the trade center on a click of the trade button */
	/* visually indicate that the trade button has been clicked */
	$(".button_bar_button").removeClass("selected_button");
	$("#trade_button").addClass("selected_button");
	$("#recipient_wealth_box").prop("readonly", false);
    $("#initiator_wealth_box").prop("readonly", false);

	var initProps = null;
	var recipProps = null
	var initMoney = null;
	var recipMoney = null;
	if (trade != null) {
		initProps = trade.initProps;
		recipProps = trade.recipProps;
		initMoney = trade.initMoney;
		recipMoney = trade.recipMoney;
		aiTrade = true;
    }

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
				$(option).text(player.name);
				select.appendChild(option);
			}
		}

        if (trade != null) {
        	recipientID = trade.recipient.id;
        } else {
	   	 	recipientID = $("#select_recipient option:selected").val();
	   	}
		var postParameters = {playerID: JSON.stringify(recipientID)};
		$.post("/loadPlayer", postParameters, function(responseJSON){
			var responseObject = JSON.parse(responseJSON);
			var player = responseObject.player;
			$("#trader_panel_current_recipient").text(player.name);
			$("#recip_player_wealth").text("Cash: $" + player.balance);

			setUpTable("trade_recip_monopolies", player.monopolies, true);
			setUpTable("trade_recip_oProperties", player.properties, false);
			setUpTable("trade_recip_railroads", player.railroads, false);
			setUpTable("trade_recip_utilities", player.utilities, false);

			addCheckBoxes("trade_recip_body", recipProps);

			if (trade != null) {
                document.getElementById("recipient_wealth_box").value = recipMoney;
            } else {
            	document.getElementById("recipient_wealth_box").value = "";
            }
		});
	});


	/* set up the iniator tab */
	$("#trade_init_header").html('<b>Trade initiated by: ');
	$(document.createTextNode(currPlayer.name)).appendTo($("#trade_init_header"));
	$("#trade_init_header").append('</b>');

	$("#trader_panel_initiator").text(currPlayer.name);
	$("#initiator_wealth").text("Cash: $" + currPlayer.balance);

	/* tables */
	setUpTable("trade_init_monopolies", currPlayer.monopolies, true);
	setUpTable("trade_init_oProperties", currPlayer.properties, false);
	setUpTable("trade_init_railroads", currPlayer.railroads, false);
	setUpTable("trade_init_utilities", currPlayer.utilities, false);

	/* check boxes */
	addCheckBoxes("trade_init_body", initProps);
	document.getElementById("initiator_wealth_box").max = currPlayer.balance;
    if (trade != null) {
    	document.getElementById("initiator_wealth_box").value = initMoney;
    } else {
    	document.getElementById("initiator_wealth_box").value = "";
    }

	$("#trade_accept").hide(0);
	$("#trade_counter").hide(0);
	$("#trade_decline").hide(0);
	$("#trade_propose").show(0);
    $("#trade_cancel").show(0);

	$("#trade_center").fadeIn(200);

	$("#screen").css("opacity", ".2");
	$(".button").css("cursor", "default");
	$(".trade_button").css("cursor", "pointer");
	$("#paused_screen").show(0);

    $("#trade_guideline").text("Step 1: Select a trade recipient and propose a trade.");

    if (aiTrade) {
       setTimeout(function() {$("#trade_propose").trigger('click');}, 100);
    }

}

$("#select_recipient").on("change", function() {
	recipientID = $("#select_recipient option:selected").val();
	var recipient = getPlayerFromId(recipientID);
	var postParameters = {playerID: JSON.stringify(recipientID)};
	$("#trader_panel_current_recipient").text(recipient.name);
	$("#recip_player_wealth").text("Cash: $" + recipient.balance);

	setUpTable("trade_recip_monopolies", recipient.monopolies, true);
	setUpTable("trade_recip_oProperties", recipient.properties, false);
	setUpTable("trade_recip_railroads", recipient.railroads, false);
	setUpTable("trade_recip_utilities", recipient.utilities, false);

	addCheckBoxes("trade_recip_body", null);

	document.getElementById("recipient_wealth_box").max = recipient.balance;
	$("#recipient_wealth_box").val("");
});

function endTrade() {
	$("#trade_center").fadeOut(200);
	$("#screen").css("opacity", "1");
	$("#trade_button").removeClass("selected_button");
	pauseOn = false;
	$(".button").css("cursor", "pointer");
	loadPlayer(currPlayer);
	$("#paused_screen").hide(0);
	if (aiTrade) {
		roll();
	}
}

$("#trade_cancel").on("click", function() {
	endTrade();
});

$("#trade_decline").on("click", function() {
    $('#trade_initiator').animate({ left: '-1px'},  "slow");
    $('#trade_recipient').animate({ right: '-1px'},  "slow");
    $("#select_recipient").show(0);

    $("#trade_init_header").html('<b>Trade initiated by: ');
	$(document.createTextNode(currPlayer.name)).appendTo($("#trade_init_header"));
	$("#trade_init_header").append('</b>');
	
	$("#trade_recip_header").text("Trade with:");

	if (aiTrade) {
		endTrade();
	} else {
		$("#trade_accept").hide(0);
		$("#trade_decline").hide(0);
		$("#trade_propose").show(0);
		$("#trade_cancel").show(0);
		$("#trade_guideline").text("Step 3: Initiator can make another proposal.");
		$("#recipient_wealth_box").prop("readonly", false);
        $("#initiator_wealth_box").prop("readonly", false);
		$("input:checkbox").show(0);
	}

});

var initProps;
var initMoney;
var recipProps;
var recipMoney;
var recipient;


$("#trade_propose").on("click", function() {
	initProps = getCheckedProperties("trade_init_body");
	recipProps = getCheckedProperties("trade_recip_body");

	initMoney = document.getElementById("initiator_wealth_box").value;
	recipMoney = document.getElementById("recipient_wealth_box").value;

	if (initMoney == "") {
		initMoney = 0;
	}
	if (recipMoney == "") {
		recipMoney = 0;
	}

	recipient = getPlayerFromId(recipientID);

	postParameters = {playerID: currPlayer.id, money: initMoney};
	$.post("/checkTradeMoney", postParameters, function(responseJSON){
		var responseObject = JSON.parse(responseJSON);
		if (responseObject.invalidMoney != "") {
        	customizeAndShowPopup({
				showNoButton: false,
				message: "Problem with " + currPlayer.name + "'s money box: " + responseObject.invalidMoney
			});
        } else {
        	postParameters = {playerID: recipientID, money: recipMoney};
			$.post("/checkTradeMoney", postParameters, function(responseJSON){
				var responseObject = JSON.parse(responseJSON);
				if (responseObject.invalidMoney != "") {
					customizeAndShowPopup({
						showNoButton: false,
						message: "Problem with " + recipient.name + "'s money box: " + responseObject.invalidMoney
					});
				} else {
					proposeTrade(recipient);
				}
			});
        }
	});
});

function proposeTrade(recipient) {
	var accept = true;
	if (!recipient.isAI) {
		$("#trade_accept").show(0);
        $("#trade_decline").show(0);
        $("#trade_propose").hide(0);
        $("#trade_cancel").hide(0);
        $("#recipient_wealth_box").prop("readonly", true);
        $("#initiator_wealth_box").prop("readonly", true);

        $("input:checkbox").hide(0);
		$("#trade_guideline").text("Step 2: Chosen recipient can review trade and click Accept or Decline.");
		$('#trade_initiator').animate({ left: '300px'},  "slow");
	    $('#trade_recipient').animate({ right: '300px'},  "slow");

		$("#select_recipient").hide(0);

		$("#trade_recip_header").html("<b> Trade recipient: ");
		$(document.createTextNode(recipient.name)).appendTo($("#trade_recip_header"));
		$("#trade_recip_header").append("</b>");

		$("#trade_init_header").html('Trade initiated by: ');
		$(document.createTextNode(currPlayer.name)).appendTo($("#trade_init_header"));
	} else {
		var postParameters = {recipient: recipient.id, initProps: JSON.stringify(initProps), initMoney: initMoney,
		recipProps: JSON.stringify(recipProps), recipMoney: recipMoney};
		$.post("/trade", postParameters, function(responseJSON){
			var responseObject = JSON.parse(responseJSON);
			currPlayer = responseObject.initiator;
			if (responseObject.accepted) {
				customizeAndShowPopup({
					titleText: "TRADE",
					showNoButton: false,
					message: recipient.name + " accepted the trade!"
				}, {
					okHandler: function() {
						endTrade();
					}
				});
				scrollNewsfeed("-> " + currPlayer.name + " traded " + responseObject.msg);
			} else {
				customizeAndShowPopup({
					titleText: "TRADE",
					showNoButton: false,
					message: recipient.name + " rejected the trade."
				});
			}
		});
	}


}

$("#trade_accept").on("click", function() {

    $("#trade_recip_header").text("Trade with:");
    $("#select_recipient").show(0);
	var postParameters = {recipient: recipient.id, initProps: JSON.stringify(initProps), initMoney: initMoney,
	recipProps: JSON.stringify(recipProps), recipMoney: recipMoney};
	$.post("/trade", postParameters, function(responseJSON){
		var responseObject = JSON.parse(responseJSON);
		currPlayer = responseObject.initiator;
		if (responseObject.accepted) {
			scrollNewsfeed("-> " + currPlayer.name + " traded " + responseObject.msg);
			endTrade();
		}
	});
	$('#trade_initiator').animate({ left: '-1px'},  "fast");
    $('#trade_recipient').animate({ right: '-1px'},  "fast");

});

function getCheckedProperties(div) {
	var tables = $(document.getElementById(div)).find("table");
	var properties = new Array();
	tables.each(function() {
		var rows = this.rows;
		$(rows).children('td:first-child').each(function() {
			var td = $(this);
			var checked = td.find("input").is(":checked");
			var id = td.parent().data().id;
			if (checked) {
				properties.push(id);
			}
		});
    });
	return properties;
}

function tradeContainsProp(properties, propID) {
    for (var i = 0; i < properties.length; i++) {
		if (properties[i] == propID) {
			return true;
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
			if ((properties != null) && tradeContainsProp(properties, $(row).data().id)) {
				var checked = $(cell).find("input");
                checked.trigger("click");
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



