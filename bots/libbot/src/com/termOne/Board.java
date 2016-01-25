package com.termOne;

import java.util.ArrayList;

import org.bridj.Pointer;

import pbots_calc.Pbots_calcLibrary;
import pbots_calc.Pbots_calcResults;
import pbots_calc.Results;

public class Board {
	public ArrayList<ArrayList<Card>> board = new ArrayList<ArrayList<Card>>();
	public ArrayList<Card> hearts = new ArrayList<Card>();
	public ArrayList<Card> spades = new ArrayList<Card>();
	public ArrayList<Card> clubs = new ArrayList<Card>();
	public ArrayList<Card> diamonds = new ArrayList<Card>();
	ArrayList<String> cardList = new ArrayList<String>();

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
		board.get(new_card.getValue()).add(new_card);
		if(new_card.getValue()==14){
			board.get(1).add(new_card);
		}
		//Organize cards by suits
		if(new_card.getSuitChar()=='h')
			hearts.add(new_card);
		else if(new_card.getSuitChar()=='s')
			spades.add(new_card);
		else if(new_card.getSuitChar()=='c')
			clubs.add(new_card);
		else if(new_card.getSuitChar()=='d')
			diamonds.add(new_card);
		cardList.add(new_card.toString());
	}

	public float getConfidenceFromBoardOld(){
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
	public float getConfidenceFromBoard(String... input) {
		double ans = 0;
		String board = "";
		String dead = "";
		if (input.length >= 2) {
			board = input[1];
			if (input.length >= 3) {
				dead = input[2];
			}
		}

		Results r = calc(input[0], board, dead, 2000);
		ans = r.getEv().get(0);
		return (float) ans;
	}

	public String getCards() {
		String ans = "";
		for(ArrayList<Card> cardList : this.board) {
			for(Card c : cardList) {
				ans+= c.toString();
			}
		}
		return ans;
	}

	public Results calc(String hands, String board, String dead,
			int iters) {
		Pointer<Pbots_calcResults> res = Pbots_calcLibrary.alloc_results();
		Results results = null;
		String cardListString = "";
		for(String cardString : cardList)
			cardListString+= cardString;
		if (Pbots_calcLibrary.calc(Pointer.pointerToCString(hands),
				Pointer.pointerToCString(cardListString/*board*/),
				Pointer.pointerToCString(dead), iters, res) > 0) {
			results = new Results(res.get());
		}
		Pbots_calcLibrary.free_results(res);
		return results;
	}

	public String toString(){
		StringBuilder ans = new StringBuilder();
		for(String s : this.cardList){
			ans.append(s);
		}
		return ans.toString();
	}
}
