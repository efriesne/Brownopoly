var currPlayer;
var prevPosition;
var secondMove = false;

/*
Function to be called at the beginning of each player's turn
 */
function startTurn() {
	$.post("/startTurn", function(responseJSON){
		var responseObject = JSON.parse(responseJSON);
		currPlayer = responseObject.player;
		if(!currPlayer.isAI) {
			alert("It is " + currPlayer.name + "'s turn!");
			if (currPlayer.inJail) {
				alert("You are in jail. You can try to roll doubles, " +
				"pay $50 or use a get out of jail free cards.");
			}
		} else {
			//execute AI's turn
		}
		//get trade and buying decisions
	});
}

// roll handler rolls the dice and determines if player is able to move
// move handler moves the player in the backend and tells the frontend if more input is needed
// play handler calls the execute effect method of the board square and feeds user input if necessary
// input is 1 if the user wants to buy and 0 otherwise
// if input was not necessary, then won't be used
$("#trade_button").bind('click', function() {
	startTurn();
});


$("#roll_button").bind('click', function() {
	if (currPlayer.inJail && (currPlayer.turnsInJail == 3)) {
		alert("Cannot roll. Pust pay bail.");
	} else {
		$.post("/roll", function(responseJSON){
			var result = JSON.parse(responseJSON);
			var dice = result.dice;
		    var canMove = result.canMove;
			alert(currPlayer.name + " rolled a " + dice.die1.num + " and a " + dice.die2.num);

			if (currPlayer.inJail && canMove) {
				alert("player is out of Jail!");
			}
			if (!currPlayer.inJail && !canMove) {
				alert("rolled doubles 3 times, sent to Jail!");			
				secondMove = true;
				move((10 - prevPosition + 40) % 40);
			}

			if (canMove) {
				move(dice.die1.num + dice.die2.num);
			} else {
				alert(currPlayer.name + " turn is over");
			}
		});
	}
});

function move(dist) {
	movePlayer(dist);

	var timeout = 0;
	if(secondMove) {
		timeout = 800;
		dist = 0;
	} else {
		timeout = dist * 400;
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
				alert(currPlayer.name + " landed on " + squareName + "!");
			}
			secondMove = false;
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
		alert(result.message);
		if (prevPosition != currPlayer.position) {
			secondMove = true;
			move((currPlayer.position - prevPosition + 40) % 40);
		}
	});
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
				cumulativeLeft -= 61;
			} else if(position > 0 && position < 9) {
				cumulativeLeft -= 42;
			} else if(position == 9) {
				cumulativeLeft -= 60;
			} else if(position == 10) {
				cumulativeBottom += 61;
			} else if(position > 10 && position < 19) {
				cumulativeBottom += 42;
			} else if(position == 19) {
				cumulativeBottom += 60;
			} else if(position == 20) {
				cumulativeLeft += 61;
			} else if(position > 20 && position < 29) {
				cumulativeLeft += 42;
			} else if(position == 29) {
				cumulativeLeft += 60;
			} else if(position == 30) {
				cumulativeBottom -= 61;
			} else if(position > 30 && position < 39) {
				cumulativeBottom -= 42;
			} else if(position == 39) {
				cumulativeBottom -= 60;
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
		$("#" + player_id).animate({"left": "-=61px"});
	} else if(position > 0 && position < 9) {
		$("#" + player_id).animate({"left": "-=42px"});
	} else if(position == 9) {
		$("#" + player_id).animate({"left": "-=60px"});
	} else if(position == 10) {
		$("#" + player_id).animate({"bottom": "+=61px"});
	} else if(position > 10 && position < 19) {
		$("#" + player_id).animate({"bottom": "+=42px"});
	} else if(position == 19) {
		$("#" + player_id).animate({"bottom": "+=60px"});
	} else if(position == 20) {
		$("#" + player_id).animate({"left": "+=61px"});
	} else if(position > 20 && position < 29) {
		$("#" + player_id).animate({"left": "+=42px"});
	} else if(position == 29) {
		$("#" + player_id).animate({"left": "+=60px"});
	} else if(position == 30) {
		$("#" + player_id).animate({"bottom": "-=61px"});
	} else if(position > 30 && position < 39) {
		$("#" + player_id).animate({"bottom": "-=42px"});
	} else if(position == 39) {
		$("#" + player_id).animate({"bottom": "-=60px"});
	}
	currPlayer.position = (position + 1) % 40;
}