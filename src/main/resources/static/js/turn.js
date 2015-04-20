var currplayer;

/*
Function to be called at the beginning of each player's turn
 */
function startTurn() {
	$.post("/startTurn", function(responseJSON){
		var responseObject = JSON.parse(responseJSON);
		currplayer = responseObject.player;
		if(!currplayer.isAI) {
			alert("It is " + currplayer.name + "'s turn!");
			if (currplayer.inJail) {
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

$("#roll_button").bind('click', function() {
	var canMove = false;
	var dice;
	if (currplayer.inJail) {
		$.post("/jailroll", function(responseJSON) {
			var result = JSON.parse(responseJSON);
			var paid = result.paid;
			dice = result.dice;
			canMove = result.move;
			if (paid) {
				alert(currplayer.name + " had to paid $50 to get out of jail.");
			}
			alert(currplayer + " rolled a" + dice.die1.num + " and a " + dice.die2.num);
		});
	} else {
		$.post("/roll", function(responseJSON){
			var result = JSON.parse(responseJSON);
			var jail = false;
			dice = result.dice;
			alert(currplayer.name + " rolled a " + dice.die1.num + " and a " + dice.die2.num);
			if (jail) {
				alert(currplayer.name + " rolled doubles three times and must go to Jail!");
			} else {
				canMove = true;
				if (canMove) {
					move(dice)
				} else {
					alert(currplayer.name + "\'s turn is over.");
					startTurn();
				}
			}
		});
	}
});

function move(dice) {
	var dist = dice.die1.num + dice.die2.num;
	for(var i = 0; i < dist; i++) {
		stepPlayer();
	}

	$.post("/move", function(responseJSON){
		var result = JSON.parse(responseJSON);
		var squareName = result.squareName;
		var inputNeeded = result.inputNeeded;
		alert(currplayer.name + " landed on " + squareName + "!");
		execute(inputNeeded);
	});
}

function execute(inputNeeded) {
	var input = 0;
	if(inputNeeded) {
		//prompt user for more input
	}
	var postParameters = {
		input : input
	};
	$.post("/play", postParameters, function(responseJSON){
		startTurn();
	});
}

function stepPlayer() {
	var position = currplayer.position;
	var player_id = currplayer.id;
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
	currplayer.position = (position + 1) % 40;
}