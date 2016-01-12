package pokerbots.player;

import java.util.ArrayList;

public class Board {
	public ArrayList<ArrayList<Card>> board = new ArrayList<ArrayList<Card>>();
	public ArrayList<Card> hearts = new ArrayList<Card>();
	public ArrayList<Card> spades = new ArrayList<Card>();
	public ArrayList<Card> clubs = new ArrayList<Card>();
	public ArrayList<Card> diamonds = new ArrayList<Card>();
	
	public float confidence;

	public Board(){
		//Put empty array for each possible value of a card
		//0 will be left as an empty list always
		for(int i=0; i<15; i++){
			ArrayList<Card> temp = new ArrayList<Card>();
			board.add(temp);
		}
		confidence = 0;
	}
	
	public void addCard(Card new_card){
		//Organize cards by numbers
		board.get(new_card.value).add(new_card);
		if(new_card.value==14){
			board.get(1).add(new_card);
		}
		//Organize cards by suits
		if(new_card.suit=="h"){
			hearts.add(new_card);
		}
		else if(new_card.suit=="s"){
			spades.add(new_card);
		}
		else if(new_card.suit=="c"){
			clubs.add(new_card);
		}
		else if(new_card.suit=="d"){
			diamonds.add(new_card);
		}
	}
	
	public float getConfidenceFromBoard(){
		int in_a_row = 0;
		float temp = 0;
		int max_straight = 0;
		//Data From Board variable
		for(int i=0; i<board.size(); i++){
			int of_a_kind = board.get(i).size();
			if(of_a_kind>1){
				//More of a kind are better
				temp+=(of_a_kind*10)+i;
			}
			if(i>12){
				//Kings and Aces are good
				for(int j=0; j<board.get(i).size(); j++){
					temp+=5;
				}
			}
			if(of_a_kind>0){in_a_row++;}
			else{in_a_row = 0;}
			if(in_a_row > max_straight){max_straight = in_a_row;}
		}
		temp+=max_straight*6;
		
		//Data from Suit variables
		int num_hearts = hearts.size();
		int num_spades = spades.size();
		int num_diamonds = spades.size();
		int num_clubs = spades.size();
		
		int max_flush = Math.max(num_hearts, Math.max(num_spades, Math.max(num_diamonds, num_clubs)));
		temp += Math.pow(4, max_flush);
		confidence = temp;
		return confidence;
	}
}
