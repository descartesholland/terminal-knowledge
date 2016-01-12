package pokerbots.player;

import java.util.ArrayList;

public class Board {
	public ArrayList<ArrayList<Card>> board = new ArrayList<ArrayList<Card>>();
	public ArrayList<Card> hearts = new ArrayList<Card>();
	public ArrayList<Card> spades = new ArrayList<Card>();
	public ArrayList<Card> clubs = new ArrayList<Card>();
	public ArrayList<Card> diamonds = new ArrayList<Card>();

	public Board(){
		//Put empty array for each possible value of a card
		//0 will be left as an empty list always
		for(int i=0; i<15; i++){
			ArrayList<Card> temp = new ArrayList<Card>();
			board.add(temp);
		}
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
	
}
