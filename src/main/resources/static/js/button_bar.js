$(".popup").hide(0);
$("#trade_center").hide(0);

function disableAll() {
	tradeDisabled = true;
	rollDisabled = true;
	manageDisabled = true;
}

function m_disableOthers() {
	tradeDisabled = true;
	rollDisabled = true;
	$("#roll_button").css("opacity", .2);
	$("#trade_button").css("opacity", .2);
}

function enableAll() {
	tradeDisabled = false;
	rollDisabled = false;
	manageDisabled = false;
}

function m_enableOthers() {
	tradeDisabled = false;
	rollDisabled = false;
	$("#roll_button").css("opacity", 1);
	$("#trade_button").css("opacity", 1);
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

$.post("/test", function(responseJSON){
	var responseObject = JSON.parse(responseJSON);
	var board = responseObject.board;
	var players = responseObject.state.players;
	//players is in correct turn order
	resetVariables();
	housesForHotel = responseObject.state.numHousesForHotel;
	fastPlay = responseObject.state.fastPlay;
	createBoard(board);
	setupPlayerPanel(players);
	loadDeeds();
	for (var i = num_players; i < 6; i++) {
		var playerID = "#player_" + i;
		$(playerID).hide(0);
	}
	$("#screen").show(0);
	$("#home_screen").slideUp(500, startTurn());
});

$("#manage_button_bar").hide(0);

$("#manage_button").on('click', function(event, mortgage) {
	if (!manageDisabled) {
		var button = $("#manage_button");
		if (!manageOn) {
			mortgages = {};
			houseTransactions = {};
			loadPlayer(currPlayer);
			manageOn = true;
			m_disableOthers();
			$(".button").css("cursor", "default");
			$(".manage_button").css("cursor", "pointer");
			$("#pause_button").css("cursor", "pointer");
			button.css("background", SELECTED);
			button.css("box-shadow", BUTTON_SHADOW);
			$("#manage_button_bar").fadeIn(200);
			hideOtherTabs(currPlayer.id);
			//if mortgage is true, click event was triggered because player went under
			//force player to mortgage
			if (mortgage) {
				turnMortgageOn();
			} else {
				turnBuildOn();
			}
		} 
	}
});

$("#manage_build").on('click', function() {
	if(!manageDisabled) {
		turnBuildOn();
	}
});

$("#manage_sell").on('click', function() {
	if(!manageDisabled) {
		turnSellOn();
	}
});

$("#manage_mortgage").on('click', function() {
	if(!manageDisabled) {
		turnMortgageOn();
	}
});

$("#manage_demortgage").on('click', function() {
	if(!manageDisabled) {
		turnDemortgageOn();
	}
});

$("#manage_save").on('click', function() {
	if(!manageDisabled) {
		if (manageOn) {
			manageProperties();
			clearValids();
			$(".player_tab").show(0);
		}
	}
});

$("#manage_cancel").on('click', function() {
	if(!manageDisabled) {
		if (manageOn) {
			drawBoardHouses();
			mortgages = {};
			houseTransactions = {};
			//still need to call manageProperties to ensure player isn't bankrupt
			$("#manage_save").trigger('click');
		}
	}
});

function drawBoardHouses() {
	$.post("/getGameState", function(responseJSON) {
		var responseObject = JSON.parse(responseJSON);
		var players = responseObject.state.players;
		clearHouses();
		for (var i = 0; i < players.length; i++) {
			var monopolies = players[i].monopolies;
			for (var j = 0; j < monopolies.length; j++) {
				var props = monopolies[j].members;
				for (var k = 0; k < props.length; k++) {
					for (var h = 1; h <= props[k].numHouses; h++) {
						if (h > housesForHotel) {
							addHotel(props[k].id);
						} else {
							addHouse(props[k].id);
						}
					}
				}
			}
		}
	});
}

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
		drawBoardHouses();
		mortgages = {};
        houseTransactions = {};
        //make sure player isn't bankrupt
        if (currPlayer.isBroke) {
			customizeAndShowPopup({
				titleText: "BANKRUPTCY",
				showNoButton: false,
				message: currPlayer.name + " is Bankrupt! Mortgage property and/or Sell houses/hotels to pay off debt!"
			}, {
				okHandler: function() {
					turnMortgageOn();
				}
			});
		} else {
			$("#manage_button").css("background", "").css("box-shadow", "");
			$("#manage_button_bar").fadeOut(100);
			manageOn = false;
			m_enableOthers();
			$(".button").css("cursor", "pointer");
			if (bankruptcyOn) {
				checkBankruptcyAll();
			}
		}
	});
}

function findValidsDuringManage(buildSell) {
	//finds which properties can have houses built on them with the user's hypothetical builds
	if (buildSell) {
		validBuildsOrSells();
	} else {
		validMortgages();
	}
}


function validBuildsOrSells() {
	params = {
		buildOrDemortgage: buildOn,
		houses: JSON.stringify(dictToArray(houseTransactions)),
		playerID: currPlayer.id
	};
	findAndDrawValids(params, true);
}

function validMortgages() {
	params = {
		buildOrDemortgage: !mortgageOn,
		mortgages: JSON.stringify(dictToArray(mortgages)),
		playerID: currPlayer.id
	};
	findAndDrawValids(params, false);
}

function findAndDrawValids(params, buildSell) {
	$.post("/findValids", params, function(responseJSON) {
		var response = JSON.parse(responseJSON);
		var valids = response.valids;

		if (buildSell) {
			drawValidHouses(valids);
		} else {
			drawValidMortgages(valids, false);
		}
	});
}

function drawValidHouses(houses) {
	$('#monopolies_table tr').each(function () {
	  	var row = $(this);
	  	var propID = row.data().id;
	  	if ($.inArray(propID, houses) >= 0) {
	  		row.children("td").each(function() {
	  			var td = $(this);
	  			var condition;
	  			//valid houses are determined differently for buying/selling
	  			if (buildOn) {
	  				var prev = td.prev();
	  				var cond1 = prev.text().trim() == 'H' && td.text().trim() == "";
	  				var cond2 = td.index() == 2 && td.text().trim() == "";
	  				condition = cond1 || cond2;
	  			} else {
	  				var next = td.next();
	  				condition = td.text().trim() == "H" && next.text().trim() == "";
	  			}
	  			if (condition) {
			  		td.css('border', '1px dashed #000');
			  		td.data("valid", true);
			  	} else {
			  		td.data("valid", false);
			  	}
	  		});
	  	} else {
	  		row.children("td").data("valid", false);
	  	}  	
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
		  	td.data("canMortgage", true);
		  	td.css('border', '1px dashed #000');
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
	if (buildOn) {
		var sqID = "#sq_" + id;
		if ($(sqID).data().houses == housesForHotel) {
			addHotel(id);
		} else {
			addHouse(id);
		}
	} else {
		removeBuilding(id);
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
		if (td.data().valid) {
			td.data("valid", false).css("border", "");
			var propID = td.parent().data().id;
			var cost = td.parent().data().cost;
			var updatedCash = 0;
			//figure out if buying/selling
			if (buildOn) {
				//add a house
			  	td.text("H");
				//figure out if the player is actually building or just negating a previous sell that was made but not yet submitted
				var houses = houseTransactions[propID];
				if (houses != undefined && houses[1] < 0) {
					cost /= 2;
				}
				updatedCash = $("#player_wealth").data().cash - cost;
			} else {
				td.text("");
				//figure out if the player is actually selling or just negating a previous build that was made but not yet submitted
				cost /= 2;
				var houses = houseTransactions[propID];
				if (houses != undefined && houses[1] > 0) {
					cost *= 2;
				}
				updatedCash = $("#player_wealth").data().cash + cost;
			}
			//update the player's cash label
			$("#player_wealth").data("cash", updatedCash);
			$("#player_wealth").text("Cash: $" + updatedCash);
			//remove/add house from houseTransactions, find valids with this change
		  	buildSellHouse(propID);
			findValidsDuringManage(true);	
		} else if (td.data().canMortgage) {
			td.data("canMortgage", false).css("border", "");
			var propID = td.parent().data().id;
			var mortMoney = td.parent().data().mortgageMoney;
			var updatedCash = 0;
			//figure out if mortgaging/demortgaging
			if (mortgageOn) {
				td.text("M");
				updatedCash = $("#player_wealth").data().cash + mortMoney;
			} else {
				td.text("");
				//check if user is actually demortgaging or just negating an unsubmitted mortgage
				if (mortgages[propID] == undefined) {
					mortMoney *= (11.0/10.0);
					mortMoney = Math.floor(mortMoney);
				}
				updatedCash = $("#player_wealth").data().cash - mortMoney;
			}
			//update cash label
			$("#player_wealth").data("cash", updatedCash);
			$("#player_wealth").text("Cash: $" + updatedCash);
			//demortgage/mortgage the property
			mortgage(propID, mortgageOn);
			findValidsDuringManage(false);
		}
	}  	
});

function turnBuildOn() {
	clearValids();
	buildOn = true;
	validBuildsOrSells();
	$(".manage_button").removeClass("selected_button");
	$("#manage_build").addClass("selected_button");
}

function turnSellOn() {
	clearValids();
	buildOn = false;
	validBuildsOrSells();
	$(".manage_button").removeClass("selected_button");
	$("#manage_sell").addClass("selected_button");
}

function turnMortgageOn() {
	clearValids();
	mortgageOn = true;
	validMortgages();
	$(".manage_button").removeClass("selected_button");
	$("#manage_mortgage").addClass("selected_button");
}

function turnDemortgageOn() {
	clearValids();
	mortgageOn = false;
	validMortgages();
	$(".manage_button").removeClass("selected_button");
	$("#manage_demortgage").addClass("selected_button");
}

function clearValids() {
	$('table.player_table tr').children('td').css("border", "").data("valid", false).data("canMortgage", false);
}

function hideOtherTabs(id) {
	$(".player_tab").each(function() {
		if ($(this).data().playerID != id) {
			$(this).hide(0);
		}
	});
}

// function defaultArg(arg, def) {
// 	return typeof arg !== 'undefined' ? arg : def;
// }

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
	if (!pauseDisabled) {
		$(".button_bar_button").removeClass("selected");
		$("#pause_button").addClass("selected");
		
		$("#popup_pause").fadeIn(200);
		$("#screen").css("opacity", ".2");
		pauseOn = true;
		$(".button").css("cursor", "default");
		$(".popup_button").css("cursor", "pointer");
		$("#paused_screen").show(0);
	}
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
