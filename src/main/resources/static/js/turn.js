var currPlayer;
var prevPosition;
var prevPlayer;
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
		prevPlayer = currPlayer;
		loadPlayer(currPlayer);
		if(!currPlayer.isAI) {
			if (currPlayer.inJail) {
				$('#newsfeed').append("-> You are in jail. You can try to roll doubles, " +
				"pay $50 or use a get out of jail free cards.\n");
				newsFeed.scrollTop = newsFeed.scrollHeight;
			}
		} else {
			console.log("here");
			if (currPlayer.exitedJail) {
				$('#newsfeed').append("-> AI paid bail.\n");
				newsFeed.scrollTop = newsFeed.scrollHeight;
			}
			$.post("/startAITurn", function(responseJSON) {
				var responseObject = JSON.parse(responseJSON);
				var trade = responseObject.trade;
				var build = responseObject.build;
				currPlayer = responseObject.AI;
				if (build != null) {
					loadPlayer(currPlayer);
					alert(build);
				}
				if (trade != null) {
					proposeTrade(trade);
				} else {
					roll();
				}
			});
		}
	});
}

function proposeTrade(trade) {
	var recipient = trade.recipient;
	if (!trade.recipient.isAI) {
		alert(currPlayer.name + " wants to propose a trade to " + recipient.name + "!");
		setUpTrade(trade);
		if (confirm(recipient.name + ", Do you accept this trade?")) {
			var postParameters = {recipient: recipient.id, initProps: JSON.stringify(trade.initProps), initMoney: trade.initMoney,
						recipProps: JSON.stringify(trade.recipProps), recipMoney: trade.recipMoney};
			$.post("/trade", postParameters, function(responseJSON){
				var responseObject = JSON.parse(responseJSON);
				currPlayer = responseObject.currPlayer;
				if (responseObject.accepted) {
					alert(recipient.name + " accepted the trade!");
				} else {
					alert(recipient.name + " rejected the trade!");
				}
				endTrade();
				roll();
			});
		} else {
			alert(recipient.name + " rejected the trade!");
			endTrade();
			roll();
		}
	} else {
		$('#newsfeed').append("-> " + currPlayer.name + " proposed a trade to " + recipient.name + "!");
		var postParameters = {recipient: recipient.id, initProps: JSON.stringify(trade.initProps), initMoney: trade.initMoney,
			recipProps: JSON.stringify(trade.recipProps), recipMoney: trade.recipMoney};
		$.post("/trade", postParameters, function(responseJSON){
			var responseObject = JSON.parse(responseJSON);
			currPlayer = responseObject.currPlayer;
			if (responseObject.accepted) {
				$('#newsfeed').append("-> " + recipient.name + " accepted the trade!");
			} else {
				$('#newsfeed').append("-> " + recipient.name + " rejected the trade!");
			}
			roll();
		});
	}
}

function roll() {
	if (currPlayer.inJail && (currPlayer.turnsInJail == 2)) {
		$('#newsfeed').append("-> Must pay bail.\n");
		newsFeed.scrollTop = newsFeed.scrollHeight;
	} 
	
	$.post("/roll", function(responseJSON) {
		var result = JSON.parse(responseJSON);
		var dice = result.dice;
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

var players;
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
			$('#newsfeed').append("-> " + currPlayer.name + " has a balance of $" + currPlayer.balance + "\n");
			newsFeed.scrollTop = newsFeed.scrollHeight;
		}
		
		if (prevPosition != currPlayer.position) {
			secondMove = true;
			move((currPlayer.position - prevPosition + 40) % 40);
		} else {
			startTurn();
			/*
        	 $.post("/getGameState", function(responseJSON) {
             	var responseObject = JSON.parse(responseJSON);
             	players = responseObject.state.players;
             });
			checkBankruptcy(0);*/
		}
	});
}
/*
function checkBankruptcy(numPlayer) {
	var player = player[numPlayer];
		if (player.isBankrupt) {
			$('#newsfeed').append("-> " + player.name + " is Bankrupt and has been removed from the game!.\n");
			//removePlayer(player[i]);
		} else if (currPlayer.isBroke) {

		}

		if (numPlayer > player.length) {

		}

		if (i == player.length-1) {
			startTurn();
		}
	}
}*/


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
				cumulativeLeft -= 70;
			} else if(position > 0 && position < 9) {
				cumulativeLeft -= 51;
			} else if(position == 9) {
				cumulativeLeft -= 70;
			} else if(position == 10) {
				cumulativeBottom += 70;
			} else if(position > 10 && position < 19) {
				cumulativeBottom += 51;
			} else if(position == 19) {
				cumulativeBottom += 70;
			} else if(position == 20) {
				cumulativeLeft += 70;
			} else if(position > 20 && position < 29) {
				cumulativeLeft += 51;
			} else if(position == 29) {
				cumulativeLeft += 70;
			} else if(position == 30) {
				cumulativeBottom -= 70;
			} else if(position > 30 && position < 39) {
				cumulativeBottom -= 51;
			} else if(position == 39) {
				cumulativeBottom -= 70;
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
		$("#" + player_id).animate({"left": "-=70px"});
	} else if(position > 0 && position < 9) {
		$("#" + player_id).animate({"left": "-=51px"});
	} else if(position == 9) {
		$("#" + player_id).animate({"left": "-=70px"});
	} else if(position == 10) {
		$("#" + player_id).animate({"bottom": "+=70px"});
	} else if(position > 10 && position < 19) {
		$("#" + player_id).animate({"bottom": "+=51px"});
	} else if(position == 19) {
		$("#" + player_id).animate({"bottom": "+=70px"});
	} else if(position == 20) {
		$("#" + player_id).animate({"left": "+=70px"});
	} else if(position > 20 && position < 29) {
		$("#" + player_id).animate({"left": "+=51px"});
	} else if(position == 29) {
		$("#" + player_id).animate({"left": "+=70px"});
	} else if(position == 30) {
		$("#" + player_id).animate({"bottom": "-=70px"});
	} else if(position > 30 && position < 39) {
		$("#" + player_id).animate({"bottom": "-=51px"});
	} else if(position == 39) {
		$("#" + player_id).animate({"bottom": "-=70px"});
	}
	currPlayer.position = (position + 1) % 40;
}