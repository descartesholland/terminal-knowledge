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
		try{
			int val = Integer.parseInt(rank);
			value = val;
		}
		catch(Error e){
			if(suit == "T"){
				value = 10;
			}
			else if(suit == "J"){
				value = 11;
			}
			else if(suit == "Q"){
				value = 12;
			}
			else if(suit == "K"){
				value = 13;
			}
			else if(suit == "A"){
				value = 14;
			}
		}
	}
}
