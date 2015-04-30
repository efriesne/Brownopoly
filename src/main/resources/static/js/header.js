/**********
HOME SCREEN
***********/
MAX_PLAYERS = 6;

var num_players = 2;
var fastPlay;
/**********
BUTTON BAR
***********/
// key code for escape key
ESC = 27;
BUTTON_SHADOW = "0px 0px 7px #D1FBE4";
// color for a selected button on the button bar (excluding build/sell buttons)
SELECTED = "rgba(209, 251, 228, .7)";

// enable or disable the user from clicking on certain buttons at certain times
var manageDisabled = false;
var tradeDisabled = false;
var rollDisabled = false;

var pauseOn = false;

// keeps track of when the user is managing properties
var manageOn = false;
// keeps track of whether the user is building houses and demortgaging or selling houses and mortgaging
var buildOn = false;

// keeps track of the mortgage/house transactions a user wishes to submit
var mortgages = {};
var houseTransactions = {};

// for trading
var gameState;

/**********
 TURN
**********/

// variables used during a player's turn
var currPlayer;
var prevPosition;
var prevPlayer;
var players;
var bankruptcyOn = false;
var numPlayers;

var secondMove = false;
var outOfJail = false;

var newsfeed = $("#newsfeed");


function resetVariables() {
	num_players = 2;
	manageDisabled = false;
	tradeDisabled = false;
	rollDisabled = false;
	pauseOn = false;
	manageOn = false;
	buildOn = false;
	mortgages = {};
	houseTransactions = {};
	gameState = undefined;
	currPlayer = undefined;
	prevPosition = 0;
	prevPlayer = undefined;
	players = undefined;
	secondMove = false;
	outOfJail = false;
	newsfeed.html("");
}
