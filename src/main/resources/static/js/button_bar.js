function disableAll() {
	tradeDisabled = true;
	rollDisabled = true;
	manageDisabled = true;
	pauseDisabled = true;
	$(".button_bar_button").css("cursor", "default");
}

function disablePause() {
	pauseDisabled = true;
	$("#pause_button").css("opacity", .2);
}

function enableAll() {
	tradeDisabled = false;
	rollDisabled = false;
	manageDisabled = false;
	pauseDisabled = false;
	$(".button_bar_button").css("opacity", 1);
	$(".button_bar_button").css("cursor", "pointer");
}

function disableTurnButtons() {
	tradeDisabled = true;
	rollDisabled = true;
	manageDisabled = true;
	$("#roll_button").css("opacity", .2);
	$("#trade_button").css("opacity", .2);
	$("#manage_button").css("opacity", .2);
	$("#roll_button").css("cursor", "default");
	$("#trade_button").css("cursor", "default");
	$("#manage_button").css("cursor", "default");
}

function m_disableOthers() {
	tradeDisabled = true;
	rollDisabled = true;
	$("#roll_button").css("opacity", .2);
	$("#trade_button").css("opacity", .2);
	$("#roll_button").css("cursor", "default");
	$("#trade_button").css("cursor", "default");
}

function m_enableOthers() {
	tradeDisabled = false;
	rollDisabled = false;
	$("#roll_button").css("opacity", 1);
	$("#trade_button").css("opacity", 1);
	$("#roll_button").css("cursor", "pointer");
	$("#trade_button").css("cursor", "pointer");
}

function findEnabled() {
	var enabledButtons = [];
	if (!manageDisabled) {
		enabledButtons.push("manage_button");
	}
	if (!rollDisabled) {
		enabledButtons.push("roll_button");
	}
	if (!tradeDisabled) {
		enabledButtons.push("trade_button");
	}
	return enabledButtons;
}

function enableButtons(buttonIDList) {
	for (var i = 0; i < buttonIDList.length; i++) {
		enableButton(buttonIDList[i]);
	}
}

function enableButton(buttonID) {
	var button = $("#" + buttonID);
	button.css("opacity", 1).css("cursor", "pointer");
	switch(buttonID) {
		case "roll_button":
			rollDisabled = false;
			break;
		case "pause_button":
			pauseDisabled = false;
			break;
		case "manage_button":
			manageDisabled = false;
			break;
		case "trade_button":
			tradeDisabled = false
			break;
		default:
			break;
	}
}

/* ############################################
###############################################

 #####   ####   #      #
 #	 #  #    #  #      #
 #####  #    #  #      #
 # #    #    #  #      #
 #  #   #    #  #      #
 #   #   ####   #####  #####

###############################################
############################################ */

//see turn.js

$("#roll_button").bind('click', function() {
	if (!rollDisabled && !popupUp) {
		disableTurnButtons();
		roll();
	}
});


/* ############################################
###############################################

 #    #    ##    #    #    ##     ####   ######
 ##  ##   #  #   ##   #   #  #   #    #  #
 # ## #  #    #  # #  #  #    #  #       #####
 #    #  ######  #  # #  ######  #  ###  #
 #    #  #    #  #   ##  #    #  #    #  #
 #    #  #    #  #    #  #    #   ####   ######

###############################################
############################################ */

$("#manage_button").on('click', function(event, mortgage) {
	if (!manageDisabled && !popupUp) {
		if (!manageOn) {
			mortgages = {};
			houseTransactions = {};
			loadPlayer(currPlayer);
			manageOn = true;
			m_disableOthers();
			$(".button").css("cursor", "default");
			$(".manage_button").css("cursor", "pointer");
			$("#pause_button").css("cursor", "pointer");
			//make this button appear selected
			$("#manage_button").addClass("selected_button");
			$("#manage_button_bar").fadeIn(200);
			hideOtherTabs(currPlayer.id);
			//if mortgage is true, click event was triggered because player went under
			//force player to mortgage
			if (mortgage) {
				turnMortgageOn();
			} else {
				turnBuildOn();
			}
		} else {
			$("#manage_cancel").trigger('click');
		}
	}
});

//see manage.js

/* ####################################
#######################################

 #####     ##    #    #   ####   ######
 #    #   #  #   #    #  #       #
 #    #  #    #  #    #   ####   #####
 #####   ######  #    #       #  #
 #       #    #  #    #  #    #  #
 #       #    #   ####    ####   ######

#######################################
#################################### */

$("#pause_button").on('click', function() {
	if (!pauseDisabled && !popupUp) {
		$("#pause_button").addClass("selected_button");
		$("#popup_pause").fadeIn(200);
		$("#screen").css("opacity", ".2");
		pauseOn = true;
		//find the buttons that were enabled at the time of pausing
		var enabledButtons = findEnabled();
		enabledButtons.push("pause_button");
		//when we resume, only re-enable the buttons that were enabled at the time of pausing
		$("#resume_button").off().on('click', function() {
			resumeFunction(enabledButtons);
		});
		disableAll();
		$(".button").css("cursor", "default");
		$(".popup_button").css("cursor", "pointer");
		$("#paused_screen").show(0);
	}
});

//made separate function so it can be re-bound to resume_button -> see utils.js and $("#pause_button").on() above
function resumeFunction(buttonList) {
	if (!saveOn) {
		enableButtons(buttonList);
		resumeRestore();
	}
}

$("#pause_quit").on('click', function() {
	$("#manage_cancel").trigger('click');
	resumeRestore();
	clearGameSettings();
	prevPopupStack = [];
	//closes popup
	defaultHandler();
	disableAll();
	$("#game_settings").hide(0);
	$("#load_screen").hide(0);
	$("#home_options").show(0);
	$("#home_screen").slideDown(500);
});

$("#pause_help").on('click', function() {
	if (pauseOn && !saveOn) {
		$("#help_center").fadeIn(200);
		helpOn = true;
	}
});

$("#help_close").on('click', function() {
	closeHelp();
});

function closeHelp() {
	$("#help_center").fadeOut(100);
	helpOn = false;
}

function resumeRestore() {
	$("#popup_pause").fadeOut(200);
	$("#screen").css("opacity", "1");
	$("#pause_button").removeClass("selected_button");
	pauseOn = false;
	$(".button").css("cursor", "pointer");
	$("#paused_screen").hide(0);
	if(currPlayer.isAI) {
		disableTurnButtons();
	}
}

/* ####################################
#######################################

####### ######     #    ######  #######
   #    #     #   # #   #     # #
   #    #     #  #   #  #     # #
   #    ######  #     # #     # #####
   #    #   #   ####### #     # #
   #    #    #  #     # #     # #
   #    #     # #     # ######  #######

#######################################
#################################### */

$("#trade_button").on("click", function(){
	if (!tradeDisabled && !popupUp) {
		setUpTrade();
	}
});

//see trade.js
