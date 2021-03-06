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
	
	//Values:
	// 2 = 2
	// 3 = 3 ...
	// K = 13
	// A = 14
	private void getCardValue(){
		try{
			int val = Integer.parseInt(rank);
			value = val;
		}
		catch(NumberFormatException e){
			if(rank.equalsIgnoreCase("T")){
				value = 10;
			}
			else if(rank.equalsIgnoreCase("J")){
				value = 11;
			}
			else if(rank.equalsIgnoreCase("Q")){
				value = 12;
			}
			else if(rank.equalsIgnoreCase("K")){
				value = 13;
			}
			else if(rank.equalsIgnoreCase("A")){
				value = 14;
			}
		}
	}
}
