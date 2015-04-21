var manageDisabled = false;
var tradeDisabled = false;
var rollDisabled = false;

function disableAll() {
	manageDisabled = true;
	tradeDisabled = true;
	rollDisabled = true;
}

function enableAll() {
	manageDisabled = false;
	tradeDisabled = false;
	rollDisabled = false;
}


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

var manageOn = false;
var buildOn = false;

$("#manage_button_bar").hide(0);

$("#manage_button").bind('click', function() {
	if (!manageDisabled) {
		var button = $("#manage_button");
		if (!manageOn) {
			validBuilds();
			manageOn = true;
			button.css("background", "rgba(209, 251, 228, .7)");
			button.css("box-shadow", "0px 0px 7px #D1FBE4");
			$("#manage_button_bar").fadeIn(200);
			buildOnSellOff();
		} 
	}
});

$("#manage_save").bind('click', function() {
	if(!manageDisabled) {
		var button = $("#manage_button");
		if (manageOn) {
			button.css("background", "");
			button.css("box-shadow", "");
			manageOn = false;
			clearValids();
			$("#manage_button_bar").fadeOut(100);
		}
	}
});

$("#manage_sell").bind('click', function() {
	if(!manageDisabled) {
		buildOffSellOn();
	}
});

$("#manage_build").bind('click', function() {
	if(!manageDisabled) {
		buildOnSellOff();
	}
});


function validBuilds() {
	$('table#monopolies_table tr').each(function(){
	  $(this).children('td:empty').each(function () {
	  	var td = $(this);
	  	var prev = td.prev();
	  	if (prev.text().trim() == 'H') {
	  		td.css('border', '1px dashed #000');
	  	}	  	
	  });
	  $(this).children('td').each(function () {
	  	var td = $(this);
	  	if (td.index() == 0 && td.text().trim() == "M") {
	  		td.css('border', '1px dashed #000');
	  	}	
	  });
	});

	$('table.unbuildablePropTable tr').each(function(){
	  $(this).children('td').each(function () {
	  	var td = $(this);
	  	if (td.index() == 0 && td.text().trim() == "M") {
	  		td.css('border', '1px dashed #000');
	  	}	
	  });
	});
}

function validSells() {
	$('table#monopolies_table tr').each(function(){
	  $(this).children('td').each(function () {
	  	var td = $(this);
	  	var next = td.next();
	  	if (td.text().trim() == "H" && next.text().trim() == "") {
	  		td.css('border', '1px dashed #000');
	  	} else if (td.index() == 0 && td.text().trim() == "") {
	  		td.css('border', '1px dashed #000');
	  	}	  	
	  });
	});

	$('table.unbuildablePropTable tr').each(function(){
	  $(this).children('td').each(function () {
	  	var td = $(this);
	  	if (td.index() == 0 && td.text().trim() == "") {
	  		td.css('border', '1px dashed #000');
	  	}	  	
	  });
	});
}

function clearValids() {
	$('table#monopolies_table tr').each(function(){
	  $(this).children('td').each(function () {
	  	var td = $(this);
	  	td.css("border", "");  	
	  });
	});
}

function build() {
	alert("BUILD");
}

function mortgage() {
	alert("MORTGAGE");
}


$("#monopolies_table").delegate("td", "click", function() {
	var td = $(this);
	if (manageOn) {
		if (buildOn) {
		  	var prev = td.prev();
			if (td.index() == 0 && td.text().trim() == "M") {
				mortgage();
			} else if (td.text().trim() == "" && prev.text().trim() == 'H') {
			  	build();
			}
		} else {
			var next = td.next();
			if (td.index() == 0 && td.text().trim() == "") {
				mortgage();
			} else if (td.text().trim() == "H" && next.text().trim() == "") {
			  	build();
			}
		}
	}
  	
	// know which property it is ---> td.parent().index();
});

function buildOnSellOff() {
	clearValids();
	buildOn = true;
	validBuilds();
	var build = $("#manage_build");
	build.css("background", "rgba(209, 251, 228, 1)");
	build.css("box-shadow", "0px 0px 7px #D1FBE4");

	var sell = $("#manage_sell");
	sell.css("background", "");
	sell.css("box-shadow", "");
}

function buildOffSellOn() {
	clearValids();
	buildOn = false;
	validSells();
	var sell = $("#manage_sell");
	sell.css("background", "rgba(209, 251, 228, 1)");
	sell.css("box-shadow", "0px 0px 7px #D1FBE4");

	var build = $("#manage_build");
	build.css("background", "");
	build.css("box-shadow", "");
}




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
var pauseOn = false;

$("#popup").hide(0);

$("#pause_button").bind('click', function() {
	var button = $("#pause_button");
	button.css("background", "rgba(209, 251, 228, .7)");
	button.css("box-shadow", "0px 0px 7px #D1FBE4");
	$("#popup").fadeIn(200);
	$("#screen").css("opacity", ".2");
	pauseOn = true;
	$(".button").css("cursor", "default");
	$(".popup_button").css("cursor", "pointer");
	$("#paused_screen").show(0);
});

$("#popup_exit, #popup_resume").bind('click', function() {
	var button = $("#pause_button");
	$("#popup").fadeOut(200);
	$("#screen").css("opacity", "1");
	button.css("background", "");
	button.css("box-shadow", "");
	pauseOn = false;
	$(".button").css("cursor", "pointer");
	$("#paused_screen").hide(0);
});

$("#popup_quit").bind('click', function() {
	$("#paused_screen").hide(0);
	$("#game_settings").hide(0);
	$("#home_options").show(0);
	$("#home_screen").slideDown(500);
	$("#popup").hide(0);
});

$(document).keyup(function(e) {
    var ESC = 27;
	if (e.keyCode == ESC && pauseOn) {
		var button = $("#pause_button");
		$("#popup").fadeOut(200);
		enableAll();
		$("#screen").css("opacity", "1");
		button.css("background", "");
		button.css("box-shadow", "");
	}
});
