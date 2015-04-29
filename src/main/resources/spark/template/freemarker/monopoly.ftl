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

        <div id="property_preview" class="preview">
        	<div id="property_preview_color"> 
        		<p id="property_title_deed">TITLE DEED</p>
        		<p id="property_preview_name">marvin gardens</p>
        	</div>
        	RENT $<p id="rent" class="inline">24</p>
        	<br>
        	<center>
	        	<table id="property_preview_house_table" class="preview_table">
	        		<tr>
	        			<td>With 1 House</td> <td> $ 120</td>
	        		</tr>
	        		<tr>
	        			<td>With 2 Houses</td> <td> 360</td>
	        		</tr>
	        		<tr>
	        			<td>With 3 Houses</td> <td> 850</td>
	        		</tr>
	        		<tr>
	        			<td>With 4 Houses</td> <td> 1025</td>
	        		</tr>
	        	</table>
        	</center>
        	With HOTEL $<p id="hotel" class="inline">1200</p>. <br>
        	Mortgage Value $<p id="mortgage_val" class="inline">140</p>. <br>
        	Houses cost $<p id="house_cost" class="inline">150</p>. each <br>
        	Hotels, $<p id="hotel_cost" class="inline">150</p>. plus 4 houses <br>
        	<br><br>

        	<p id="property_preview_disclaimer"> If a player owns ALL the Lots of any Color-Group, the rent is doubled on Unimproved Lots in that group. </p>
        </div>

        <div id="railroad_preview" class="preview">
        	<img id="railroad_preview_logo" src="/images/railroad.png" class="preview_logo">
        	<div class="UR_preview_padding">
	        	<p id="railroad_preview_name" class="UR_preview_name"> Short Line </p>

	        	<center>
	        	<table id="railroad_preview_table" class="preview_table">
	        		<tr>
	        			<td>Rent</td><td>$25</td>
	        		</tr>
	        		<tr>
	        			<td>If 2 R.R.'s are owned</td><td>50</td>
	        		</tr>
	        		<tr>
	        			<td>If 3 &nbsp;&nbsp;&nbsp; " &nbsp;&nbsp; " &nbsp;&nbsp; " </td><td>100</td>
	        		</tr>
	        		<tr>
	        			<td>If 4 &nbsp;&nbsp;&nbsp; " &nbsp;&nbsp; " &nbsp;&nbsp; " </td><td>200</td>
	        		</tr>
	        		<tr><td> &nbsp;</td></tr>
	        		<tr>
	        			<td>Mortgage Value</td><td>100</td>
	        		</tr>
	        	</table>
	        	</center>
	        </div>
        </div>

        <div id="utility_preview" class="preview">
        	<img id="utility_preview_logo" src="/images/waterworks_2.png" class="preview_logo">
        	<div class="UR_preview_padding">
	        	<p id="utility_preview_name" class="UR_preview_name"> Water Works </p>
	        	<p class="indented"> If one "Utility" is owned, rent is 4 times amount shown on dice. </p>
	        	<p class="indented"> If both "Utilities" are owned, rent is 10 times the amount shown on the dice. </p>
	        	<center>
	        	<table id="utility_preview_table" class="preview_table">
	        		<tr>
	        			<td>Mortgage Value</td><td>$75</td>
	        		</tr>
	        	</table>
	        	</center>
	        </div>
        </div>
	</div>

	<div id="player_panel"> 
		<div id="player_tab_panel"></div>
		<div id="player_panel_header"> 
			<h3 id="player_panel_current_name"> Emma </h3>
			<center><p id="player_wealth" style="color: green"> Cash: $300 </p></center>
		</div>
		<div id="player_panel_body">
			<strong> Monopolies </strong> <br>
				<table id="monopolies_table" class="monopoly_table player_table"></table>
            <strong> Other Properties </strong> <br>
            	<table id="oProperties" class="player_table unbuildablePropTable"></table>
			<strong> Railroads </strong><br>
				<table id="railroads" class="player_table unbuildablePropTable"></table>
			<strong> Utilities </strong>
				<table id="utilities" class="player_table unbuildablePropTable"></table>
			<br>
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

	<div id="newsfeed_box">
		<h4> NEWSFEED </h4>
		<textarea id="newsfeed" readonly></textarea>
	</div>

	
</div>

<div id="popup_pause" class="popup">
	<h2> PAUSED </h2>
	<div id="popup_resume" class="popup_button">Resume</div>
	<div id="popup_help" class="popup_button">Help</div>
	<div id="save_button" class="popup_button">Save</div>
	<div id="popup_quit" class="popup_button">Quit</div>
</div>

<div id="popup_save" class="popup">
	<h2> SAVE </h2>
	<input type="text" id="save_filename">
	<div class="bottom_bar">
		<div id="save_cancel" class="game_settings_button">Cancel</div> 
		<div id="save_submit" class="game_settings_button">Done</div>
	</div>
</div>

<div id="popup_error" class="popup">
	<h2> UH OH... </h2>
	<div class="bottom_bar">
		<div id="error_no" class="game_settings_button">No</div> 
		<div id="error_okay" class="game_settings_button">Okay</div>
	</div>
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

		<div class="bottom_bar">
			<div id="customize_board_button" class="game_settings_button">Customize Board</div>
			<div id="play_button" class="game_settings_button">Done</div> 
		</div>
	</div>
	<div id="load_screen"> 
		<strong> Please select a game to load:</strong> <br><br>
		<div id="load_table_container">
			<table id="saved_games_table"></table>
		</div>

		<div class="bottom_bar">
			<div id="load_cancel" class="game_settings_button">Cancel</div> 
			<div id="load_game_button" class="game_settings_button">Done</div>
		</div>
	</div>
</div>

<div id="trade_center">
	<h2 id="trade_title"> TRADE </h2>
	<p style="margin-bottom: 10px; margin-top: 0px; text-align: center;"> Step 1: Select a trade recipient and propose a trade. </p>
	<div id="trade_recipient" class="trader_panel"> 
			<div class="trader_header">
			Trade with: &nbsp; <select id="select_recipient"></select> 
			</div>

			<div class="trader_panel_header"> 
				<h3 id="trader_panel_current_recipient"> Marley </h3>
				<center><p id="recip_player_wealth" style="color: green"> Cash: $300 </p></center>
			</div>

			<div id="trade_recip_body" class="trader_panel_body">
			<strong> Monopolies </strong> <br>
				<table id="trade_recip_monopolies" class="trade_monopoly_table trade_table"></table>
			<strong> Other Properties </strong> <br>
				<table id="trade_recip_oProperties" class="trade_table"></table>
				<strong> Railroads </strong><br>
					<table id="trade_recip_railroads" class="trade_table"></table>
				<strong> Utilities </strong>
					<table id="trade_recip_utilities" class="trade_table"></table>
				<br>
			</div>

		<div class="trade_money_footer"> Additional money:&nbsp; $ <input id="recipient_wealth_box" type="number" min="1" max="50000"> </div>

	</div> 
	
	<div id="trade_initiator" class="trader_panel"> 
		<div id="trade_init_header" class="trader_header"> 
			Trade initiated by: Marley
		</div>

		<div class="trader_panel_header"> 
			<h3 id="trader_panel_initiator"> Emma </h3>
			<center><p id="initiator_wealth" style="color: green"> Cash: $300 </p></center>
		</div>

		<div id="trade_init_body" class="trader_panel_body">
			<strong> Monopolies </strong> <br>
				<table id="trade_init_monopolies" class="trade_monopoly_table trade_table"></table>
			<strong> Other Properties </strong> <br>
				<table id="trade_init_oProperties" class="trade_table"></table>
			<strong> Railroads </strong><br>
				<table id="trade_init_railroads" class="trade_table"></table>
			<strong> Utilities </strong>
				<table id="trade_init_utilities" class="trade_table"></table>
			<br>
		</div>

		<div class="trade_money_footer"> Additional money:&nbsp; $ <input type="number" id="initiator_wealth_box" min="1" max="50000"> </div>
	</div> 

	<div id="trade_center_footer" class="trade_footer">
		<div id="trade_propose" class="trade_button"> Propose </div>
		<div id="trade_cancel" class="trade_button"> Cancel </div>
		<div id="trade_accept" class="trade_button"> Accept </div>
		<div id="trade_counter" class="trade_button"> Counter </div>
		<div id="trade_decline" class="trade_button"> Decline </div>
	</div>
</div>

<div id="paused_screen"> </div>

</#assign>
<#include "main.ftl">