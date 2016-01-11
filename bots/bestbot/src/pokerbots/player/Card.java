package pokerbots.player;

public class Card {
	public String rank, suit;
	public int value;
	
	public Card(String r, String s){
		rank = r;
		suit = s;
		getCardValue();
	}
	private void getCardValue(){
		int val = Integer.parseInt(rank);
		value = val;
	}
}
