var currplayer;

function startTurn() {
	$.post("/startTurn", function(responseJSON){
		var responseObject = JSON.parse(responseJSON);
		console.log("here1");
		currplayer = responseObject.player;
		console.log("here2");
		alert("It is " + currplayer.name + "'s turn!");
		if (currplayer.inJail) {
			alert("You are in jail. You can try to roll doubles, " +
					"pay $50 or us a get out of jail free cards.");
		}
		//get trade and buying decisions
	});
}
$("#roll_button").bind('click', function() {
	console.log("registered click");
	roll();
});

//when roll button is clicked
function roll() {
	console.log("in roll function");
	var canMove = false;
	var dice;
	if (currplayer.inJail) {
		$.post("/jailroll", function(responseJSON) {
			result = JSON.parse(responseJSON);
			var paid = result.paid;
			dice = result.dice;
			canMove = result.move;
			if (paid) {
				alert(currplayer.name + " had to paid $50 to get out of jail.");
			}
			alert(currplayer + " rolled a" + dice.die1.num + " and a " + dice.die2.num);
		});
	} else {
		console.log("about to send roll request");
		$.post("/roll", function(responseJSON){
			console.log("got a response back");
			result = JSON.parse(responseJSON);
			var jail = false;
			dice = result.dice;
			console.log(dice);
			console.log(jail);
			alert(currplayer.name + " rolled a " + dice.die1.num + " and a " + dice.die2.num);
			if (jail) {
				alert(currplayer.name + " rolled doubles three times and must go to Jail!");
			} else {
				canMove = true;
				console.log(canMove);
				if (canMove) {
					move(dice)
				} else {
					alert(currplayer.name + "\'s turn is over.");
					startTurn();
				}
			}
		});
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
	//$.post("/move", function(responseJSON){
	//	resO = JSON.parse(responseJSON);
	//	var player = res0.player;
	//	var square = res0.square;
	//	var input = res0.input;
	//	alert("You landed on " + square + "!");
	//
	//});
	//if (input == 1) {
	//	if (!player.isAI) {
	//		alert("Do you want to buy this property?");
	//	}
	//} else if (input == 2) {
	//	alert("Roll dice to determine rent.");
	//	//make post to roll dice
	//}
	//var response; //get input from something
	//var postParameters = {response: response};
	//$.post("/execute", function(responseJSON){
	//	resO = JSON.parse(responseJSON);
	//	alert(res0.info);
	//	currplayer = res0.player;
	//});
	//
	
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