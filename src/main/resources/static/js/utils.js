/***********
CUSTOM POPUP
************/

function customizePopup(appearanceObject, handlerObject) {
	/*
	appearanceObject: 
		{
			titleText: text to appear in the title
			message: the message to be displayed on the popup
			showNoButton: boolean whether or not to show the no button
			noText: text to place on the "no" button
			okText: text to place on the "ok" button
		}	
	handlerObject: 
		{
			noHandler: function to attach to "no" button
			okHandler: function to attach to "ok" button
			noHandlerData: data object for noHandler
			okHandlerData: data object for okHandler
		}

	HOW TO USE:
		pass in handlerObject and appearanceObject with the mappings you wish to customize
		data passed into as noHandlerData/okHandlerData can be accessed using event.data (where event is the parameter in the handler)
		any mappings not given will provide default settings for that variable (see defaults below)
		ex. not giving appearanceObject a titleText key-value mapping will cause the title of the popup to be set to "UH OH..."
		calling customizePopup() with no args will reset the popup to its default settings
		handlers do not need to deal with hiding the popup, or restoring it to default values, as this will be done 
			in addition to the passed in handlers automatically
	*/
	//make sure handlerObject and appearanceObject are defined, if not make them empty objects
	handlerObject = replaceIfUndefined(handlerObject, {});
	appearanceObject = replaceIfUndefined(appearanceObject, {});
	//initialize variables
	var titleText = replaceIfUndefined(appearanceObject.titleText, "UH OH...");
	var message = replaceIfUndefined(appearanceObject.message, "");
	var showNoButton = replaceIfUndefined(appearanceObject.showNoButton, true);
	var noText = replaceIfUndefined(appearanceObject.noText, "No");
	var okText = replaceIfUndefined(appearanceObject.okText, "Okay");
	var noHandler = replaceIfUndefined(handlerObject.noHandler, defaultHandler);
	var okHandler = replaceIfUndefined(handlerObject.okHandler, defaultHandler);
	var noHandlerData = replaceIfUndefined(handlerObject.noHandlerData, {});
	var okHandlerData = replaceIfUndefined(handlerObject.okHandlerData, {});

	var popup = $("#popup_error");
	//reset title 
	popup.children("h2").text(titleText);
	//reset message
	popup.find("p").text(message);
	//hide the no button if necessary
	if (showNoButton) {
		popup.find("#error_no").show(0);
	} else {
		popup.find("#error_no").hide(0);
		//need to make sure this keeps the other button centered
	}
	//replace ok button with its stuff (text, handler)
	var ok = $("#error_okay");
	ok.text(okText);
	//clear ok of previously bound functions
	ok.off();	
	//bind functions to ok
	ok.on('click', okHandlerData, function(event) {
		//if the okHandler is the defaultHandler, don't call it again
		if (!areSameFunction(okHandler, defaultHandler)) {
			//hide the popup, restore its defaults
			defaultHandler();
		}
		okHandler(event);
	});
	//replace no button with its stuff if we're showing it
	if (showNoButton) {
		var no = $("#error_no");
		no.text(noText);
		//clear no of previously bound functions
		no.off();
		//bind functions to no
		no.on('click', noHandlerData, function(event) {
			//if the noHandler is the defaultHandler, don't call it again
			if (!areSameFunction(noHandler, defaultHandler)) {
				//hide the popup, restore its defaults
				defaultHandler();
			}
			noHandler(event);
		});
	}
}

function customizeAndShowPopup(appearanceObject, handlerObject) {
	customizePopup(appearanceObject, handlerObject);
	$("#popup_error").show(0);
	$("#paused_screen").show(0);
}

function replaceIfUndefined(toCheck, toReplace) {
	return toCheck == undefined ? toReplace : toCheck;
}

function defaultHandler() {
	$("#popup_error").hide(0);
	$("#paused_screen").hide(0);
	//reset popup to its default settings
	customizePopup();
}

function areSameFunction(f, g) {
	return f.toString() == g.toString();
}

