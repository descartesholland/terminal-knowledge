package pokerbots.player;

public class Card {
	public String rank, suit;
	public int value;
	
	public Card(String r, String s){
		rank = r;
		suit = s;
		getCardValue();
	}
	
	public String toString(){
		return rank+suit;
	}
	
	private void getCardValue(){
		try{
			int val = Integer.parseInt(rank);
			value = val;
		}
		catch(NumberFormatException e){
			if(rank == "T"){
				value = 10;
			}
			else if(rank == "J"){
				value = 11;
			}
			else if(rank == "Q"){
				value = 12;
			}
			else if(rank == "K"){
				value = 13;
			}
			else if(rank == "A"){
				value = 14;
			}
		}
	}
}
