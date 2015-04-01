<#assign content>

<h1> MONOPOLY </h1>

<div id="screen">
	<div id="board"> 
		<div id="top" class="row"></div>
		<div id="left" class="col"></div>
		<div id="middle"></div>
		<div id="right" class="col"></div>
		<div id="bottom" class="row"></div>
	</div>

	<div id="player_panel"> 
		<div id="player_tab_panel">
			<div id="emma" class="player_tab">E</div>
			<div class="player_tab">M</div>
			<div class="player_tab">C</div>
			<div class="player_tab">N</div>
			<div class="player_tab">E</div>
			<div class="player_tab">M</div>
			<div class="player_tab">C</div>
			<div class="player_tab">N</div>
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
		<!-- <br>
		Player: Emma
		<br><br>
		Properties:
		<br>
		Oriental Avenue <br>
		Vermont Avenue <br>
		CIT Avenue <br> -->

		<div id="manage_button_bar"> 
			<div id="manage_build" class="manage_button">Build</div>
			<div id="manage_sell" class="manage_button">Sell</div>
			<div id="manage_save" class="manage_button">Save Changes</div>
		</div>


	</div>

	<div id="button_bar">
		<div id="roll_button" class="button_bar_button" title="Roll"></div>
		<div id="manage_button" class="button_bar_button" title="Manage Properties"></div>
		<div id="trade_button" class="button_bar_button" title="Trade"></div>
		<div id="pause_button" class="button_bar_button" title="Pause"></div>
	</div>
</div>

<div id="popup">
	<div id="popup_exit">x</div>
	<h2> PAUSED </h2>
</div>

</#assign>
<#include "main.ftl">