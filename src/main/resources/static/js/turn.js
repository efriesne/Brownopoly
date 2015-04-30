function scrollNewsfeed(toAppend) {
	newsfeed.append(toAppend);
	var tempNewsfeed = document.getElementById("newsfeed");
	tempNewsfeed.scrollTop = tempNewsfeed.scrollHeight;
}


/*
Function to be called at the beginning of each player's turn
 */
function startTurn() {
	rollDisabled = false;
	$.post("/startTurn", function(responseJSON){
		var responseObject = JSON.parse(responseJSON);
		currPlayer = responseObject.player;
		numPlayers = responseObject.numPlayers;
		if (numPlayers == 1) {
			loadPlayer(currPlayer);
			alert(currPlayer.name + " has won the game!");
			$("#popup_quit").trigger('click');
		} else {
			scrollNewsfeed("\n");
			if (prevPlayer != null) {
				loadPlayer(prevPlayer);
				if (prevPlayer.id == currPlayer.id) {
					scrollNewsfeed(currPlayer.name + " rolled doubles. Roll again!\n");
				} else {
					scrollNewsfeed("It is " + currPlayer.name + "'s turn!\n");
				}
			} else {
				scrollNewsfeed("It is " + currPlayer.name + "'s turn! Roll the dice or manage/trade your properties.\n");
			}

			prevPlayer = currPlayer;
			loadPlayer(currPlayer);

			if(!currPlayer.isAI) {
				if (currPlayer.inJail) {
					handleInJail();
				}
			} else {
				if (currPlayer.exitedJail) {
					scrollNewsfeed("-> " + currPlayer.name + " is out of jail.\n");
				}
				$.post("/startAITurn", function(responseJSON) {
					var responseObject = JSON.parse(responseJSON);
					var trade = responseObject.trade;
					var build = responseObject.build;
					currPlayer = responseObject.AI;
					if (build != "") {
						loadPlayer(currPlayer);
						scrollNewsfeed("-> " + build + "\n");
					}
					if (trade.hasTrade) {
						makeTrade(trade);
					} else {
						roll();
					}
				});
			}
		}
	});
}

function getOutOfJail(jailCard) {
	params = {jailCard : jailCard};
	$.post("/getOutOfJail", params, function(responseJSON) {
		currPlayer = JSON.parse(responseJSON).player;
		loadPlayer(currPlayer);
		scrollNewsfeed("-> " + currPlayer.name + " is out of Jail!\n");
	});
}

function handleInJail() {
	console.log(fastPlay);
	if ((fastPlay) || (currPlayer.turnsInJail == 2)) {
		alert(currPlayer.name + ", you must pay bail.");
		if (currPlayer.jailFree) {
			if (confirm("You have a Get Out Of Jail Free card! Do you want to use it?")) {
				getOutOfJail(1);
			} else {
				getOutOfJail(0);
			}
		} else {
			getOutOfJail(0);
		}
	} else {
		scrollNewsfeed("-> You are in jail. You can try to roll doubles, " +
			"pay $50 or use a Get Out of Jail Free card.\n");
		if (currPlayer.jailFree) {
			if (confirm(currPlayer.name + ", you have a Get Out of Jail Free card! Do you want to use it?")) {
				getOutOfJail(1);
			} else {
				if (confirm("Do you want to pay $50 to get out of Jail?")) {
					getOutOfJail(0);
				}
			}
		} else {
			if (confirm(currPlayer.name + ", do you want to pay $50 to get out of Jail?")) {
				getOutOfJail(0);
			}
		}
	}
}

function makeTrade(trade) {
	var recipient = trade.recipient;
	if (!trade.recipient.isAI) {
		alert(currPlayer.name + " wants to propose a trade to " + recipient.name + "!");
		setUpTrade(trade);
		setTimeout(function() {
			if (confirm(recipient.name + ", Do you accept this trade?")) {
				var postParameters = {recipient: recipient.id, initProps: JSON.stringify(trade.initProps), initMoney: trade.initMoney,
							recipProps: JSON.stringify(trade.recipProps), recipMoney: trade.recipMoney};
				$.post("/trade", postParameters, function(responseJSON){
					var responseObject = JSON.parse(responseJSON);
					currPlayer = responseObject.initiator;
					endTrade();
					roll();
				});
			} else {
				endTrade();
				roll();
			}
		}, 200);
	} else {
		scrollNewsfeed("-> " + currPlayer.name + " proposed a trade to " + recipient.name + "!\n");
		var postParameters = {recipient: recipient.id, initProps: JSON.stringify(trade.initProps), initMoney: trade.initMoney,
			recipProps: JSON.stringify(trade.recipProps), recipMoney: trade.recipMoney};
		$.post("/trade", postParameters, function(responseJSON){
			var responseObject = JSON.parse(responseJSON);
			currPlayer = responseObject.initiator;
			if (responseObject.accepted) {
				scrollNewsfeed("-> " + recipient.name + " accepted the trade!\n");
			} else {
				scrollNewsfeed("-> " + recipient.name + " rejected the trade!\n");
			}
			roll();
		});
	}
}

function roll() {
	
	$.post("/roll", function(responseJSON) {
		var result = JSON.parse(responseJSON);
		var dice = result.dice;
	    var canMove = result.canMove;

		scrollNewsfeed("-> " + currPlayer.name + " rolled a " + dice.die1.num + " and a " + dice.die2.num + "\n");
		if (currPlayer.inJail && canMove) {
			scrollNewsfeed("-> player is out of Jail!\n");
			move(dice.die1.num + dice.die2.num);
		} else if (!currPlayer.inJail && !canMove) {
			scrollNewsfeed("-> rolled doubles 3 times, sent to Jail!\n");
			secondMove = true;
			move((10 - prevPosition + 40) % 40);
		} else if (currPlayer.inJail && !canMove) {
			scrollNewsfeed("-> Still in jail.\n");
			startTurn();
		} else if (!currPlayer.inJail && canMove) {
			move(dice.die1.num + dice.die2.num);
		}
	});
}

function move(dist) {
	movePlayer(currPlayer, dist, prevPosition);

	if(secondMove) {
		dist = 0;
	}

	var postParameters = {
		dist : dist
	};

	//wait until all animations are finished
	$("img").filter(":animated").promise().done(function() {
		$.post("/move", postParameters, function (responseJSON) {
			var result = JSON.parse(responseJSON);
			var squareName = result.squareName;
			var inputNeeded = result.inputNeeded;
			prevPosition = result.player.position;
			if (!secondMove) {
				scrollNewsfeed("-> " + currPlayer.name + " landed on " + squareName + "!\n");
			}
			secondMove = false;
			if (currPlayer.isAI) {
				inputNeeded = 0;
			}
			execute(inputNeeded);
		});
	});
}

var playerBankruptcyCount;
var players;
function execute(inputNeeded) {
	var input = 0;
	if(inputNeeded) {
		var answer = confirm("Would you like to purchase this property?");
		if(answer) {
			input = 1;
		}
	}
	var postParameters = {input : input};
	$.post("/play", postParameters, function(responseJSON){
		var result = JSON.parse(responseJSON);
		currPlayer = result.player;
		if (result.message != "") {
			scrollNewsfeed("-> " + result.message + "\n");
			if(!currPlayer.isBankrupt) {
				scrollNewsfeed("-> " + currPlayer.name + " has a balance of $" + currPlayer.balance + "\n");
			}
		}
		
		if (prevPosition != currPlayer.position) {
			secondMove = true;
			move((currPlayer.position - prevPosition + 40) % 40);
		} else {
			$.post("/getGameState", postParameters, function(responseJSON) {
				var responseObject = JSON.parse(responseJSON);
				players = responseObject.state.players;
				playerBankruptcyCount = 0;
                bankruptcyOn = true;
                checkBankruptcy();
			});
		}
	});
}

function checkBankruptcy() {
	if (playerBankruptcyCount == numPlayers) {
		$.post("/removeBankrupts", function(responseJSON) {
			bankruptcyOn = false;
			startTurn();
		});
    } else {
    	currPlayer = players[playerBankruptcyCount];
		if (currPlayer.isBroke) {
			playerBankruptcyCount++;
			if (currPlayer.isAI) {
				params = {player: currPlayer.id};
				$.post("/mortgageAI", params, function(responseJSON) {
					responseObject = JSON.parse(responseJSON);
					currPlayer = responseObject.player;
					var msg = responseObject.mortgage;
					loadPlayer(currPlayer);
					scrollNewsfeed("\n-> " + currPlayer.name + " was bankrupt and paid off his/her debt\n");
					checkBankruptcy();
				});
			} else {
				alert(currPlayer.name + " is Broke! Mortgage property and/or Sell houses/hotels to pay off debt!");
				$("#manage_button").trigger("click", [true]);
			}
		} else {
			if (currPlayer.isBankrupt) {
				alert(currPlayer.name + " is Bankrupt and has been removed from the game!");
				removePlayer(currPlayer);
			}
			playerBankruptcyCount++;
			checkBankruptcy();
		}
	}

}


function movePlayer(player, dist, previous_position) {
	if(!secondMove) {
		for (var i = 0; i < dist; i++) {
			stepPlayer();
		}
	} else {
		var position = previous_position;
		var player_id = player.id;
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
	//console.log("calling step player");
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