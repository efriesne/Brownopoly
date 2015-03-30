createBoard();

function createBoard() {
	for (var i = 0; i < 40; i++) {
		if (i == 0 || i == 10 || i == 20 || i == 30) {
			var corner = document.createElement("div");
			corner.className = "corner";
			var id = "sq_" + i;
			corner.id = id;

			if (i < 11) {
				document.getElementById("bottom").appendChild(corner);
			} else {
				document.getElementById("top").appendChild(corner);
			}
		}  
		else {
			var square = document.createElement("div");
			square.className = "card";
			var id = "sq_" + i;
			square.id = id;

			// if (i < 11) {
			// 	square.style.transform = "rotate(90deg)";
			// 	square.style.webkitTransform = "rotate(90deg)";
			// }
			
			var color = document.createElement("div");
			color.className = "color";
			color.style.background = "red";


			if (i < 10) {
				if (i != 1) {
					var preID = "sq_" + (i-1);
					var preSQ = document.getElementById(preID);
					document.getElementById("bottom").insertBefore(square, preSQ);
				} else {
					document.getElementById("bottom").appendChild(square);
				}
				
			} else if (i < 20) {
				square.style.webkitTransform = "rotate(90deg)";
				square.style.marginBottom = "-22px";
				square.style.marginLeft = "11px";
				if (i != 11) {
					var preID = "sq_" + (i-1);
					var preSQ = document.getElementById(preID);
					document.getElementById("left").insertBefore(square, preSQ);
				} else {
					document.getElementById("left").appendChild(square);
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
			document.getElementById(id).appendChild(color);


			var name = document.createTextNode(id);
			var price = document.createTextNode("$400");

			var new_square = document.getElementById(id);
			
			new_square.appendChild(name);
			for (var c = 0; c < 3; c ++) {
				new_square.appendChild(document.createElement("br"));
			}

			new_square.appendChild(price);
		}
	}
}