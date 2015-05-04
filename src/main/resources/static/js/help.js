$(".help_tab").on("click", function(){
	var id = this.id;

	switch (id) {
		case "help_general":
			helpGeneral();
			break;
		case "help_doubles":
			helpDoubles();
			break;
		case "help_jail":
			helpJail();
			break;
		case "help_construction":
			helpConstruction();
			break;
		case "help_urRent":
			helpURRent();
			break;
		case "help_application":
			helpApplication();
			break;
		default:
			break;
	}
});

function helpGeneral() {
	var body = document.getElementById("help_body");
	$(body).html("");

	var h4 = document.createElement("h4");
	$(h4).html("General Rules");
	body.appendChild(h4);

	var p1 = document.createElement("p");
	$(p1).html("Each player, in turn, rolls both dice and then moves forward " +
		"(clockwise around the board) the number of spaces indicated by the sum" +
		"of the numbers rolled. Action is then taken depending on the space on which the player lands:");
	body.appendChild(p1);

	var list = document.createElement("ul");
	$(list).html("<li>If the player lands on an <b>unowned property</b>, "+
		"he or she may buy it for the price listed on that property's space. "+
		"If he or she <b>agrees</b> to buy it, he or she pays the <b>Bank</b> "+
		"the amount shown on the property space and receives the deed for that property. "+
		"There can only be one player per color. Railroads and utilities are also properties.</li>"+
		"<li>If the player lands on an unmortgaged property owned by another player, he or she pays rent to that person, as specified on the property's deed.</li>" +
		"<li>If the player lands on his or her own property, or on property which is owned by another player but currently mortgaged, nothing happens.</li>" +
		"<li>If the player lands on Luxury Tax, he or she must pay the Bank $75.</li>" +
		"<li>If the player lands on Income Tax he or she must pay the Bank either $200.</li>" +
		"<li>If the player lands a on Chance or Community Chest, the player takes a card from the top of the respective pack and performs the instruction given on the card.</li>" +
		"<li>If the player lands on the Jail space, he or she is 'Just Visiting' and does nothing. No penalty applies.</li>" +
		"<li>If the player lands on the Go to Jail square, he or she must move his token directly to Jail.</li>" +
		"<li>If the player lands on or passes Go in the course of his or her turn, he or she receives $200 from the bank.</li>");
	body.appendChild(list);

	var p2 = document.createElement("p");
	$(p2).html("Players may not loan money to other players, only the bank can loan money," +
	 " and then only by mortgaging properties.");
	body.appendChild(p2);
}


function helpDoubles() {
	var body = document.getElementById("help_body");
	$(body).html("");
	
	var h4 = document.createElement("h4");
	$(h4).html("Doubles");
	body.appendChild(h4);


	var p1 = document.createElement("p");
	$(p1).html("When doubles are rolled, the player moves again, " +
		"the official rules state that you do land on the square you landed on, on your first roll, and you must pay the price " +
		"or should you so choose, buy the property in question. After the third roll of doubles, rather than landing on the square " +
		"you would go to, go immediately to jail, and therefore, do not enact the appropriate action of landing on that square.");
	body.appendChild(p1);
}

function helpJail() {
	var body = document.getElementById("help_body");
	$(body).html("");

	var h4 = document.createElement("h4");
	$(h4).html("Jail");
	body.appendChild(h4);

	var p1 = document.createElement("p");
	$(p1).html("A player can be directed to jail in three ways: by landing on the Go to Jail space, by drawing a 'Go to Jail' Chance or Community Chest card or rolling three doubles. In any of these cases, the player moves directly into jail, without collecting the $200 for passing Go.");
	body.appendChild(p1);

	var p2 = document.createElement("p");
	$(p2).html("A player in jail remains there until he/she does one of the following:");
	body.appendChild(p2);

	var list = document.createElement("ul");
	$(list).html("<li>opts to pay a $50 bailout to the bank, or spend a 'Get out of Jail Free' card, before rolling the dice</li>"+
				"<li>rolls doubles.</li>"+
				"<li>fails to roll doubles on his/her third turn in jail, in which case the $50 charge is levied anyway (the player may not use a 'Get out of Jail Free' card in this situation)</li>");
	body.appendChild(list);

	var p3 = document.createElement("p");
	$(p3).html("In any of these cases, the dice rolled on the turn on which the player leaves jail are played out. However, in the second case, the power of the doubles is 'used up' by bailing the player out of jail, and so he/she does not get an extra turn. On the other hand, if the player rolls doubles in the first case, the extra turn is still granted.");
	body.appendChild(p3);	

	var p4 = document.createElement("p");
	$(p4).html("While in jail, a player may still buy and erect houses/hotels, sell or buy property, and collect rent.");
	body.appendChild(p4);
}

function helpConstruction() {
	var body = document.getElementById("help_body");
	$(body).html("");

	var h4 = document.createElement("h4");
	$(h4).html("Construction");
	body.appendChild(h4);

	var p1 = document.createElement("p");
	$(p1).html("Once a player owns all properties of a color group (a monopoly), the rent is now doubled on all unimproved lots of that color group, even if some of the properties are mortgaged to the Bank, and the player may purchase either one to four houses or one hotel (which is equivalent to five houses) for those properties (as long as none of the properties of that color group are mortgaged to the bank), which raise the rents that must be paid when other players land on the property.");
	body.appendChild(p1);
	
	var p2 = document.createElement("p");
	$(p2).html("The properties in a color group must be developed evenly, i.e. each house that is built must go on a property in the group with the fewest number of houses on it so far. In another way of speaking, the number of houses of any properties of a same color group must not differ by more than one. For example, houses in a group may be distributed (2,3,2) or (0,1,1) or (4,4,3), but not (1,2,3) or (0,4,4).");
	body.appendChild(p2);

	var p3 = document.createElement("p");
	$(p3).html("For a street repairs card, the player must pay for every house and/or hotel they own on the board.");
	body.appendChild(p3);
}


function helpURRent() {
	var body = document.getElementById("help_body");
	$(body).html("");

	var h4 = document.createElement("h4");
	$(h4).html("Railroad/Utility Rent");
	body.appendChild(h4);

	var p1 = document.createElement("p");
	$(p1).html("The rent a player owes for landing on a railroad varies with the number of railroads the owner possesses. "+
		"The rent is as follows: Charge $25 if one owned, $50 if two owned, $100 if three owned, $200 if all owned by the same owner. "+
		"If you get a chance card and it says advance to the nearest railroad, you may either buy it if unowned or if owned by the other player you must pay double what you would normally pay.");
	body.appendChild(p1);

	var p2 = document.createElement("p");
	$(p2).html("For utilities, after a player lands on one to owe rent, they must roll the dice again to determine the rent amount. The rent is 10 times the amount rolled if the both are owned, or 4 times if not.");
	body.appendChild(p2);
}


function helpApplication() {
	var body = document.getElementById("help_body");
	$(body).html("");

	var h4 = document.createElement("h4");
	$(h4).html("Using the Player Panel");
	body.appendChild(h4);

	var tabsIMG = document.createElement("img");
	$(tabsIMG).attr("src", "/images/help_playertabs.png");
	$(tabsIMG).css("width", "190px");
	$(tabsIMG).css("height", "79px");
	tabsIMG.className = "help_image";
	body.appendChild(tabsIMG);

	var p1 = document.createElement("p");
	$(p1).html("In the Player Panel, you can select which user's information you would like to see by clicking on a tab." +
		" The user that is selected will have their tab highlighted with their color, like the 'dog' is in the picture.");
	body.appendChild(p1);


	var p2 = document.createElement("p");
	$(p2).html("<b>When the user would like to manage their properties</b>, he or she should click the 'manage' button (house image in the button panel)" +
		"at the start of their turn. Once clicked, the user should see a scene like the image displayed below");
	body.appendChild(p2);

	var ppIMG = document.createElement("img");
	$(ppIMG).attr("src", "/images/help_manage.png");
	$(ppIMG).css("width", "308px");
	$(ppIMG).css("height", "178px");
	ppIMG.className = "help_image";
	body.appendChild(ppIMG);

	var p3 = document.createElement("p");
	$(p3).html("<b>(1)</b> The highlighted tab and name should reflect that of the user trying to manage their properties.");

	var p4 = document.createElement("p");
	$(p4).html("<b>(2)</b> Valid builds or sells (mortgages or unmortgages) are shown using dashed boxes. These are the valid " +
		"constructions that can be done based on the selected mode displayed to the right of the manage button (see (3)).");

	var p5 = document.createElement("p");
	$(p5).html("<b>(3)</b> Mode of managing can be selected by clicking the buttons to the right on the manage button. "+
		"'Build' allows for houses and hotels to be built. 'Sell' allows for houses and hotels to be sold. 'Mortgage' can mortgage only properties with no houses built. "+
		"'Unmortgage' allows for unmortgaging mortgaged properties. 'Cancel' closes the managing mode without saving. 'Save' commits changes made.");

	body.appendChild(p3);
	body.appendChild(p4);
	body.appendChild(p5);
}




