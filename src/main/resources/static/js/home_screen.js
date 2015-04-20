$("#game_settings").hide(0);


$("#home_newgame").bind('click', function() {
	$("#home_options").fadeOut(100);
	$("#game_settings").delay(100).fadeIn(200);
});


$("#play_button").bind('click', function() {
	$("#home_screen").slideUp(500);
});