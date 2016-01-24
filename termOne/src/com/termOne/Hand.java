package com.termOne;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


public class Hand {
	private ArrayList<Card> cardList;

	public Hand(Card[] cards){
		cardList = new ArrayList<Card>();
		for(Card card : cards)
			cardList.add(card);
		Collections.sort(cardList);
	}

	public Hand(String handString) {
		cardList = new ArrayList<Card>();
		for(int i = 0; i < handString.length()-1; i+= 2) {
			cardList.add(new Card(handString.charAt(i), handString.charAt(i+1)));
		}
		Collections.sort(cardList);

	}

	/**
	 * Returns a list of 2-card permutations of a 4-card hand
	 * @return an array of Strings ready to be passed to the hand eval
	 * library. All entries in the returned array have 2 card hands to be
	 * able to be parsed by the libraries and are followed by ":xxx"
	 * denoting the random hand.
	 */
	public String[] permute2CardHands() {
		String RANDOM_HAND = ":xxx";
		String[] ans = new String[6];
		ans[0] = cardList.get(0).toString() + cardList.get(1).toString() + RANDOM_HAND;
		ans[1] = cardList.get(0).toString() + cardList.get(2).toString() + RANDOM_HAND;
		ans[2] = cardList.get(0).toString() + cardList.get(3).toString() + RANDOM_HAND;
		ans[3] = cardList.get(1).toString() + cardList.get(2).toString() + RANDOM_HAND;
		ans[4] = cardList.get(1).toString() + cardList.get(3).toString() + RANDOM_HAND;
		ans[5] = cardList.get(2).toString() + cardList.get(3).toString() + RANDOM_HAND;
		return ans;
	}

	public String toString(){
		StringBuilder ans = new StringBuilder();
		for(Card c : cardList){
			ans.append(c.toString());
		}
		return ans.toString();
	}

	public static int get_num_suit(String hand, char c){
		int temp = 0;
		for(char i : hand.toCharArray()){
			if(i==c){temp++;}
		}
		return temp;
	}

	/**
	 * Operates on the invariant that for any sorted hand, 
	 * if the 1st card is not a club, there are no clubs 
	 * in the hand, if the 2nd card is not a diamond there 
	 * are no diamonds, etc. This allows in-place char
	 * replacements (i.e. without swapping 
	 * a->temp, b->a, temp->b). All hands are sorted in their
	 * constructors
	 * 
	 * @return a hand string suited to match the database's 
	 * hand-saving hash of clubs->diamonds->hearts->spades
	 */
	@SuppressWarnings("serial")
	public String toDatabaseString() {
		System.out.println("toDatabaseString() input: " + this.toString());
		ArrayList<Character> suits = new ArrayList<Character>() {{add('c'); add('d'); add('h'); add('s');}};
		String ans = new String(this.toString());
		int counter = 0;
		ArrayList<Card> seen = new ArrayList<Card>();

		Card max =getMaxValueCard();
		seen.add(max);
		ans = ans.replace(max.getSuit().charAt(0), ' ');
		ans = ans.replace(suits.get(counter), max.getSuit().charAt(0));
		ans = ans.replace(' ', suits.get(counter));
		
		counter++;
		if(getMaxValueCard(seen) != null && getMaxValueCard(seen).getSuit().charAt(0) != max.getSuit().charAt(0)) {
			max = getMaxValueCard(seen);
			seen.add(max);
			while (suits.indexOf(max.getSuit().charAt(0)) < counter) {
				max = getMaxValueCard(seen);
				seen.add(max);
				if (max == null) {
					return ans;
				}
			}
			ans = ans.replace(max.getSuit().charAt(0), ' ');
			ans = ans.replace(suits.get(counter), max.getSuit().charAt(0));
			ans = ans.replace(' ', suits.get(counter));
			counter++;
		}
		else {
			max = getMaxValueCard(seen);
			seen.add(max);
			if (max == null) {
				return ans;
			}
		}

		if(getMaxValueCard(seen) != null && getMaxValueCard(seen).getSuit().charAt(0) != max.getSuit().charAt(0)) {
			max = getMaxValueCard(seen);
			seen.add(max);

			while (suits.indexOf(max.getSuit().charAt(0)) < counter) {
				max = getMaxValueCard(seen);
				seen.add(max);
				if (max == null) {
					return ans;
				}
			}
			ans = ans.replace(max.getSuit().charAt(0), ' ');
			ans = ans.replace(suits.get(counter), max.getSuit().charAt(0));
			ans = ans.replace(' ', suits.get(counter));
			counter++;
		}
		else {
			max = getMaxValueCard(seen);
			seen.add(max);
			if (max == null) {
				return ans;
			}
		}

		ans = new Hand(ans).toString();
		System.out.println("Intermediary: " + ans);
		int suitTracker = 1;
		while(suitTracker < numDifferentSuits()) {
			boolean temp = true;
			for(int i = 1; i < ans.length(); i+= 2) {
				if(ans.charAt(i) == suits.get(suitTracker)) {
					temp = false;
				}
			}
			if(temp) {
				int last_index = -1;
				for(int j = 1; j < ans.length(); j+=2){
					if(ans.charAt(j)==suits.get(suitTracker-1)){last_index = j;}
				}
				try {
					ans = ans.replace(ans.charAt(last_index+2), suits.get(suitTracker));
				} catch(StringIndexOutOfBoundsException e) {
					e.printStackTrace();
				}
			}
			suitTracker++;
		}
		ans = new Hand(ans).toString();
		System.out.println("Intermediary2: " + ans);
		
		int max_flush = 0;
		char max_suit =' ';
		for(char c : suits){
			if(max_flush<get_num_suit(ans, c)){
				max_flush = Math.max(max_flush, c);
				max_suit = c;
			}
		}
		ans = ans.replace(max_suit, ' ');
		ans = ans.replace('c', max_suit);
		ans = ans.replace(' ', 'c');

		ans = new Hand(ans).toString();
		System.out.println("Returning: " + ans);
		return ans;
	}

	private int numDifferentSuits() {
		Set<Character> suits = new HashSet<Character>();
		for(Card c : this.cardList) {
			suits.add(c.getSuit().charAt(0));
		}
		return suits.size();
	}

	/**
	 * Returns the Card with the highest value less than or equal to leq_card
	 * @param leq_card a Card to set the maximum value of this method's result
	 * @return a Card in the hand (excluding leq_card) with a value less 
	 * than or equal to leq_card's value. Returns null if there are no cards in
	 * the hand with a value less than or equal to leq_card.
	 */
	private Card getMaxValueCard(ArrayList<Card> exclusion) {
		System.out.println("Exclusion: "+exclusion);
		int min = 15;
		for(Card c : exclusion) {
			if(c.getValue() < min) 
				min = c.getValue();
		}

		Card card = new Card('0', 's');
		for(Card c : this.cardList) {
			if(c.getValue() > card.getValue()) {
				if(exclusion.size() > 0 && c.getValue() <= min && !exclusion.contains(c))
					card = c;
				else if(exclusion.size() == 0)
					card = c;
			}
		}
		if(card.equals(new Card('0', 's'))) {
//			System.out.println("0s");
			card = null;
		}
		return card;
	}

	/**
	 * @return the Card in the hand with the highest value
	 */
	private Card getMaxValueCard() {
		return getMaxValueCard(new ArrayList<Card>());
	}
}
