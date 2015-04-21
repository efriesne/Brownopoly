// createBoard();

function loadPlayers() {
	//use data-player_id = <name>
}

function createBoard(backEndBoard) {
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
			square.className = "card";
			var id = "sq_" + i;
			square.id = id;
			
			var color = document.createElement("div");
			color.className = "color";
			
			var curr_color = board.colors[i];
			if (curr_color != null) {
				var red = curr_color[0];
				var green = curr_color[1];
				var blue = curr_color[2];
				color.style.background = "rgb("+ red + "," + green + "," + blue + ")";
			}

			var preID = "sq_" + (i-1);
			var preSQ = document.getElementById(preID);

			if (i < 10) {
				if (i == 0) {
					document.getElementById("bottom").appendChild(square);
				} else {
					document.getElementById("bottom").insertBefore(square, preSQ);
				}
			} else if (i < 20) {
				square.style.webkitTransform = "rotate(90deg)";
				square.style.marginBottom = "-22px";
				square.style.marginLeft = "11px";
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
				square.style.marginBottom = "-22px";
				square.style.marginLeft = "11px";
				document.getElementById("right").appendChild(square);
			}

			if (curr_color != null) {
				document.getElementById(id).appendChild(color);
			}

			var name = document.createTextNode(board.names[i]);
			var price = document.createTextNode("$" + board.prices[i]);

			var new_square = document.getElementById(id);
			
			new_square.appendChild(document.createElement("br"));
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
			} else if (i == 4) {
				new_square.appendChild(document.createElement("br"));
				new_square.appendChild(document.createElement("br"));
				new_square.appendChild(document.createTextNode("❖"));
				new_square.appendChild(document.createElement("br"));
				new_square.appendChild(document.createElement("br"));
				new_square.appendChild(document.createElement("br"));
				new_square.appendChild(document.createTextNode("Pay $200"));

			} else if (i == 38) {
				var img = document.createElement("img")
				img.setAttribute("src", "/images/tax.png");
				img.setAttribute("width", "30px");
				img.setAttribute("height", "30px");
				new_square.appendChild(img);
			} else if (i == 12) {
				var img = document.createElement("img")
				img.setAttribute("src", "/images/electric_co.png");
				img.setAttribute("width", "30px");
				img.setAttribute("height", "30px");
				new_square.appendChild(img);
				new_square.appendChild(price);
			} else if (i == 28) {
				var img = document.createElement("img")
				img.setAttribute("src", "/images/waterworks.png");
				img.setAttribute("width", "30px");
				img.setAttribute("height", "23px");
				new_square.appendChild(img);
				new_square.appendChild(document.createElement("br"));
				new_square.appendChild(document.createElement("br"));
				new_square.appendChild(price);
			} else if (board.prices[i] != -1 ) {
				for (var c = 0; c < 3; c ++) {
					new_square.appendChild(document.createElement("br"));
				}
				new_square.appendChild(price);
			}
		}
	}
}


$("#paused_screen").hide(0);
$("#paused_screen").bind("click", function () { 
	return false;
});




