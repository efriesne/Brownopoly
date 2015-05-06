/**********
HOME SCREEN
***********/
MAX_PLAYERS = 6;

var num_players = 2;
var fastPlay;

var housesForHotel = 4;

/**********
BUTTON BAR
***********/

// variable to track whether loading games or themes
var loadingGames;

// enable or disable the user from clicking on certain buttons at certain times
var manageDisabled = true;
var tradeDisabled = true;
var rollDisabled = true;
var pauseDisabled = true;

var rolling = false;

var pauseOn = false;

var helpOn = false;

// keeps track of when the user is managing properties
var manageOn = false;
// keeps track of whether the user is building houses, demortgaging, selling houses, or mortgaging
var buildOn = false;
var mortgageOn = false;

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
var playerBankruptcyCount;
var bankruptcyOn = false;

var secondMove = false;
var outOfJail = false;

var newsfeed = $("#newsfeed");

/*********
CUSTOMIZE
**********/

var defaultColors;
var defaultNames;

var customColors;
var customNames;

/********
POPUP
*********/

var popupUp = false;
var prevPopupStack = [];

/*****************/

function resetVariables() {
	num_players = 2;
	housesForHotel = 4;
	manageDisabled = true;
	tradeDisabled = true;
	rollDisabled = true;
	pauseDisabled = true;
	rolling = false;
	pauseOn = false;
	helpOn = false;
	manageOn = false;
	buildOn = false;
	mortgageOn = false;
	mortgages = {};
	houseTransactions = {};
	gameState = undefined;
	currPlayer = undefined;
	prevPosition = 0;
	prevPlayer = undefined;
	players = undefined;
	playerBankruptcyCount = 0;
	secondMove = false;
	outOfJail = false;
	popupUp = false;
	prevPopupStack = [];
	customColors = defaultColors.slice(0);
	customNames = defaultNames.slice(0);
	assembleCustomization();
	newsfeed.html("");
	disableAll();
}
