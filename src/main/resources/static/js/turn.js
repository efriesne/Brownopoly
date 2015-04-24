var currPlayer;
var prevPosition;
var secondMove = false;
var outOfJail = false;
$('#newsfeed').append("\n");
var newsFeed = document.getElementById("newsfeed");
/*
Function to be called at the beginning of each player's turn
 */
function startTurn() {
	$.post("/startTurn", function(responseJSON){
		var responseObject = JSON.parse(responseJSON);
		prevPlayer = currPlayer;
		currPlayer = responseObject.player;
		if (prevPlayer != null) {
			loadPlayer(prevPlayer);
			if (prevPlayer.id == currPlayer.id) {
				$('#newsfeed').append("-> " + currPlayer.name + " rolled doubles. Roll again!\n");
				newsFeed.scrollTop = newsFeed.scrollHeight;
			} else {
				$('#newsfeed').append("-> " + prevPlayer.name + "'s turn is over. It is " + currPlayer.name + "'s turn!\n");
				newsFeed.scrollTop = newsFeed.scrollHeight;
			}
		} else {
			$('#newsfeed').append("-> It is " + currPlayer.name + "'s turn!\n");
			newsFeed.scrollTop = newsFeed.scrollHeight;
		}
		loadPlayer(currPlayer);
		if(!currPlayer.isAI) {
			if (currPlayer.inJail) {
				$('#newsfeed').append("-> You are in jail. You can try to roll doubles, " +
				"pay $50 or use a get out of jail free cards.\n");
				newsFeed.scrollTop = newsFeed.scrollHeight;
			}
		} else {
			if (currPlayer.exitedJail) {
				$('#newsfeed').append("-> AI paid bail.\n");
				newsFeed.scrollTop = newsFeed.scrollHeight;
			}
			roll();
		}
		//get trade and buying decisions
	});
}

// roll handler rolls the dice and determines if player is able to move
// move handler moves the player in the backend and tells the frontend if more input is needed
// play handler calls the execute effect method of the board square and feeds user input if necessary
// input is 1 if the user wants to buy and 0 otherwise
// if input was not necessary, then won't be used



var dice;
$("#roll_button").bind('click', function() {
	roll();
});

function roll() {
	if (currPlayer.inJail && (currPlayer.turnsInJail == 2)) {
		$('#newsfeed').append("-> Must pay bail.\n");
		newsFeed.scrollTop = newsFeed.scrollHeight;
	} 
	
	$.post("/roll", function(responseJSON) {
		var result = JSON.parse(responseJSON);
		dice = result.dice;
	    var canMove = result.canMove;

		$('#newsfeed').append("-> " + currPlayer.name + " rolled a " + dice.die1.num + " and a " + dice.die2.num + "\n");
		newsFeed.scrollTop = newsFeed.scrollHeight;
		if (currPlayer.inJail && canMove) {
			$('#newsfeed').append("-> player is out of Jail!\n");
			newsFeed.scrollTop = newsFeed.scrollHeight;
			move(dice.die1.num + dice.die2.num);
		} else if (!currPlayer.inJail && !canMove) {
			$('#newsfeed').append("-> rolled doubles 3 times, sent to Jail!\n");
			newsFeed.scrollTop = newsFeed.scrollHeight;
			secondMove = true;
			move((10 - prevPosition + 40) % 40);
		} else if (currPlayer.inJail && !canMove) {
			$('#newsfeed').append("-> Still in jail.\n");
			newsFeed.scrollTop = newsFeed.scrollHeight;
			startTurn();
		} else if (!currPlayer.inJail && canMove) {
			move(dice.die1.num + dice.die2.num);
		}
	});
}

function move(dist) {
	movePlayer(dist);

	var timeout = 0;
	if(secondMove) {
		timeout = 900;
		dist = 0;
	} else {
		timeout = dist * 450;
	}

	var postParameters = {
		dist : dist
	};
	setTimeout(function() {
		$.post("/move", postParameters, function (responseJSON) {
			var result = JSON.parse(responseJSON);
			var squareName = result.squareName;
			var inputNeeded = result.inputNeeded;
			prevPosition = result.player.position;
			if (!secondMove) {
				$('#newsfeed').append("-> " + currPlayer.name + " landed on " + squareName + "!\n");
				newsFeed.scrollTop = newsFeed.scrollHeight;
			}
			secondMove = false;
			if (currPlayer.isAI) {
				inputNeeded = 0;
			}
			execute(inputNeeded);
		});
	}, timeout);
}

function execute(inputNeeded) {
	var input = 0;
	if(inputNeeded) {
		//prompt user for more input
		var answer = confirm("Would you like to purchase this property?");
		if(answer) {
			input = 1;
		}
	}
	var postParameters = {
		input : input
	};
	$.post("/play", postParameters, function(responseJSON){
		var result = JSON.parse(responseJSON);
		currPlayer = result.player;
		if (result.message != "") {
			$('#newsfeed').append("-> " + result.message + "\n");
			newsFeed.scrollTop = newsFeed.scrollHeight;
		}
		
		if (prevPosition != currPlayer.position) {
			secondMove = true;
			move((currPlayer.position - prevPosition + 40) % 40);
		} else {
			if (currPlayer.isBankrupt) {
				$('#newsfeed').append("-> You are Bankrupt! You have been removed from the game.\n");
				newsFeed.scrollTop = newsFeed.scrollHeight;
				startTurn();
			} else if (currPlayer.isBroke) {
				$('#newsfeed').append("-> Your balance is negative. You must mortgage something\n");
				newsFeed.scrollTop = newsFeed.scrollHeight;
				mortgage();
			} else {
				startTurn();
			}
		}
	});
}

function mortagage() {
	/**
	 * TO DO
	 * don't allow them to end their turn until balance is non negative
	 */
}

function movePlayer(dist) {
	if(!secondMove) {
		for (var i = 0; i < dist; i++) {
			stepPlayer();
		}
	} else {
		var position = prevPosition;
		var player_id = currPlayer.id;
		var cumulativeLeft = 0;
		var cumulativeBottom = 0;
		for (var i = 0; i < dist; i++) {
			if(position == 0) {
				cumulativeLeft -= 71;
			} else if(position > 0 && position < 9) {
				cumulativeLeft -= 51;
			} else if(position == 9) {
				cumulativeLeft -= 71;
			} else if(position == 10) {
				cumulativeBottom += 71;
			} else if(position > 10 && position < 19) {
				cumulativeBottom += 51;
			} else if(position == 19) {
				cumulativeBottom += 71;
			} else if(position == 20) {
				cumulativeLeft += 71;
			} else if(position > 20 && position < 29) {
				cumulativeLeft += 51;
			} else if(position == 29) {
				cumulativeLeft += 71;
			} else if(position == 30) {
				cumulativeBottom -= 71;
			} else if(position > 30 && position < 39) {
				cumulativeBottom -= 51;
			} else if(position == 39) {
				cumulativeBottom -= 71;
			}
			position = (position + 1) % 40;
		}
		$("#" + player_id).animate({"left": "+=" + cumulativeLeft}, "fast");
		$("#" + player_id).animate({"bottom": "+=" + cumulativeBottom}, "fast");
	}
}

function stepPlayer() {
	var position = currPlayer.position;
	var player_id = currPlayer.id;
	if(position == 0) {
		$("#" + player_id).animate({"left": "-=71px"});
	} else if(position > 0 && position < 9) {
		$("#" + player_id).animate({"left": "-=51px"});
	} else if(position == 9) {
		$("#" + player_id).animate({"left": "-=71px"});
	} else if(position == 10) {
		$("#" + player_id).animate({"bottom": "+=71px"});
	} else if(position > 10 && position < 19) {
		$("#" + player_id).animate({"bottom": "+=51px"});
	} else if(position == 19) {
		$("#" + player_id).animate({"bottom": "+=71px"});
	} else if(position == 20) {
		$("#" + player_id).animate({"left": "+=71px"});
	} else if(position > 20 && position < 29) {
		$("#" + player_id).animate({"left": "+=51px"});
	} else if(position == 29) {
		$("#" + player_id).animate({"left": "+=71px"});
	} else if(position == 30) {
		$("#" + player_id).animate({"bottom": "-=71px"});
	} else if(position > 30 && position < 39) {
		$("#" + player_id).animate({"bottom": "-=51px"});
	} else if(position == 39) {
		$("#" + player_id).animate({"bottom": "-=71px"});
	}
	currPlayer.position = (position + 1) % 40;
}