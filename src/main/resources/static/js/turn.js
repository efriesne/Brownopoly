var currplayer;

function startTurn() {
	$.post("/startTurn", function(responesJSON){
		var responseObject = JSON.parse(responseJSON);
		var dice = responseObject.dice;
		currplayer = responseObject.player;
		var square = responseObject.square;
		alert("It is " + player + "'s turn!");
		alert(player + " rolled a" + dice.roll1 + " and a " + dice.roll2);
	});
}

//when roll button is clicked
function roll() {
	var canMove = false;
	if (currplayer.inJail) {
		$.post("/jailroll", function(responseJSON) {
			/**
			 * display roll
			 * check if player can move
			 * if player can move, set canMove to true
			 */
		});
	} else {
		$.post("/roll", function(responseJSON){
			/**
			 * display roll
			 * check if player was sent to jail
			 * if player can move, set canMove to true
			 */
		});
	}
	
	if (canMove) {
		move()
	}

}

function move() {
	/**
	 * move player by amount on dice
	 * display name of square landed on
	 * display what happened
	 * get user input if needed:
	 * 		1. buying a property
	 * 		2. rolling dice to get utility rent
	 * check bankruptcy - if bankrupt, redisplay game without player
	 */
}