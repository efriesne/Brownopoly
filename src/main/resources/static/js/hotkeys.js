var R_KEY = 82;
var M_KEY = 77;
var T_KEY = 84;
var P_KEY = 80;
var H_KEY = 72;


$(document).keyup(function(e) {
	var key = e.keyCode;
	if (key == ESC ) {
		if (helpOn) {
			closeHelp();
		} else if (pauseOn) {
			resumeRestore();
		} else if (manageOn) {
			$("#manage_cancel").trigger('click');
		}
	} else if (key == R_KEY) {
		$("#roll_button").trigger("click");
	} else if (key == M_KEY) {
		$("#manage_button").trigger("click");
	} else if (key == T_KEY) {
		$("#trade_button").trigger("click");
	} else if (key == P_KEY) {
		$("#pause_button").trigger("click");
	} else if (key == H_KEY) {
		if (pauseOn) {
			$("#popup_help").trigger("click");
		}
	}
});