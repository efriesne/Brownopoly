/****************
MANAGE PROPERTIES
****************/

$("#manage_build").on('click', function() {
	if(!manageDisabled && !popupUp) {
		turnBuildOn();
	}
});

$("#manage_sell").on('click', function() {
	if(!manageDisabled && !popupUp) {
		turnSellOn();
	}
});

$("#manage_mortgage").on('click', function() {
	if(!manageDisabled && !popupUp) {
		turnMortgageOn();
	}
});

$("#manage_demortgage").on('click', function() {
	if(!manageDisabled && !popupUp) {
		turnDemortgageOn();
	}
});

$("#manage_save").on('click', function() {
	if(!manageDisabled && !popupUp) {
		if (manageOn) {
			manageProperties();
			clearValids();
			$(".player_tab").show(0);
		}
	}
});

$("#manage_cancel").on('click', function() {
	if(!manageDisabled && !popupUp) {
		if (manageOn) {
			drawBoardHousesAndMortgageStamps();
			mortgages = {};
			houseTransactions = {};
			//still need to call manageProperties to ensure player isn't bankrupt
			$("#manage_save").trigger('click');
		}
	}
});

function drawBoardHousesAndMortgageStamps() {
	$.post("/getGameState", function(responseJSON) {
		var responseObject = JSON.parse(responseJSON);
		var players = responseObject.state.players;
		drawBoardHouses(players);
		drawBoardMortgageStamps(players);
	});
}

//use this to draw mortgage stamps
function drawBoardHouses(players) {
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
}

function drawBoardMortgageStamps(players) {
	clearMortgageStamps();
	for (var i = 0; i < players.length; i++) {
		var monopolies = players[i].monopolies;
		for (var j = 0; j < monopolies.length; j++) {
			drawMortgageStampsHelper(monopolies[j].members);
		}
		drawMortgageStampsHelper(players[i].properties);
		drawMortgageStampsHelper(players[i].utilities);
		drawMortgageStampsHelper(players[i].railroads);
	}
}

function drawMortgageStampsHelper(list) {
	for (var j = 0; j < list.length; j++) {
		if (list[j].mortgaged) {
			addMortgageStamp(list[j].id);
		}
	}
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
		drawBoardHousesAndMortgageStamps();
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
			$("#manage_button").removeClass("selected_button");
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
	findAndDrawValids(true, buildOn);
}

function validMortgages() {
	findAndDrawValids(false, !mortgageOn);
}

function findAndDrawValids(buildSell, buildOrDemortgage) {
	params = {
		buildOrDemortgage: buildOrDemortgage,
		houses: JSON.stringify(dictToArray(houseTransactions)),
		mortgages: JSON.stringify(dictToArray(mortgages)),
		playerID: currPlayer.id
	};
	$.post("/findValids", params, function(responseJSON) {
		var response = JSON.parse(responseJSON);
		if (buildSell) {
			drawValidHouses(response.validHouses);
		} else {
			drawValidMortgages(response.validMortgages, false);
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
	var sqID = "#sq_" + id;
	if (buildOn) {
		if ($(sqID).data().houses == housesForHotel) {
			addHotel(id);
		} else {
			addHouse(id);
		}
	} else {
		removeBuilding(id);
		if ($(sqID).data().houses == housesForHotel) {
			$(sqID).data("houses", 0);
			for (var i = 0; i < housesForHotel; i++) {
				addHouse(id);
			}
		} 
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
			validBuildsOrSells();
		} else if (td.data().canMortgage) {
			td.data("canMortgage", false).css("border", "");
			var propID = td.parent().data().id;
			var mortMoney = td.parent().data().mortgageMoney;
			var updatedCash = 0;
			//figure out if mortgaging/demortgaging
			if (mortgageOn) {
				td.text("M");
				addMortgageStamp(propID);
				updatedCash = $("#player_wealth").data().cash + mortMoney;
			} else {
				td.text("");
				removeMortgageStamp(propID);
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
			validMortgages();
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