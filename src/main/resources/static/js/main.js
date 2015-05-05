function createBoard(backEndBoard) {
	clearBoard();
	var board = backEndBoard;

	for (var i = 0; i < 40; i++) {
		if (i == 0 || i == 10 || i == 20 || i == 30) {
			var corner = document.createElement("div");
			corner.className = "corner";
			var id = "sq_" + i;
			corner.id = id;

			if (i == 0) {
				document.getElementById("bottom").appendChild(corner);
			} else if (i == 10) {
				var preID = "sq_" + (i-1);
				var preSQ = document.getElementById(preID);
				corner.style.webkitTransform = "rotate(90deg)";
				document.getElementById("bottom").insertBefore(corner, preSQ);
			} else if (i == 20) {
				corner.style.webkitTransform = "rotate(180deg)";
				document.getElementById("top").appendChild(corner);
			} else {
				corner.style.webkitTransform = "rotate(-90deg)";
				document.getElementById("top").appendChild(corner);
			}
		}  
		else {
			var square = document.createElement("div");
			square.className = "property";
			var id = "sq_" + i;
			square.id = id;
			$(square).data("id", i);
			$(square).data("name", board.names[i]);

			var color = document.createElement("div");
			color.className = "color";
			
			var curr_color = board.colors[i];
			if (curr_color.length > 0) {
				var red = curr_color[0];
				var green = curr_color[1];
				var blue = curr_color[2];
				color.style.background = "rgb("+ red + "," + green + "," + blue + ")";
			}

			var preID = "sq_" + (i-1);
			var preSQ = document.getElementById(preID);

			if (i < 10) {
				square.style.webkitTransform = "rotate(0deg)";
				if (i == 0) {
					document.getElementById("bottom").appendChild(square);
				} else {
					document.getElementById("bottom").insertBefore(square, preSQ);
				}
			} else if (i < 20) {
				square.style.webkitTransform = "rotate(90deg)";
				square.style.marginBottom = "-25px";
				square.style.marginLeft = "12.5px";
				if (i == 11) {
					document.getElementById("left").appendChild(square);
				} else {
					document.getElementById("left").insertBefore(square, preSQ);
				}
			} else if (i < 30) {
				square.style.webkitTransform = "rotate(180deg)";
				document.getElementById("top").appendChild(square);
			} else {
				square.style.webkitTransform = "rotate(-90deg)";
				square.style.marginBottom = "-25px";
				square.style.marginLeft = "12.5px";
				document.getElementById("right").appendChild(square);
			}

			if (curr_color.length > 0) {
				document.getElementById(id).appendChild(color);
			}

			var n = document.createTextNode(board.names[i]);
			var name = document.createElement("p");
			name.appendChild(n);
			name.className = "name";
			var p = document.createTextNode("$" + board.prices[i]);
			var price = document.createElement("p");
			price.appendChild(p);
			price.className = "price";

			var new_square = document.getElementById(id);
			
			new_square.appendChild(name);
			

			if (board.names[i] == "COMMUNITY CHEST") {
				var img = document.createElement("img")
				img.setAttribute("src", "/images/community_chest.png");
				img.setAttribute("width", "35px");
				img.setAttribute("height", "35px");
				new_square.appendChild(img);
			} else if (board.names[i] == "CHANCE") {
				var img = document.createElement("img")
				img.setAttribute("src", "/images/chance_pink.png");
				img.setAttribute("width", "20px");
				img.setAttribute("height", "40px");
				new_square.appendChild(img);
			} else if (i == 5 || i == 15 || i == 25 || i == 35) {
				var img = document.createElement("img")
				img.setAttribute("src", "/images/railroad.png");
				img.setAttribute("width", "30px");
				img.setAttribute("height", "30px");
				new_square.appendChild(img);
				new_square.appendChild(price);
				new_square.className = "property infoable";
			} else if (i == 4) {
				new_square.appendChild(document.createElement("br"));
				new_square.appendChild(document.createElement("br"));
				new_square.appendChild(document.createTextNode("❖"));
				var p = document.createTextNode("Pay $200");
				var price = document.createElement("p");
				price.appendChild(p);
				price.className = "price";
				new_square.appendChild(price);
			} else if (i == 38) {
				var img = document.createElement("img")
				img.setAttribute("src", "/images/tax.png");
				img.setAttribute("width", "30px");
				img.setAttribute("height", "30px");
				img.setAttribute("style", "margin-top: 5px");
				new_square.appendChild(img);
				var p = document.createTextNode("Pay $75");
				var price = document.createElement("p");
				price.appendChild(p);
				price.className = "price";
				new_square.appendChild(price);
			} else if (i == 12) {
				var img = document.createElement("img")
				img.setAttribute("src", "/images/electric_co.png");
				img.setAttribute("width", "30px");
				img.setAttribute("height", "30px");
				new_square.appendChild(img);
				new_square.appendChild(price);
				new_square.className = "property infoable";
			} else if (i == 28) {
				var img = document.createElement("img")
				img.setAttribute("src", "/images/waterworks.png");
				img.setAttribute("width", "30px");
				img.setAttribute("height", "23px");
				new_square.appendChild(img);
				new_square.appendChild(price);
				new_square.className = "property infoable";
			} else if (board.prices[i] != -1 ) {
				new_square.appendChild(price);
				new_square.className = "property infoable";
			}
		}
	}
	resetIcons();
}

function addHouse(boardIDX) {
	var img = document.createElement("img")
	img.setAttribute("src", "/images/built_house3.png");
	img.setAttribute("width", "10px");
	img.setAttribute("height", "10px");
	var house = document.createElement("div");
	house.className = "buildBox";
	house.appendChild(img);
	var sq = document.getElementById("sq_" + boardIDX);
	var houses = $(sq).data().houses;
	$(sq).data("houses", houses + 1);
	var color = $(sq).find("div.color");
	color.append(house);
}

function addHotel(boardIDX) {
	var img = document.createElement("img")
	img.setAttribute("src", "/images/built_hotel3.png");
	img.setAttribute("width", "10px");
	img.setAttribute("height", "10px");
	var house = document.createElement("div");
	house.className = "buildBox";
	house.appendChild(img);
	var sq = document.getElementById("sq_" + boardIDX);
	var houses = $(sq).data().houses;
	$(sq).data("houses", houses + 1);
	var color = $(sq).find("div.color");
	color.html("");
	color.append(house);
}

function removeBuilding(boardIDX) {
	var sq = document.getElementById("sq_" + boardIDX);
	var houses = $(sq).data().houses;
	$(sq).data("houses", houses - 1);
	var color = $(sq).find("div.color");
	var buildings = $(sq).find("div.buildBox");
	var building = buildings[buildings.length-1];
	building.remove();
}

function clearHouses() {
	$(".property").find("div.color").html("");
	$(".property").data("houses", 0);
}

function resetIcons() {
	$("#player_0").css("bottom", 39);
	$("#player_1").css("bottom", 39);
	$("#player_2").css("bottom", 24);
	$("#player_3").css("bottom", 24);
	$("#player_4").css("bottom", 9);
	$("#player_5").css("bottom", 9);

	$("#player_0").css("left", 557);
	$("#player_1").css("left", 587);
	$("#player_2").css("left", 557);
	$("#player_3").css("left", 587);
	$("#player_4").css("left", 557);
	$("#player_5").css("left", 587);
}

$("#paused_screen").on("click", function () { });

function clearBoard() {
	$("#top").html("");
	$("#left").html("");
	$("#right").html("");
	$("#bottom").html("");
}



