// $.post("/test", function(responseJSON){
// 	var responseObject = JSON.parse(responseJSON);
// 	var board = responseObject.board;
// 	var players = responseObject.state.players;
// 	//players is in correct turn order
// 	// createBoard(board);
// 	setupPlayerPanel(players);
// 	for (var i = num_players; i < 6; i++) {
// 		var playerID = "#player_" + i;
// 		$(playerID).hide(0);
// 	}
// 	currPlayer = players[0];

// 	$("#screen").show(0);
// 	$("#home_screen").slideUp(500);

// 	//setTimeout(function() {startTurn(); }, 600);
// });

$("#property_preview").hide(0);
$("#railroad_preview").hide(0);
$("#utility_preview").hide(0);

var deeds;

function loadDeeds() {
	$.post("/loadDeeds", function(responseJSON){
		var responseObject = JSON.parse(responseJSON);
		deeds = responseObject.deeds;
	});
}

document.addEventListener("keydown", helpCursor, false);
document.addEventListener("keyup", endHelpCursor, false);

var infoPressed = false;

function helpCursor(e) {
	var keyCode = e.keyCode;
	if(keyCode == 91) {
		infoPressed = true;
		$(".infoable").css("cursor", "help");
	}
}

function endHelpCursor(e) {
	var keyCode = e.keyCode;
	if(keyCode == 91) {
		$(".infoable").css("cursor", "default");
		infoPressed = false;
		$("#property_preview").hide(0);
		$("#railroad_preview").hide(0);
		$("#utility_preview").hide(0);
		$("#middle").css("opacity", "1");
	}
}

$("#screen").on("click", "div.infoable, tr.infoable", function() {
	if (infoPressed) {
		$("#property_preview").hide(0);
		$("#railroad_preview").hide(0);
		$("#utility_preview").hide(0);
		var boardIDX = $(this).data().id;
		var deed = deeds[boardIDX];
		switch (deed.type) {
			case "property":
				previewProperty(deed);
				break;
			case "railroad":
				previewRailroad(deed);
				break;
			case "utility":
				previewUtility(deed);
				break;
			case "":
				break;
			default:
				break;
		}		
	}
});

function previewProperty(deed) {
	$("#property_preview_name").html(deed.name);
	$("#rent").html(deed.rentCosts[0]);
	var table = document.getElementById("property_preview_house_table");
	for (var i = 0; i < table.rows.length; i++) {
		var row = table.rows[i];
		var tds = $(row).children("td");
		if (i == 0) {
			$(tds[1]).html("$ " + deed.rentCosts[i+1]);
		} else {
			$(tds[1]).html(deed.rentCosts[i+1]);
		}
	}
	$("#hotel").html(deed.rentCosts[table.rows.length]);
	$("#mortgage_val").html(deed.mortgageValue);
	$("#house_cost").html(deed.houseCost);
	$("#hotel_cost").html(deed.houseCost);

	var curr_color = deed.color;
	if (curr_color != null) {
		var red = curr_color[0];
		var green = curr_color[1];
		var blue = curr_color[2];
		$("#property_preview_color").css("background", "rgb("+ red + "," + green + "," + blue + ")");

		var hsl = rgbToHsl(red, green, blue);

		if (hsl[2] < .5) {
			$("#property_preview_color").css("color", "#FFF");
		} else {
			$("#property_preview_color").css("color", "#000");
		}

	}

	$("#property_preview").fadeIn(100);
	$("#middle").css("opacity", ".3");
}

function previewRailroad(deed) {
	$("#railroad_preview_name").html(deed.name);
	$("#railroad_preview").fadeIn(100);
	$("#middle").css("opacity", ".3");
}

function previewUtility(deed) {
	if (deed.boardIDX == 12) {
		$("#utility_preview_logo").attr("src", "/images/electric_co_clear.png");
	} else {
		$("#utility_preview_logo").attr("src", "/images/waterworks_2.png");
	}

	$("#utility_preview_name").html(deed.name);

	$("#utility_preview").fadeIn(100);
	$("#middle").css("opacity", ".3");
}

/* CITATION: http://stackoverflow.com/questions/2353211/hsl-to-rgb-color-conversion */
/**
 * Converts an RGB color value to HSL. Conversion formula
 * adapted from http://en.wikipedia.org/wiki/HSL_color_space.
 * Assumes r, g, and b are contained in the set [0, 255] and
 * returns h, s, and l in the set [0, 1].
 *
 * @param   Number  r       The red color value
 * @param   Number  g       The green color value
 * @param   Number  b       The blue color value
 * @return  Array           The HSL representation
 */
function rgbToHsl(r, g, b){
    r /= 255, g /= 255, b /= 255;
    var max = Math.max(r, g, b), min = Math.min(r, g, b);
    var h, s, l = (max + min) / 2;

    if(max == min){
        h = s = 0; // achromatic
    }else{
        var d = max - min;
        s = l > 0.5 ? d / (2 - max - min) : d / (max + min);
        switch(max){
            case r: h = (g - b) / d + (g < b ? 6 : 0); break;
            case g: h = (b - r) / d + 2; break;
            case b: h = (r - g) / d + 4; break;
        }
        h /= 6;
    }

    return [h, s, l];
}







