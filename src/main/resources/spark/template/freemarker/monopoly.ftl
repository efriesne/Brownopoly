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
        
        <img id="player_0" class="piece" 
        	data-imgurlpath="/images/dog_piece.png" 
        	data-color="#D66F8E" 
        	src="/images/dog_piece.png">
        
        <img id="player_1" class="piece" 
        	data-imgurlpath="/images/car_piece.png" 
        	data-color="orange"
        	src="/images/car_piece.png">
        
        <img id="player_2" class="piece" 
        	data-imgurlpath="/images/thimble_piece.png" 
        	data-color="yellow"
        	src="/images/thimble_piece.png">
        
        <img id="player_3" class="piece" 
        	data-imgurlpath="/images/ship_piece.png" 
        	data-color="#9F57C9"
        	src="/images/ship_piece.png">
        
        <img id="player_4" class="piece" 
        	data-imgurlpath="/images/hat_piece.png" 
        	data-color="#57BCC9"
        	src="/images/hat_piece.png">
        
        <img id="player_5" class="piece" 
        	data-imgurlpath="/images/wheelbarrow_piece.png" 
        	data-color="#64C957"
        	src="/images/wheelbarrow_piece.png">
	</div>

	<div id="player_panel"> 
		<div id="player_tab_panel">
			<!-- <div id="emma" class="player_tab">E</div>
			<div class="player_tab">M</div>
			<div class="player_tab">C</div>
			<div class="player_tab">N</div> -->
	<!-- 		<div class="player_tab">E</div>
			<div class="player_tab">M</div>
			<div class="player_tab">C</div>
			<div class="player_tab">N</div> -->
		</div>
		<div id="player_panel_header"> 
			<h3 id="player_panel_current_name"> Emma </h3>
			<center><p id="player_wealth" style="color: green"> Cash: $300 </p></center>
		</div>
		<div id="player_panel_body">
			
			<!-- <br> -->
			<strong> Monopolies </strong> <br>
			<!-- <center> -->
				<table id="monopolies_table" class="player_table">
				<tr>
					<td title="Mortgage"> M </td>
					<td><div class="mtable_noOF"> Oriental Avenue </div></td>
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
			<!-- </center> -->
			<strong> Other Properties </strong> <br>
			<!-- <center> -->
				<table id="oProperties" class="player_table unbuildablePropTable">
					<tr><td title="Mortgage"> M </td>
					<td> <div class="ptable_noOF"> Oriental LAKJSDKASJDLAKSJDLAKSJDLAKJDLJKSDLJAS Avenue </div> </td> </tr>
					<tr><td title="Mortgage"> M </td>
					<td> Oriental Avenue </td> </tr>
					<tr><td title="Mortgage"> M </td>
					<td> Oriental Avenue </td> </tr>
					<tr><td title="Mortgage"> M </td>
					<td> Oriental Avenue </td> </tr>
					<tr><td title="Mortgage"> M </td>
					<td> Oriental Avenue </td> </tr>
					<tr><td title="Mortgage"> M </td>
					<td> Oriental Avenue </td> </tr>
					<tr><td title="Mortgage"> M </td>
					<td> Oriental Avenue </td> </tr>
				</table>
			<!-- </center> -->
			<strong> Railroads </strong><br>
			<center>
				<table id="railroads" class="player_table unbuildablePropTable"></table>
			</center>
			<strong> Utilities </strong>
			<center>
				<table id="utilities" class="player_table unbuildablePropTable"></table>
			</center>
		
			<br>
			<!-- <div id="manage_button_bar"> 
				<div id="manage_build" class="manage_button button">Build</div>
				<div id="manage_sell" class="manage_button button">Sell</div>
				<div id="manage_save" class="manage_button button">Save Changes</div>
			</div> -->

		</div>
	</div>

	<div id="button_bar">
		<div id="roll_button" class="button_bar_button button" title="Roll"></div>
		<div id="manage_button" class="button_bar_button button" title="Manage Properties"></div>
		<div id="trade_button" class="button_bar_button button" title="Trade"></div>
		<div id="pause_button" class="button_bar_button button" title="Pause"></div>
	</div>

	<div id="manage_button_bar"> 
		<div id="manage_build" class="manage_button button">Build</div>
		<div id="manage_sell" class="manage_button button">Sell</div>
		<div id="manage_save" class="manage_button button">Save</div>
	</div>

	<div id="newsfeed_title">
		<textarea id="newsfeed">
		</textarea>
	</div>

	
</div>

<div id="popup">
	<h2> PAUSED </h2>
	<div id="popup_resume" class="popup_button">Resume</div>
	<div id="popup_help" class="popup_button">Help</div>
	<div id="popup_save" class="popup_button">Save</div>
	<div id="popup_quit" class="popup_button">Quit</div>
</div>

<div id="home_screen" class="home">
	<div id="monopoly_logo" class="home"> </div>
	<div id="home_options">
		<div id="home_newgame" class="home_button">New Game</div>
		<div id="home_customize" class="home_button">Customize Board</div>
		<div id="home_load" class="home_button">Load Game</div>
	</div>
	<div id="game_settings"> 
		<strong> Please create between 2 and 6 players:</strong> <br><br>
		<table id="player_creation_table">
		<tr>
			<td></td>
			<td>Player: </td> 
			<td><input type="text" id="player_name_0"></td> 
			<td><input type="radio" name="player_type_0" value="human" checked="checked"> Human </td>
			<td><input type="radio" name="player_type_0" value="ai"> AI </td>
		</tr>
		<tr>
			<td></td>
			<td>Player: </td> 
			<td><input type="text" id="player_name_1"></td> 
			<td><input type="radio" name="player_type_1" value="human" checked="checked"> Human </td>
			<td><input type="radio" name="player_type_1" value="ai"> AI </td>
		</tr>
		</table>


		<div id="add_player_button" class="game_settings_button">Add Player</div>

		<strong> Select a game play: </strong> <br><br>
		<center>
			<input type="radio" name="game_play" value="normal" checked="checked"> Normal &nbsp;&nbsp;&nbsp;<input type="radio" name="game_play" value="fast"> Fast Play 
		</center>

		<div id="bottom_bar">
			<div id="customize_board_button" class="game_settings_button">Customize Board</div>
			<div id="play_button" class="game_settings_button">Done</div> 
		</div>

	</div>
</div>

<div id="paused_screen"> </div>

</#assign>
<#include "main.ftl">