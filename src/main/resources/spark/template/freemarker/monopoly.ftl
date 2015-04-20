<#assign content>



<h1> MONOPOLY </h1>

<div id="screen">
	<div id="board"> 
		<div id="top" class="row"></div>
		<div id="left" class="col"></div>
		<div id="middle">
			<div id="community_chest" class="deck"></div>
			<div id="chance" class="deck"></div>
		</div>
		<div id="right" class="col"></div>
		<div id="bottom" class="row"></div>
        <img id="player_1" class="piece" src="/images/dog_piece.png">
        <img id="player_2" class="piece" src="/images/car_piece.png">
        <img id="player_3" class="piece" src="/images/thimble_piece.png">
        <img id="player_4" class="piece" src="/images/ship_piece.png">
        <img id="player_5" class="piece" src="/images/hat_piece.png">
        <img id="player_6" class="piece" src="/images/wheelbarrow_piece.png">
	</div>

	<div id="player_panel"> 
		<div id="player_tab_panel">
			<div id="emma" class="player_tab">E</div>
			<div class="player_tab">M</div>
			<div class="player_tab">C</div>
			<div class="player_tab">N</div>
	<!-- 		<div class="player_tab">E</div>
			<div class="player_tab">M</div>
			<div class="player_tab">C</div>
			<div class="player_tab">N</div> -->
		</div>
		
		<h3> Emma </h3>
		<!-- <br> -->
		<strong> Properties </strong>
		<center>
			<table id="properties_table" class="player_table">
			<tr>
				<td title="Mortgage"> M </td>
				<td> Oriental Avenue </td>
				<td></td>
				<td></td>
				<td></td>
				<td></td>
				<td></td>
				
			</tr>
			<tr>
				<td></td>
				<td> Vermont Avenue </td> 
				<td> H </td>
				<td></td>
				<td></td>
				<td></td> 
				<td></td>
				
			</tr>
			<tr>
				<td></td> 
				<td> CIT Avenue </td>
				<td> H </td>
				<td> H </td>
				<td> H </td>
				<td> H </td>
				<td> H </td> 
				
			</tr>
			</table>
		</center>
		<br>
		<strong> Railroads </strong>
		<br>
		<strong> Utilities </strong>
	
		</center>

		<div id="manage_button_bar"> 
			<div id="manage_build" class="manage_button" class="button">Build</div>
			<div id="manage_sell" class="manage_button" class="button">Sell</div>
			<div id="manage_save" class="manage_button" class="button">Save Changes</div>
		</div>


	</div>

	<div id="button_bar">
		<div id="roll_button" class="button_bar_button" class="button" title="Roll"></div>
		<div id="manage_button" class="button_bar_button" class="button" title="Manage Properties"></div>
		<div id="trade_button" class="button_bar_button" class="button" title="Trade"></div>
		<div id="pause_button" class="button_bar_button" class="button" title="Pause"></div>
	</div>
</div>

<div id="popup">
	<h2> PAUSED </h2>
	<div id="popup_resume" class="popup_button" class="button">Resume</div>
	<div id="popup_help" class="popup_button" class="button">Help</div>
	<div id="popup_save" class="popup_button" class="button">Save</div>
	<div id="popup_quit" class="popup_button" class="button">Quit</div>
</div>

<div id="home_screen" class="home">
	<div id="monopoly_logo" class="home"> </div>
	<div id="home_options">
		<div id="home_newgame" class="home_button">New Game</div>
		<div id="home_customize" class="home_button">Customize Board</div>
		<div id="home_load" class="home_button">Load Game</div>
		<div id="home_quit" class="home_button">Quit</div>
	</div>
	<div id="game_settings"> 
		<strong> Please create between 2 and 6 players:</strong> <br><br>
		<table id="player_creation_table">
		<tr>
			<td></td>
			<td>Player: </td> 
			<td><input type="text" id="player_name_1"></td> 
			<td><input type="radio" name="player_type_1" value="human"> Human </td>
			<td><input type="radio" name="player_type_1" value="ai"> AI </td>
		</tr>
		<tr>
			<td></td>
			<td>Player: </td> 
			<td><input type="text" id="player_name_2"></td> 
			<td><input type="radio" name="player_type_2" value="human"> Human </td>
			<td><input type="radio" name="player_type_2" value="ai"> AI </td>
		</tr>
		</table>


		<div id="add_player_button" class="game_settings_button">Add Player</div>

		<strong> Select a game play: </strong> <br><br>
		<center>
			<input type="radio" name="game_play" value="normal"> Normal &nbsp;&nbsp;&nbsp;<input type="radio" name="game_play" value="fast"> Fast Play 
		</center>

		<div id="bottom_bar">
			<div id="customize_board_button" class="game_settings_button">Customize Board</div>
			<div id="play_button" class="game_settings_button">Done</div> 
		</div>

	</div>
</div>

</#assign>
<#include "main.ftl">