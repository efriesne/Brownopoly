var currplayer;

startTurn();

function startTurn() {
	$.post("/startTurn", function(responesJSON){
		var responseObject = JSON.parse(responseJSON);
		currplayer = responseObject.player;
		alert("It is " + currplayer.name + "'s turn!");
		if (currplayer.inJail) {
			alert("You are in jail. You can try to roll doubles, " +
					"pay $50 or us a get out of jail free cards.");
		}
		//get trade and buying decisions
	});
}
$("#roll_button").bind('click', roll());

//when roll button is clicked
function roll() {
	console.log("registered click");
	var canMove = false;
	var dice;
	if (currplayer.inJail) {
		$.post("/jailroll", function(responseJSON) {
			resO = JSON.parse(responseJSON);
			var paid = res0.paid;
			dice = res0.dice;
			canMove = res0.move;
			if (paid) {
				alert(currplayer.name + " had to paid $50 to get out of jail.");
			}
			alert(currplayer + " rolled a" + dice.die1.num + " and a " + dice.die2.num);
		});
	} else {
		$.post("/roll", function(responseJSON){
			resO = JSON.parse(responseJSON);
			var jail = res0.jail;
			dice = res0.dice;
			alert(currplayer.name + " rolled a" + dice.die1.num + " and a " + dice.die2.num);
			if (jail) {
				alert(currplayer.name + " rolled doubles three times and must go to Jail!");
			} else {
				canMove = true;
			}
		});
	}
	
	if (canMove) {
		move(dice)
	} else {
		alert(currentplayer.name + "\'s turn is over.");
		startTurn();
	}
}

function move(dice) {
	/**
	 * move player by amount on dice
	 * display name of square landed on
	 * display what happened
	 * get user input if needed:
	 * 		1. buying a property
	 * 		2. rolling dice to get utility rent
	 * check bankruptcy - if bankrupt, redisplay game without player
	 */
	var dist = dice.die1.num + dice.die2.num;
	for(var i = 0; i < dist; i++) {
		stepPlayer();
	}
	$.post("/move", function(responseJSON){
		resO = JSON.parse(responseJSON);
		var player = res0.player;
		var square = res0.square;
		var input = res0.input;
		alert("You landed on " + square + "!");
		
	});
	if (input == 1) {
		if (!player.isAI) {
			alert("Do you want to buy this property?");
		}
	} else if (input == 2) {
		alert("Roll dice to determine rent.");
		//make post to roll dice
	}
	var response; //get input from something
	var postParameters = {response: response};
	$.post("/execute", function(responseJSON){
		resO = JSON.parse(responseJSON);
		alert(res0.info);
		currplayer = res0.player;
	});
	
	
	/**
	 * 1. check if bankrupt + make post that removes player
	 * 2. redisplay current balance
	 * 3. redisplay properties
	 * 4. 
	 */

}

function stepPlayer() {
	var position = currplayer.position;
	var player_id = currplayer.id;
	if(position == 0) {
		$("#" + player_id).animate({"left": "-=61px"}, "slow");
	} else if(position > 0 && position < 9) {
		$("#" + player_id).animate({"left": "-=42px"}, "slow");
	} else if(position == 9) {
		$("#" + player_id).animate({"left": "-=60px"}, "slow");
	} else if(position == 10) {
		$("#" + player_id).animate({"bottom": "+=61px"}, "slow");
	} else if(position > 10 && position < 19) {
		$("#" + player_id).animate({"bottom": "+=42px"}, "slow");
	} else if(position == 19) {
		$("#" + player_id).animate({"bottom": "-=60px"}, "slow");
	} else if(position == 20) {
		$("#" + player_id).animate({"left": "+=61px"}, "slow");
	} else if(position > 20 && position < 29) {
		$("#" + player_id).animate({"left": "+=42px"}, "slow");
	} else if(position == 29) {
		$("#" + player_id).animate({"left": "+=60px"}, "slow");
	} else if(position == 30) {
		$("#" + player_id).animate({"bottom": "-=61px"}, "slow");
	} else if(position > 30 && position < 39) {
		$("#" + player_id).animate({"bottom": "-=42px"}, "slow");
	} else if(position == 39) {
		$("#" + player_id).animate({"bottom": "-=60px"}, "slow");
	}
}