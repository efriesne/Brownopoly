R_KEY = 82;
M_KEY = 77;
T_KEY = 84;
P_KEY = 80;
H_KEY = 72;
D_KEY = 68;
ESC = 27;
ENTER = 13;

$(document).off('keyup').on('keyup', function(e) {
	endHelpCursor(e);
	var key = e.keyCode;
	if (key == ESC ) {
		if (popupUp) {
			$("#popup_no").trigger('click');
		} else if (helpOn) {
			$("#help_close").trigger('click');
		} else if (pauseOn) {
			$("#resume_button").trigger('click');
		} else if (manageOn) {
			$("#manage_cancel").trigger('click');
		}
	} else if (key == ENTER) {
		$("#popup_okay").trigger('click');
	} else if (key == R_KEY) {
		$("#roll_button").trigger("click");
	} else if (key == M_KEY) {
		$("#manage_button").trigger("click");
	} else if (key == T_KEY) {
		$("#trade_button").trigger("click");
	} else if (key == P_KEY) {
		$("#pause_button").trigger("click");
	} else if (key == H_KEY) {
		$("#pause_help").trigger("click");
	} else if (key == D_KEY) {
		var selected = $("#saved_games_table tr.selected");
		if (selected.length) {
			var filename = selected.children().first().text();
			deleteData(filename, loadingGames);
		}
	} 
});

