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
		num_players = responseObject.numPlayers;
		if(currPlayer.isAI) {
			disableTurnButtons();
		} else {
			enableAll();
		}
		if (num_players == 1) {
			loadPlayer(currPlayer);
			customizeAndShowPopup({
				titleText: "GAME OVER",
				showNoButton: false,
				okText: "Main Menu",
				message: currPlayer.name + " has won the game!"
			}, {
				okHandler: function() {
					$("#popup_quit").trigger('click');
				}
			});
		} else {
			scrollNewsfeed("\n");
			if (prevPlayer != null) {
				loadPlayer(prevPlayer);
				if (prevPlayer.id == currPlayer.id) {
					if (!currPlayer.isAI) {
						customizeAndShowPopup({
							titleText: "START TURN",
							showNoButton: false,
							message: currPlayer.name + " rolled doubles. Roll again!"
						});
					}
					scrollNewsfeed(currPlayer.name + " rolled doubles. Roll again!\n");
				} else {
					if (!currPlayer.isAI) {
						customizeAndShowPopup({
							titleText: "START TURN",
							showNoButton: false,
							message: "It is " + currPlayer.name + "'s turn!"
						});
					}
					scrollNewsfeed("It is " + currPlayer.name + "'s turn!\n");
				}
			} else {
				if (!currPlayer.isAI) {
					customizeAndShowPopup({
						titleText: "START TURN",
						showNoButton: false,
						message: "It is " + currPlayer.name + "'s turn! Roll the dice or manage/trade your properties."
					});
				}
				scrollNewsfeed("It is " + currPlayer.name + "'s turn! Roll the dice or manage/trade your properties.\n");
			}

			prevPlayer = currPlayer;
			loadPlayer(currPlayer);
			drawBoardHouses();

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
					var mortgage = responseObject.mortgage;
					currPlayer = responseObject.AI;
					loadPlayer(currPlayer);
					drawBoardHouses();
					if (build != "") {
						scrollNewsfeed("-> " + build + "\n");
					}
					if (mortgage != "") {
						scrollNewsfeed("-> " + mortgage + "\n");
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


function fastPlayEndGame(player) {
	$.post("/getFastPlayWinner", function(responseJSON) {
		var responseObject = JSON.parse(responseJSON);
		var winner = responseObject.winner;
		customizeAndShowPopup({
			titleText: "GAME OVER",
			showNoButton: false,
			okText: "Main Menu",
			message: "Game is over because " + player.name + " went bankrupt! " + winner.name + " has the highest wealth and wins!"
		}, {
			okHandler: function() {
				$("#popup_quit").trigger('click');
			}
		});
	});
}

function getOutOfJail(jailCard) {
	params = {jailCard : jailCard};
	$.post("/getOutOfJail", params, function(responseJSON) {
		currPlayer = JSON.parse(responseJSON).player;
		loadPlayer(currPlayer);
		scrollNewsfeed("-> " + currPlayer.name + " is out of Jail!\n");
		checkBankruptcy(currPlayer, false);
	});
}

function handleInJail() {
	if ((fastPlay) || (currPlayer.turnsInJail == 2)) {
		customizeAndShowPopup({
			titleText: "OUT OF JAIL",
			showNoButton: false,
			message: currPlayer.name + ", you must pay the $50 bail."
		}, {
			okHandler: function() {
				if (currPlayer.jailFree) {
					customizeAndShowPopup({
						titleText: "IN JAIL!",
						showNoButton: true,
						message: currPlayer.name + ", you have a Get Out Of Jail Free card! Do you want to use it?"
					}, {
						okHandler: function() {
							getOutOfJail(0);
						},
						noHandler: function() {
							getOutOfJail(1);
						}

					});
				} else {
					getOutOfJail(1);
				}
			}
		});
	} else {
		scrollNewsfeed("-> You are in jail. You can try to roll doubles, " +
			"pay $50 or use a Get Out of Jail Free card.\n");
		if (currPlayer.jailFree) {
			customizeAndShowPopup({
				titleText: "IN JAIL!",
				showNoButton: true,
				message: currPlayer.name + ", you have a Get Out Of Jail Free card! Do you want to use it?"
			}, {
				okHandler: function() {
					getOutOfJail(0);
				},
				noHandler: function() {
					customizeAndShowPopup({
						titleText: "IN JAIL!",
						showNoButton: true,
                    	message: "Do you want to pay $50 to get out of Jail?"
                    }, {
						okHandler: function() {
							getOutOfJail(1);
						}
					})
				}

			});
		} else {
			customizeAndShowPopup({
				titleText: "IN JAIL!",
				showNoButton: true,
				message: currPlayer.name + ", do you want to pay $50 to get out of Jail?"
			}, {
				okHandler: function() {
					getOutOfJail(1);
				}
			})
		}
	}
}

function makeTrade(trade) {
	var recipient = trade.recipient;
	if (!trade.recipient.isAI) {
		customizeAndShowPopup({
			titleText: "TRADE",
			showNoButton: false,
			message: currPlayer.name + " wants to propose a trade to " + recipient.name + "!"
		}, {
			okHandler: function() {
				setUpTrade(trade);
		}});
	} else {
		scrollNewsfeed("-> " + currPlayer.name + " proposed a trade to " + recipient.name + ":\n");
		var postParameters = {recipient: recipient.id, initProps: JSON.stringify(trade.initProps), initMoney: trade.initMoney,
			recipProps: JSON.stringify(trade.recipProps), recipMoney: trade.recipMoney};
		$.post("/trade", postParameters, function(responseJSON){
			var responseObject = JSON.parse(responseJSON);
			currPlayer = responseObject.initiator;
			scrollNewsfeed(" " + currPlayer.name + " wants to trade " + responseObject.msg + "\n");

			if (responseObject.accepted) {
				scrollNewsfeed("-> " + recipient.name + " accepted the trade!\n");
				scrollNewsfeed("-> " + responseObject.msg + "\n");
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
			scrollNewsfeed("-> " + currPlayer.name + " is out of Jail!\n");
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
			var canBuy = result.canBuy;
			var isCard = result.card;
			prevPosition = result.player.position;
			if (!secondMove) {
				scrollNewsfeed("-> " + currPlayer.name + " landed on " + squareName + "!\n");
			}
			secondMove = false;
			if (currPlayer.isAI) {
				inputNeeded = 0;
			}
			execute(inputNeeded, canBuy, isCard);
		});
	});
}

var playerBankruptcyCount;
var players;

function execute(inputNeeded, canBuy, isCard) {
	if(inputNeeded && canBuy) {
		var idx = currPlayer.position;
		var deed = deeds[idx];
		var div = populateDeed(deed);
		$("#popup_other_html").addClass("popup_preview");
		$("#popup_error").addClass("popup_shifted");
		disableAll();
		disablePause();
		customizeAndShowPopup({
			titleText: "PURCHASE?",
			message: "Would you like to purchase this property?",
			otherHtml: div.html(),
			okText: "Yes"
		}, {
			okHandler: function(event){
				$("#popup_other_html").removeClass("popup_preview");
				$("#popup_error").removeClass("popup_shifted");
				play(isCard, event.data);
				enableAll();
			}, 
			noHandler: function(event) {
				$("#popup_other_html").removeClass("popup_preview");
				$("#popup_error").removeClass("popup_shifted");
				play(isCard, event.data);
				enableAll();
			},
			okHandlerData: {input: 1},
			noHandlerData: {input: 0}
		}, true);
	} else if (inputNeeded && !canBuy) {
		customizeAndShowPopup({
			titleText: "PURCHASE",
			message: "You cannot afford this property.",
			showNoButton: false
		}, {
			okHandler: function() {
				play(isCard, {input: 1});
			}
		});
	} else {
		play(isCard, {input: 0});
	}
}

function play(isCard, postParameters) {
	$.post("/play", postParameters, function(responseJSON){
		var result = JSON.parse(responseJSON);
		currPlayer = result.player;
		if (result.message != "") {
			scrollNewsfeed("-> " + result.message + "\n");
			if (isCard && !currPlayer.isAI) {
				customizeAndShowPopup({
					titleText: "CARD",
					message: result.message,
					showNoButton: false
				}, {
					okHandler: function() {
						scrollNewsfeed("-> " + currPlayer.name + " has a balance of $" + currPlayer.balance + "\n");
						checkEndTurn()
					}
				});
			} else {
				scrollNewsfeed("-> " + currPlayer.name + " has a balance of $" + currPlayer.balance + "\n");
				checkEndTurn();
			}
		} else {
			scrollNewsfeed("-> " + currPlayer.name + " has a balance of $" + currPlayer.balance + "\n");
			checkEndTurn();
		}
	});
}

function checkEndTurn() {
	if (prevPosition != currPlayer.position) {
			secondMove = true;
			move((currPlayer.position - prevPosition + 40) % 40);
	} else {
		$.post("/getGameState", function(responseJSON) {
			var responseObject = JSON.parse(responseJSON);
			players = responseObject.state.players;
			playerBankruptcyCount = 0;
			bankruptcyOn = true;
			checkBankruptcyAll();
		});
	}
}

function checkBankruptcy(player, all) {
	if (player.isBroke) {
		console.log("broke: " + player.name);
		if (player.isAI) {
			console.log("broke AI");
			params = {player: player.id};
			$.post("/mortgageAI", params, function(responseJSON) {
				responseObject = JSON.parse(responseJSON);
				player = responseObject.player;
				var msg = responseObject.mortgage;
				console.log("AI msg: " + msg);
				loadPlayer(player);
				scrollNewsfeed("\n-> " + player.name + " was bankrupt " + msg + "\n");

				if (all) {
					checkBankruptcyAll();
				}
			});
		} else {
			customizeAndShowPopup({
				titleText: "BANKRUPTCY",
				showNoButton: false,
				message: player.name + " is Bankrupt! Mortgage property and/or Sell houses/hotels to pay off debt!"
			}, {
				okHandler: function() {
					if (all) {
						currPlayer = player;
					}
					manageDisabled = false;
					$("#manage_button").trigger("click", [true]);
				}
			});
		}
	} else {
		if (player.isBankrupt) {
			if (player.isAI) {
				scrollNewsfeed("\n-> " + player.name + " is Bankrupt and has been removed from the game!\n");
				if (!fastPlay) {
					removePlayer(player);
					if (all) {
						checkBankruptcyAll();
					} else {
						$.post("/removeBankrupts", function(responseJSON) {
							startTurn();
						});}
				} else {
					fastPlayEndGame(player);
				}
			} else {
				customizeAndShowPopup({
					titleText: "BANKRUPTCY",
					showNoButton: false,
					message: player.name + " is Bankrupt and has been removed from the game!"
				}, {
					okHandler: function() {
						if (!fastPlay) {
							removePlayer(player);
							if (all) {
								checkBankruptcyAll();
							} else {
								$.post("/removeBankrupts", function(responseJSON) {
									startTurn();
								});
							}
						} else {
							fastPlayEndGame(player);
						}
					}
				});
			}

		} else {
			if (all) {
				checkBankruptcyAll();
			}
		}
	}
}

function checkBankruptcyAll() {
	if (playerBankruptcyCount == num_players) {
		$.post("/removeBankrupts", function(responseJSON) {
			bankruptcyOn = false;
			startTurn();
		});
    } else {
    	var player = players[playerBankruptcyCount];
	    playerBankruptcyCount++;
		checkBankruptcy(player, true);
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
		player.position = position;
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