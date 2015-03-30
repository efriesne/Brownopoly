package edu.brown.cs.cmen.brownopoly.player;

public interface Player {

	// returns position + increment
	public int move(int increment);

	// returns true if player can pay rent
	public boolean payRent(Property property);
	
	public void collectRent(int rent);

	// returns false if player does not have enough money to buy property
	public boolean buyProperty(Property property);

	// for mortgaging/trading
	public void removeProperty(Property property);
	
	public void receiveTrade(Property[] properties, int moneyToGet);

	public int getMoney();
	public List<Property> getProperties();
	public List<Monopoly> getMonopolies();
	public void addMonopoly();
	
	public void addJailFree();
	public boolean hasJailFree();
	public void useJailFree();

	public boolean isInJail();
	public int turnsInJail(); 

}
