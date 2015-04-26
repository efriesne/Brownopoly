$("#popup").hide(0);
$("#trade_center").hide(0);


var manageDisabled = false;
var tradeDisabled = false;
var rollDisabled = false;
var mortgages = {};
var houseTransactions = {};

function disableAll() {
	manageDisabled = true;
	tradeDisabled = true;
	rollDisabled = true;
}

function enableAll() {
	manageDisabled = false;
	tradeDisabled = false;
	rollDisabled = false;
}

/* ############################################
###############################################

 #####   ####   #      #
 #	 #  #    #  #      #
 #####  #    #  #      #
 # #    #    #  #      #
 #  #   #    #  #      #
 #   #   ####   #####  #####

###############################################
############################################ */

$("#roll_button").bind('click', function() {
	roll();
});


/* ############################################
###############################################

 #    #    ##    #    #    ##     ####   ######
 ##  ##   #  #   ##   #   #  #   #    #  #
 # ## #  #    #  # #  #  #    #  #       #####
 #    #  ######  #  # #  ######  #  ###  #
 #    #  #    #  #   ##  #    #  #    #  #
 #    #  #    #  #    #  #    #   ####   ######

###############################################
############################################ */

//for testing
/*
$.post("/test", function(responseJSON){
	var responseObject = JSON.parse(responseJSON);
	var board = responseObject.board;
	var players = responseObject.state.players;
	//players is in correct turn order
	createBoard(board);
	setupPlayerPanel(players);
	for (var i = num_players; i < 6; i++) {
		var playerID = "#player_" + i;
		$(playerID).hide(0);
	}
	currPlayer = players[0];

	$("#screen").show(0);
	$("#home_screen").slideUp(500);

});
*/
var manageOn = false;
var buildOn = false;

$("#manage_button_bar").hide(0);

$("#manage_button").on('click', function() {
	if (!manageDisabled) {
		var button = $("#manage_button");
		if (!manageOn) {
			mortgages = {};
			houseTransactions = {};
			loadPlayer(currPlayer);
			manageOn = true;
			button.css("background", "rgba(209, 251, 228, .7)");
			button.css("box-shadow", "0px 0px 7px #D1FBE4");
			$("#manage_button_bar").fadeIn(200);
			//$("#player_tab_panel").hide(0);
			hideOtherTabs(currPlayer.id);
			buildOnSellOff();
		} 
	}
});

$("#manage_save").on('click', function() {
	//need some way to ensure user doesn't click save before they have positive balance (if they are forced to mortgage)
	if(!manageDisabled) {
		var button = $("#manage_button");
		if (manageOn) {
			manageProperties();
			button.css("background", "");
			button.css("box-shadow", "");
			manageOn = false;
			clearValids();
			$(".player_tab").show(0);
			$("#manage_button_bar").fadeOut(100);
		}
	}
});

$("#manage_sell").bind('click', function() {
	if(!manageDisabled) {
		buildOffSellOn();
	}
});

$("#manage_build").bind('click', function() {
	if(!manageDisabled) {
		buildOnSellOff();
	}
});

function findValidsDuringManage() {
	//finds which properties can have houses built on them with the user's hypothetical builds
	var hTransactions = dictToArray(houseTransactions);
	var mTransactions = dictToArray(mortgages);
	var params = {
		builds: buildOn,
		houses: JSON.stringify(hTransactions),
		mortgages: JSON.stringify(mTransactions)
	};
	if (buildOn) {
		validBuilds(params);
	} else {
		validSells(params);
	}
}


function validBuilds(params) {
	var defaultParams = {
		builds: true,
		houses: JSON.stringify([]),
		mortgages: JSON.stringify([])
	};
	params = defaultArg(params, defaultParams);
	console.log(params);
	$.post("/findValids", params, function(responseJSON) {
		var response = JSON.parse(responseJSON);
		var valids = response.valids;
		$('#monopolies_table tr').children('td:empty').each(function () {
		  	var td = $(this);
		  	var propID = td.parent().children("td:nth-child(2)").data().id;
		  	if ($.inArray(propID, valids) >= 0) {
		  		var prev = td.prev();
		  		if (prev.text().trim() == 'H' || td.index() == 2) {
		  			//td.text("+");
			  		td.css('border', '1px dashed #000');
			  		td.data("valid", true);
			  	} else {
			  		td.data("valid", false);
			  	}
		  	} else {
		  		td.data("valid", false);
		  	}  	
		});
		drawValidMortgages(false);
	});
	
}

function validSells(params) {
	var defaultParams = {
		builds: false,
		houses: JSON.stringify([])
	};
	params = defaultArg(params, defaultParams);

	$.post("/findValids", params, function(responseJSON) {
		var response = JSON.parse(responseJSON);
		var valids = response.valids;
		$('#monopolies_table tr').children('td').each(function () {
		  	var td = $(this);
		  	var propID = td.parent().children("td:nth-child(2)").data().id;
		  	if ($.inArray(propID, valids) >= 0) {
		  		var next = td.next();
		  		if (td.text().trim() == "H" && next.text().trim() == "") {
		  			//td.text("+");
			  		td.css('border', '1px dashed #000');
			  		td.data("valid", true);
			  	} else {
			  		td.data("valid", false);
			  	}
		  	} else {
		  		td.data("valid", false);
		  	}  	
		});
		drawValidMortgages(true);
	});
}

function drawValidMortgages(mortgaging) {
	var compareText;
	if (mortgaging) {
		compareText = "";
	} else {
		compareText = "M";
	}
	$('table.player_table tr').children('td:first-child').each(function () {
	  	var td = $(this);
	  	if (td.text().trim() == compareText) {
	  		var canMortgage = true;
		  	td.parent().children('td').each(function() {
		  		//make sure property has no houses
		  		if ($(this).text().trim() == "H") {
		  			canMortgage = false;
		  		}
		  	});
		  	td.data("canMortgage", canMortgage);
		  	if (canMortgage) {
		  		td.css('border', '1px dashed #000');
		  	}
	  	}	  	
	});
}

function clearValids() {
	$('table.player_table tr').children('td').css("border", "");
}

function buildSellHouse(id) {
	var numToAdd;
	if (buildOn) {
		numToAdd = 1;
	} else {
		numToAdd = -1;
	}
	if (houseTransactions[id] != undefined) {
		houseTransactions[id][1] += numToAdd;
	} else {
		houseTransactions[id] = [id, numToAdd];
	}
}

function mortgage(id, mortgaging) {
	if (mortgages[id] != undefined) {
		delete mortgages[id];
	} else {
		mortgages[id] = [id, mortgaging];
	}
}

function manageProperties() {
	var mTransactions = dictToArray(mortgages);
	var hTransactions = dictToArray(houseTransactions);
	var params = {
		mortgages: JSON.stringify(mTransactions),
		houses: JSON.stringify(hTransactions)
	}
	$.post("/mortgage", params, function(responseJSON){
		currPlayer = JSON.parse(responseJSON).player;
		loadPlayer(currPlayer);
	});
}

$("table.player_table").on("click", "td", function() {
	var td = $(this);
	
	if (manageOn) {
		if (buildOn) {
		  	var prev = td.prev();
			if (td.data().canMortgage) {
				td.data("canMortgage", false);
				td.text("");
				td.css("border", "");
				mortgage(td.next().data().id, false);
			} else if (td.data().valid) {
				td.data("valid", false);
			  	td.text("H");
				td.css("border", "");
				var propID = td.parent().children("td:nth-child(2)").data().id;
				buildSellHouse(propID);
			}
			findValidsDuringManage(true);			
		} else {
			if (td.data().canMortgage) {
				td.data("canMortgage", false);
				td.text("M").css("border", "");
				mortgage(td.next().data().id, true);
			} else if (td.data().valid) {
				td.data("valid", false);
				td.text("").css("border", "");
				var propID = td.parent().children("td:nth-child(2)").data().id;
			  	buildSellHouse(propID);				
			}
			findValidsDuringManage(false);
		}
	}
  	
	// know which property it is ---> td.parent().index();
});

function buildOnSellOff() {
	clearValids();
	buildOn = true;
	var params = {
		builds: buildOn,
		houses: JSON.stringify(dictToArray(houseTransactions)),
		mortgages: JSON.stringify(dictToArray(mortgages))
	}
	validBuilds(params);
	var build = $("#manage_build");
	build.css("background", "rgba(209, 251, 228, 1)");
	build.css("box-shadow", "0px 0px 7px #D1FBE4");

	var sell = $("#manage_sell");
	sell.css("background", "");
	sell.css("box-shadow", "");
}

function buildOffSellOn() {
	clearValids();
	buildOn = false;
	var params = {
		builds: buildOn,
		houses: JSON.stringify(dictToArray(houseTransactions)),
		mortgages: JSON.stringify(dictToArray(mortgages))
	}
	validSells(params);
	var sell = $("#manage_sell");
	sell.css("background", "rgba(209, 251, 228, 1)");
	sell.css("box-shadow", "0px 0px 7px #D1FBE4");

	var build = $("#manage_build");
	build.css("background", "");
	build.css("box-shadow", "");
}


function hideOtherTabs(id) {
	$(".player_tab").each(function() {
		if ($(this).data().playerID != id) {
			$(this).hide(0);
		}
	});
}

function defaultArg(arg, def) {
	return typeof arg !== 'undefined' ? arg : def;
}

function dictToArray(dict) {
	//takes in a dictionary and turns it into an array of the dict's values
	var arr = [];
	for (var key in dict) {
		if (dict.hasOwnProperty(key)) {
			arr.push(dict[key]);
		}
	}
	return arr;
}




/* ####################################
#######################################

 #####     ##    #    #   ####   ######
 #    #   #  #   #    #  #       #
 #    #  #    #  #    #   ####   #####
 #####   ######  #    #       #  #
 #       #    #  #    #  #    #  #
 #       #    #   ####    ####   ######

#######################################
#################################### */
var pauseOn = false;


$("#pause_button").bind('click', function() {
	var button = $("#pause_button");
	button.css("background", "rgba(209, 251, 228, .7)");
	button.css("box-shadow", "0px 0px 7px #D1FBE4");
	$("#popup").fadeIn(200);
	$("#screen").css("opacity", ".2");
	pauseOn = true;
	$(".button").css("cursor", "default");
	$(".popup_button").css("cursor", "pointer");
	$("#paused_screen").show(0);
});

$("#popup_exit, #popup_resume").bind('click', function() {
	var button = $("#pause_button");
	$("#popup").fadeOut(200);
	$("#screen").css("opacity", "1");
	button.css("background", "");
	button.css("box-shadow", "");
	pauseOn = false;
	$(".button").css("cursor", "pointer");
	$("#paused_screen").hide(0);
});

$("#popup_quit").bind('click', function() {
	$("#paused_screen").hide(0);
	$("#game_settings").hide(0);
	$("#home_options").show(0);
	$("#home_screen").slideDown(500);
	$("#popup").hide(0);
});

$(document).keyup(function(e) {
    var ESC = 27;
	if (e.keyCode == ESC && pauseOn) {
		var button = $("#pause_button");
		$("#popup").fadeOut(200);
		enableAll();
		$("#screen").css("opacity", "1");
		button.css("background", "");
		button.css("box-shadow", "");
	}
});

/* ####################################
#######################################

####### ######     #    ######  #######
   #    #     #   # #   #     # #
   #    #     #  #   #  #     # #
   #    ######  #     # #     # #####
   #    #   #   ####### #     # #
   #    #    #  #     # #     # #
   #    #     # #     # ######  #######

#######################################
#################################### */

var gameState;
/* Opens the trade center on a click of the trade button */
$("#trade_button").on("click", function(){
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

			addCheckBoxes("trade_recip_body");
			document.getElementById("recipient_wealth_box").max = player.balance; 
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
	addCheckBoxes("trade_init_body");
	document.getElementById("initiator_wealth_box").max = currPlayer.balance; 

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

		addCheckBoxes("trade_recip_body");
		document.getElementById("recipient_wealth_box").max = player.balance; 


	});
});



$("#trade_cancel").on("click", function() {
	var button = $("#trade_button");
	$("#trade_center").fadeOut(200);
	$("#screen").css("opacity", "1");
	button.css("background", "");
	button.css("box-shadow", "");
	pauseOn = false;
	$(".button").css("cursor", "pointer");
	$("#paused_screen").hide(0);
});


function addCheckBoxes(div) {
	var tables = $(document.getElementById(div)).find("table");

	tables.each(function() {
		var rows = this.rows;
		for (var r = 0; r < rows.length; r++) {
			var row = rows[r];
			var cell = row.insertCell(0);
			$(cell).html('<input type="checkbox"' 
							+ 'name="initiator_selections"' 
							+ 'onclick="highlightRow(this);">');
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








