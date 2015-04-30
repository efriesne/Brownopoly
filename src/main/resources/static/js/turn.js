$('#newsfeed').append("\n");

function getOutOfJail(jailCard) {
	params = {jailCard : jailCard};
	$.post("/getOutOfJail", params, function(responseJSON) {
		currPlayer = JSON.parse(responseJSON).player;
		loadPlayer(currPlayer);
		$('#newsfeed').append("-> " + currPlayer.name + " is out of Jail!\n");
		newsFeed.scrollTop = newsFeed.scrollHeight;
	});
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
			alert(currPlayer.name + " has won the game!");
			//gameOver();
		} else {
			$('#newsfeed').append("\n");
			if (prevPlayer != null) {
				loadPlayer(prevPlayer);
				if (prevPlayer.id == currPlayer.id) {
					$('#newsfeed').append(currPlayer.name + " rolled doubles. Roll again!\n");
					newsFeed.scrollTop = newsFeed.scrollHeight;
				} else {
					$('#newsfeed').append("It is " + currPlayer.name + "'s turn!\n");
					newsFeed.scrollTop = newsFeed.scrollHeight;
				}
			} else {
				$('#newsfeed').append("It is " + currPlayer.name + "'s turn! Roll the dice or manage/trade your properties.\n");
				newsFeed.scrollTop = newsFeed.scrollHeight;
			}

			prevPlayer = currPlayer;
			loadPlayer(currPlayer);

			if(!currPlayer.isAI) {
				if (currPlayer.inJail) {
					if (currPlayer.turnsInJail == 2) {
						alert("You have been in jail for 2 turns, you must pay bail.");
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
						$('#newsfeed').append("-> You are in jail. You can try to roll doubles, " +
							"pay $50 or use a Get Out of Jail Free card.\n");
							newsFeed.scrollTop = newsFeed.scrollHeight;
						if (currPlayer.jailFree) {
							if (confirm("You have a Get Out of Jail Free card! Do you want to use it?")) {
								getOutOfJail(1);
							} else {
								if (confirm("Do you want to pay $50 to get out of Jail?")) {
									getOutOfJail(0);
								}
							}
						} else {
							if (confirm("Do you want to pay $50 to get out of Jail?")) {
								getOutOfJail(0);
							}
						}
					}
				}
			} else {
				if (currPlayer.exitedJail) {
					$('#newsfeed').append("-> " + currPlayer.name + " is out of jail.\n");
					newsFeed.scrollTop = newsFeed.scrollHeight;
				}
				$.post("/startAITurn", function(responseJSON) {
					var responseObject = JSON.parse(responseJSON);
					var trade = responseObject.trade;
					var build = responseObject.build;
					currPlayer = responseObject.AI;
					if (build != "") {
						loadPlayer(currPlayer);
						$('#newsfeed').append("-> " + build + "\n");
						newsFeed.scrollTop = newsFeed.scrollHeight;
					}
					if (trade.hasTrade) {
						proposeTrade(trade);
					} else {
						roll();
					}
				});
			}
		}
	});
}

function proposeTrade(trade) {
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
		}, 20);
	} else {
		$('#newsfeed').append("-> " + currPlayer.name + " proposed a trade to " + recipient.name + "!\n");
		newsFeed.scrollTop = newsFeed.scrollHeight;
		var postParameters = {recipient: recipient.id, initProps: JSON.stringify(trade.initProps), initMoney: trade.initMoney,
			recipProps: JSON.stringify(trade.recipProps), recipMoney: trade.recipMoney};
		$.post("/trade", postParameters, function(responseJSON){
			var responseObject = JSON.parse(responseJSON);
			currPlayer = responseObject.initiator;
			if (responseObject.accepted) {
				$('#newsfeed').append("-> " + recipient.name + " accepted the trade!\n");
				newsFeed.scrollTop = newsFeed.scrollHeight;
			} else {
				$('#newsfeed').append("-> " + recipient.name + " rejected the trade!\n");
				newsFeed.scrollTop = newsFeed.scrollHeight;
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

	if(secondMove) {
		dist = 0;
	}

	var postParameters = {
		dist : dist
	};

	//wait until all animations are finished
	$("img").filter(":animated").promise().done(function() {
		console.log("animations done");
		$.post("/move", postParameters, function (responseJSON) {
			var result = JSON.parse(responseJSON);
			var squareName = result.squareName;
			var inputNeeded = result.inputNeeded;
			console.log("player pos: " + result.player.position);
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
	});
}

var playerBankruptcyCount;
var tmpCurrPlayer;
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
			$('#newsfeed').append("-> " + result.message + "\n");
			$('#newsfeed').append("-> " + currPlayer.name + " has a balance of $" + currPlayer.balance + "\n");
			newsFeed.scrollTop = newsFeed.scrollHeight;
		}
		
		//console.log("prev: " + prevPosition + ", curr: " + currPlayer.position);
		if (prevPosition != currPlayer.position) {
			secondMove = true;
			move((currPlayer.position - prevPosition + 40) % 40);
		} else {
			startTurn()
			/*
			$.post("/getGameState", postParameters, function(responseJSON) {
				var responseObject = JSON.parse(responseJSON);
				players = responseObject.state.players;
				playerBankruptcyCount = 0;
                bankruptcyOn = true;
                checkBankruptcy();
			});*/
		}
	});
}
/*
function checkBankruptcy() {
	if (playerBankruptcyCount == numPlayers) {
		$.post("/removeBankrupts", function(responseJSON) {
			bankruptcyOn = false;
			startTurn();
		});
    } else {
    	currPlayer = players[playerBankruptcyCount];
		if (currPlayer.isBroke) {
			if (currPlayer.isAI) {
				$.post("/mortgageAI", function(responseJSON) {
					responseObject = JSON.parse(responseJSON);
					currPlayer = responseObject.player;
					loadPlayer(currPlayer);
					$('#newsfeed').append("-> " + currPlayer.name + " paid off his/her debt\n");
                    newsFeed.scrollTop = newsFeed.scrollHeight;
					checkBankruptcy();
				} else {
					alert(currPlayer.name + " is Broke! Mortgage property and/or Sell houses/hotels to pay off debt!");
                	playerBankruptcyCount++;
                	$("#manage_button").trigger("click", [true]);
				}

			}
		} else {
			if (currPlayer.isBankrupt) {
				alert(currPlayer.name + " is Bankrupt and has been removed from the game!");
				//removePlayer(player[i]);
			}
			playerBankruptcyCount++;
			checkBankruptcy();
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