$(".popup").hide(0);
$("#trade_center").hide(0);

$("#customize_screen").hide(0);
$("#game_settings").hide(0);
$("#screen").hide(0);
$("#load_screen").hide(0);

$("#property_preview").hide(0);
$("#railroad_preview").hide(0);
$("#utility_preview").hide(0);
$("#popup_property_preview").hide(0);
$("#popup_railroad_preview").hide(0);
$("#popup_utility_preview").hide(0);

$("#manage_button_bar").hide(0);
$("#paused_screen").hide(0);


//for testing

// $.post("/test", function(responseJSON){
// 	var responseObject = JSON.parse(responseJSON);
// 	var board = responseObject.board;
// 	var players = responseObject.state.players;
// 	//players is in correct turn order
// 	resetVariables();
// 	createBoard(board);
// 	setupPlayerPanel(players);
// 	housesForHotel = responseObject.state.numHousesForHotel;
// 	fastPlay = responseObject.state.fastPlay;
// 	num_players = players.length;
// 	loadDeeds();
// 	for (var i = num_players; i < MAX_PLAYERS; i++) {
// 		var playerID = "#player_" + i;
// 		$(playerID).hide(0);
// 	}
// 	$("#screen").show(0);
// 	$("#home_screen").slideUp(500, startTurn());
// });
