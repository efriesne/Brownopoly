createBoard();

function createBoard() {
	for (var i = 0; i < 40; i++) {
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
			document.getElementById("bottom").appendChild(square);
		} else if (i < 20) {
			square.style.webkitTransform = "rotate(90deg)";
			square.style.marginBottom = "-20px";
			document.getElementById("left").appendChild(square);
		} else if (i < 30) {
			document.getElementById("top").appendChild(square);
		} else {
			square.style.webkitTransform = "rotate(-90deg)";
						square.style.marginBottom = "-20px";

			document.getElementById("right").appendChild(square);
		}
		document.getElementById(id).appendChild(color);


		var name = document.createTextNode("Marley");
		var price = document.createTextNode("$400");

		var new_square = document.getElementById(id);
		
		new_square.appendChild(name);
		for (var c = 0; c < 4; c ++) {
			new_square.appendChild(document.createElement("br"));
		}

		new_square.appendChild(price);




	}

}