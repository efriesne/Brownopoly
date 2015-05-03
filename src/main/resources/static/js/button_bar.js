$(".popup").hide(0);
$("#trade_center").hide(0);


// var manageDisabled = false;
// var tradeDisabled = false;
// var rollDisabled = false;
// var mortgages = {};
// var houseTransactions = {};

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

//see roll.js

$("#roll_button").bind('click', function() {
	if (!rollDisabled) {
		rollDisabled = true;
		roll();
	}
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

// $.post("/test", function(responseJSON){
// 	var responseObject = JSON.parse(responseJSON);
// 	var board = responseObject.board;
// 	var players = responseObject.state.players;
// 	//players is in correct turn order
// 	resetVariables();
// 	createBoard(board);
// 	setupPlayerPanel(players);
// 	for (var i = num_players; i < 6; i++) {
// 		var playerID = "#player_" + i;
// 		$(playerID).hide(0);
// 	}
// 	$("#screen").show(0);
// 	$("#home_screen").slideUp(500, startTurn());
// });

$("#manage_button_bar").hide(0);

$("#manage_button").on('click', function(event, mortgage) {
	if (!manageDisabled) {
		var button = $("#manage_button");
		if (!manageOn) {
			mortgages = {};
			houseTransactions = {};
			loadPlayer(currPlayer);
			manageOn = true;
			rollDisabled = true;
			tradeDisabled = true;
			button.css("background", SELECTED);
			button.css("box-shadow", BUTTON_SHADOW);
			$("#manage_button_bar").fadeIn(200);
			hideOtherTabs(currPlayer.id);
			if (mortgage) {
				buildOffSellOn();
			} else {
				buildOnSellOff();
			}
		} 
	}
});

$("#manage_sell").on('click', function() {
	if(!manageDisabled) {
		buildOffSellOn();
	}
});

$("#manage_build").on('click', function() {
	if(!manageDisabled) {
		buildOnSellOff();
	}
});

$("#manage_save").on('click', function() {

	if(!manageDisabled) {
		var button = $("#manage_button");
		if (manageOn) {
			manageProperties();
			button.css("background", "");
			button.css("box-shadow", "");
			clearValids();
			$(".player_tab").show(0);
			setTimeout(function() {
				if (currPlayer.isBroke) {
					customizeAndShowPopup({
						titleText: "BANKRUPTCY",
						showNoButton: false,
						message: currPlayer.name + " is Bankrupt! Mortgage property and/or Sell houses/hotels to pay off debt!"
					}, {
						okHandler: function() {
							buildOffSellOn();
						}
					});
				} else {
					$("#manage_button_bar").fadeOut(100);
					manageOn = false;
					rollDisabled = false;
                    tradeDisabled = false;
					if (bankruptcyOn) {
						checkBankruptcy();
					}

				}
			}, 100);
		}
	}
});

function manageProperties() {
	var mTransactions = dictToArray(mortgages);
	var hTransactions = dictToArray(houseTransactions);
	var params = {
		mortgages: JSON.stringify(mTransactions),
		houses: JSON.stringify(hTransactions),
		playerID: currPlayer.id
	};
	$.post("/manage", params, function(responseJSON){
		currPlayer = JSON.parse(responseJSON).player;
		loadPlayer(currPlayer);
		mortgages = {};
        houseTransactions = {};
	});
}

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

	params = {
		builds: buildOn,
		houses: JSON.stringify(dictToArray(houseTransactions)),
		mortgages: JSON.stringify(dictToArray(mortgages)),
		playerID: currPlayer.id
	};

	$.post("/findValids", params, function(responseJSON) {
		var response = JSON.parse(responseJSON);
		var validHouses = response.validHouses;
		$('#monopolies_table tr').children('td:empty').each(function () {
		  	var td = $(this);
		  	var propID = td.parent().data().id;
		  	if ($.inArray(propID, validHouses) >= 0) {
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
		drawValidMortgages(response.validMortgages, false);
	});
	
}

function validSells(params) {
	var defaultParams = {
		builds: false,
		houses: JSON.stringify([]),
		mortgages: JSON.stringify([])
	};
	params = defaultArg(params, defaultParams);

	params = {
		builds: buildOn,
		houses: JSON.stringify(dictToArray(houseTransactions)),
		mortgages: JSON.stringify(dictToArray(mortgages)),
		playerID: currPlayer.id
	};

	$.post("/findValids", params, function(responseJSON) {
		var response = JSON.parse(responseJSON);
		var validHouses = response.validHouses;
		$('#monopolies_table tr').children('td').each(function () {
		  	var td = $(this);
		  	var propID = td.parent().data().id;
		  	if ($.inArray(propID, validHouses) >= 0) {
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
		drawValidMortgages(response.validMortgages, true);
	});
}

function drawValidMortgages(valids, mortgaging) {
	var compareText;
	if (mortgaging) {
		compareText = "";
	} else {
		compareText = "M";
	}
	$('table.player_table tr').children('td:first-child').each(function () {
	  	var td = $(this);
	  	var id = td.parent().data().id;
	  	if ($.inArray(id, valids) >= 0) {
	  		var canMortgage = true;
	  		/*
		  	td.parent().children('td').each(function() {
		  		//make sure property has no houses
		  		if ($(this).text().trim() == "H") {
		  			canMortgage = false;
		  		}
		  	});
*/
		  	td.data("canMortgage", canMortgage);
		  	if (canMortgage) {
		  		td.css('border', '1px dashed #000');
		  	}
	  	} else {
	  		td.data("canMortgage", false);
	  	} 	
	});
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

$("table.player_table").on("click", "td", function() {
	var td = $(this);
	if (manageOn) {
		if (buildOn) {
		  	var prev = td.prev();
			if (td.data().canMortgage) {
				td.data("canMortgage", false);
				td.text("");
				td.css("border", "");
				//update cash label
				var propID = td.parent().data().id;
				var cost = td.parent().data().mortgageMoney;
				//check if user is actually demortgaging or just negating an unsubmitted mortgage
				if (mortgages[propID] == undefined) {
					console.log("bef: " + cost);
					cost *= (11.0/10.0);
					cost = Math.floor(cost);
					console.log("aft: " + cost);
				}
				var updatedCash = $("#player_wealth").data().cash - cost;
				$("#player_wealth").data("cash", updatedCash);
				$("#player_wealth").text("Cash: $" + updatedCash);
				//demortgage the property
				mortgage(propID, !buildOn);
				findValidsDuringManage(buildOn);
			} else if (td.data().valid) {
				td.data("valid", false);
				//add a house
			  	td.text("H");
				td.css("border", "");
				//update the player's cash label
				var cost = td.parent().data().cost;
				var propID = td.parent().data().id;
				//figure out if the player is actually building or just negating a previous sell that was made but not yet submitted
				var houses = houseTransactions[propID];
				if (houses != undefined && houses[1] < 0) {
					cost /= 2;
				}
				var updatedCash = $("#player_wealth").data().cash - cost;
				$("#player_wealth").data("cash", updatedCash);
				$("#player_wealth").text("Cash: $" + updatedCash);
				//add the house to houseTransactions, find the valids with this change
				buildSellHouse(propID);
				findValidsDuringManage(buildOn);
			}
						
		} else {
			if (td.data().canMortgage) {
				td.data("canMortgage", false);
				td.text("M").css("border", "");
				//update cash label
				var propID = td.parent().data().id;
				var gains = td.parent().data().mortgageMoney;
				var updatedCash = $("#player_wealth").data().cash + gains;
				$("#player_wealth").data("cash", updatedCash);
				$("#player_wealth").text("Cash: $" + updatedCash);
				//mortgage the property
				mortgage(propID, !buildOn);
				findValidsDuringManage(buildOn);
			} else if (td.data().valid) {
				td.data("valid", false);
				td.text("").css("border", "");
				//update the player's cash label
				var cost = td.parent().data().cost / 2;
				var propID = td.parent().data().id;
				//figure out if the player is actually selling or just negating a previous build that was made but not yet submitted
				var houses = houseTransactions[propID];
				if (houses != undefined && houses[1] > 0) {
					cost *= 2;
				}
				var updatedCash = $("#player_wealth").data().cash + cost;
				$("#player_wealth").data("cash", updatedCash);
				$("#player_wealth").text("Cash: $" + updatedCash);
				//remove house from houseTransactions, find valids with this change
			  	buildSellHouse(propID);
			  	findValidsDuringManage(buildOn);				
			}
		}
	}  	
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
	build.css("box-shadow", BUTTON_SHADOW);

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
	sell.css("box-shadow", BUTTON_SHADOW);

	var build = $("#manage_build");
	build.css("background", "");
	build.css("box-shadow", "");
}

function clearValids() {
	$('table.player_table tr').children('td').css("border", "");
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

$("#pause_button").on('click', function() {
	var button = $("#pause_button");
	button.css("background", SELECTED);
	button.css("box-shadow", BUTTON_SHADOW);
	$("#popup_pause").fadeIn(200);
	$("#screen").css("opacity", ".2");
	pauseOn = true;
	$(".button").css("cursor", "default");
	$(".popup_button").css("cursor", "pointer");
	$("#paused_screen").show(0);

});

$("#popup_resume").on('click', function() {
	resumeRestore();
});

$("#popup_quit").on('click', function() {
	resumeRestore();

	clearGameSettings();

	$("#game_settings").hide(0);
	$("#load_screen").hide(0);
	$("#home_options").show(0);
	$("#home_screen").slideDown(500);
});

function resumeRestore() {
	var button = $("#pause_button");
	$("#popup_pause").fadeOut(200);
	$("#screen").css("opacity", "1");
	button.css("background", "");
	button.css("box-shadow", "");
	pauseOn = false;
	$(".button").css("cursor", "pointer");
	$("#paused_screen").hide(0);
}

$(document).keyup(function(e) {
	if (e.keyCode == ESC && pauseOn) {
		// var button = $("#pause_button");
		// $("#popup_pause").fadeOut(200);
		// enableAll();
		// $("#screen").css("opacity", "1");
		// button.css("background", "");
		// button.css("box-shadow", "");

		resumeRestore();

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

$("#trade_button").on("click", function(){
	if (!tradeDisabled) {
		setUpTrade();
	}
});

//see trade.js
