var deeds;

function loadDeeds() {
	$.post("/loadDeeds", function(responseJSON){
		var responseObject = JSON.parse(responseJSON);
		deeds = responseObject.deeds;
	});
}

document.addEventListener("keydown", helpCursor, false);
//document.addEventListener("keyup", endHelpCursor, false);

var infoPressed = false;

function helpCursor(e) {
	var keyCode = e.keyCode;
	if(keyCode == 91 && !purchasing) {
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
		$("#preview_button_bar").hide(0);
		$("#preview_prompt").hide(0);
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
				previewProperty(deed, "property_preview");
				break;
			case "railroad":
				previewRailroad(deed, "railroad_preview");
				break;
			case "utility":
				previewUtility(deed, "utility_preview");
				break;
			case "":
				break;
			default:
				break;
		}		
	}
});

function populateDeed(deed) {
	switch (deed.type) {
		case "property":
			populatePropertyDeed(deed, "property_preview");
			return $("#property_preview");
		case "railroad":
			populateRailroadDeed(deed, "railroad_preview");
			return $("#railroad_preview");
		case "utility":
			populateUtilityDeed(deed, "utility_preview");
			return $("#utility_preview");
		case "":
			break;
		default:
			break;
	}
}

// $("#preview_button_bar").hide(0);
// $("#preview_prompt").hide(0);

var purchasing = false;

function purchaseOwnable(boardIDX) {
	$("#popup_property_preview").hide(0);
	$("#popup_railroad_preview").hide(0);
	$("#popup_utility_preview").hide(0);

	var deed = deeds[boardIDX];
	purchasing = true;
	switch (deed.type) {
		case "property":
			previewProperty(deed, "popup_property_preview");
			break;
		case "railroad":
			previewRailroad(deed, "popup_railroad_preview");
			break;
		case "utility":
			previewUtility(deed, "popup_utility_preview");
			break;
		case "":
			break;
		default:
			break;
	}
}

$("#purchase_buy").on("click", function(){

});
$("#purchase_decline").on("click", function(){

});

// function previewProperty(deed, divID) {
// 	var div = document.getElementById(divID);
// 	var name = $(div).find("p.property_preview_name");
// 	name.html(deed.name);

// 	$(div).find("p.rent").html(deed.rentCosts[0]);
// 	var table = document.getElementById("property_preview_house_table");
// 	for (var i = 0; i < table.rows.length; i++) {
// 		var row = table.rows[i];
// 		var tds = $(row).children("td");
// 		if (i == 0) {
// 			$(tds[1]).html("$ " + deed.rentCosts[i+1]);
// 		} else {
// 			$(tds[1]).html(deed.rentCosts[i+1]);
// 		}
// 	}
// 	$(div).find("p.hotel").html(deed.rentCosts[table.rows.length]);
// 	$(div).find("p.mortgage_val").html(deed.mortgageValue);
// 	$(div).find("p.house_cost").html(deed.houseCost);
// 	$(div).find("p.hotel_cost").html(deed.houseCost);

// 	var curr_color = deed.color;
// 	if (curr_color != null) {
// 		var red = curr_color[0];
// 		var green = curr_color[1];
// 		var blue = curr_color[2];
// 		$(div).find("div.property_preview_color").css("background", "rgb("+ red + "," + green + "," + blue + ")");

// 		var hsl = rgbToHsl(red, green, blue);

// 		if (hsl[2] < .5) {
// 			$(div).find("div.property_preview_color").css("color", "#FFF");
// 		} else {
// 			$(div).find("div.property_preview_color").css("color", "#000");
// 		}
// 	}

// 	$(div).fadeIn(100);
// 	if (!purchasing) {
// 		$("#middle").css("opacity", ".3");
// 	}
// }

function previewProperty(deed, divID) {
	populatePropertyDeed(deed, divID);	
	var div = document.getElementById(divID);
	$(div).fadeIn(100);
	if (!purchasing) {
		$("#middle").css("opacity", ".3");
	}
}

function populatePropertyDeed(deed, divID) {
	var div = document.getElementById(divID);
	$(div).html("");

	/* set up the color header */
	var color = document.createElement("div");
	color.className = "property_preview_color";

	var curr_color = deed.color;
	if (curr_color != null) {
		var red = curr_color[0];
		var green = curr_color[1];
		var blue = curr_color[2];
		$(color).css("background", "rgb("+ red + "," + green + "," + blue + ")");

		var hsl = rgbToHsl(red, green, blue);

		if (hsl[2] < .5) {
			$(color).css("color", "#FFF");
		} else {
			$(color).css("color", "#000");
		}
	}

	div.appendChild(color);

	var titleDeed = document.createElement("p");
	titleDeed.className = "property_title_deed";
	$(titleDeed).html("TITLE DEED");
	color.appendChild(titleDeed);

	var name = document.createElement("p");
	name.className = "property_preview_name";
	$(name).html(deed.name);
	color.appendChild(name);


	/* set up body */
	div.appendChild(document.createTextNode("RENT $"));
	var rent = document.createElement("p");
	rent.className = "rent inline";
	$(rent).html(deed.rentCosts[0]);
	div.appendChild(rent);
	div.appendChild(document.createElement("br"));

	var table = document.createElement("table");
	table.className = "property_preview_house_table preview_table";

	for (var i = 1; i < deed.rentCosts.length - 1; i++) {
		var row = table.insertRow(i-1);
		var td0 = row.insertCell(0);
		var td1 = row.insertCell(1);
		if (i == 1) {
			$(td0).html("With 1 House")
			$(td1).html("$ " + deed.rentCosts[i]);
		} else {
			$(td0).html("With " + i + " Houses")
			$(td1).html(deed.rentCosts[i]);
		}
	}

	div.appendChild(table);

	div.appendChild(document.createTextNode("With HOTEL $"));
	var hotel = document.createElement("p");
	hotel.className = "hotel inline";
	$(hotel).html(deed.rentCosts[deed.rentCosts.length - 1]);
	div.appendChild(hotel);
	div.appendChild(document.createElement("br"));

	div.appendChild(document.createTextNode("Mortgage Value $"));
	var m = document.createElement("p");
	m.className = "mortgage_val inline";
	$(m).html(deed.mortgageValue);
	div.appendChild(m);
	div.appendChild(document.createElement("br"));

	div.appendChild(document.createTextNode("Houses cost $"));
	var houses = document.createElement("p");
	houses.className = "house_cost inline";
	$(houses).html(deed.houseCost);
	div.appendChild(houses);
	div.appendChild(document.createTextNode(" each"));
	div.appendChild(document.createElement("br"));

	div.appendChild(document.createTextNode("Hotels, $"));
	var hotels = document.createElement("p");
	hotels.className = "hotel_cost inline";
	$(houses).html(deed.houseCost);
	div.appendChild(houses);
	div.appendChild(document.createTextNode(" plus " + deed.rentCosts.length + " houses"));

	var disclaimer = document.createElement("p");
	disclaimer.className = "property_preview_disclaimer";
	$(disclaimer).html("If a player owns ALL the Lots of any Color-Group, " +
		"the rent is doubled on Unimproved Lots in that group.");
	div.appendChild(disclaimer);
}

// function previewProperty(deed) {
// 	$("#property_preview_name").html(deed.name);
// 	$("#rent").html(deed.rentCosts[0]);
// 	var table = document.getElementById("property_preview_house_table");
// 	for (var i = 0; i < table.rows.length; i++) {
// 		var row = table.rows[i];
// 		var tds = $(row).children("td");
// 		if (i == 0) {
// 			$(tds[1]).html("$ " + deed.rentCosts[i+1]);
// 		} else {
// 			$(tds[1]).html(deed.rentCosts[i+1]);
// 		}
// 	}
// 	$("#hotel").html(deed.rentCosts[table.rows.length]);
// 	$("#mortgage_val").html(deed.mortgageValue);
// 	$("#house_cost").html(deed.houseCost);
// 	$("#hotel_cost").html(deed.houseCost);

// 	var curr_color = deed.color;
// 	if (curr_color != null) {
// 		var red = curr_color[0];
// 		var green = curr_color[1];
// 		var blue = curr_color[2];
// 		$("#property_preview_color").css("background", "rgb("+ red + "," + green + "," + blue + ")");

// 		var hsl = rgbToHsl(red, green, blue);

// 		if (hsl[2] < .5) {
// 			$("#property_preview_color").css("color", "#FFF");
// 		} else {
// 			$("#property_preview_color").css("color", "#000");
// 		}
// 	}

// 	$("#property_preview").fadeIn(100);
// 	$("#middle").css("opacity", ".3");
// }

function previewRailroad(deed, divID) {
	populateRailroadDeed(deed, divID);
	var div = document.getElementById(divID);
	$(div).fadeIn(100);
	if (!purchasing) {
		$("#middle").css("opacity", ".3");
	}
}

function populateRailroadDeed(deed, divID) {
	var div = document.getElementById(divID);
	var name = $(div).find("p.railroad_preview_name");
	name.html(deed.name);
}

// function previewRailroad(deed) {
// 	$("#railroad_preview_name").html(deed.name);
// 	$("#railroad_preview").fadeIn(100);
// 	$("#middle").css("opacity", ".3");
// }

function previewUtility(deed, divID) {
	populateUtilityDeed(deed, divID);
	var div = document.getElementById(divID);
	$(div).fadeIn(100);
	if (!purchasing) {
		$("#middle").css("opacity", ".3");
	}
}

function populateUtilityDeed(deed, divID) {
	var div = document.getElementById(divID);
	var img = $(div).find("img.utility_preview_logo");
	if (deed.boardIDX == 12) {
		img.attr("src", "/images/electric_co_clear.png");
	} else {
		img.attr("src", "/images/waterworks_2.png");
	}
	var name = $(div).find("p.utility_preview_name");
	name.html(deed.name);
}
// function previewUtility(deed) {
// 	if (deed.boardIDX == 12) {
// 		$("#utility_preview_logo").attr("src", "/images/electric_co_clear.png");
// 	} else {
// 		$("#utility_preview_logo").attr("src", "/images/waterworks_2.png");
// 	}

// 	$("#utility_preview_name").html(deed.name);

// 	$("#utility_preview").fadeIn(100);
// 	$("#middle").css("opacity", ".3");
// }

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







