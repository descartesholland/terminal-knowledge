package com.termOne;

public class Card implements Comparable<Card> {
	public static final Card ACE_SUITLESS = new Card("A", "");
	private String rank, suit;
	private int value = -1;

	public Card(String rank, String suit){
		this.rank = rank;
		this.suit = suit.toLowerCase();
		value = getValue();
	}
	
	public Card(char rank, char suit) {
		this.rank = new String(new char[] {rank});
		this.suit = new String(new char[] {suit}).toLowerCase();
		value = getValue();
	}

	/**
	 * Forms a Card based on a two-character input String with rank and suit
	 * @param handString a two-character input String with rank as the first
	 * character and suit as the second
	 */
	public Card(String handString) {
		this.rank = String.valueOf(handString.charAt(0));
		this.suit = String.valueOf(handString.charAt(1)).toLowerCase();
	}

	public String toString(){
		return rank+suit;
	}
	

	//Values:
	// 2 = 2
	// 3 = 3 ...
	// K = 13
	// A = 14
	public int getValue(){
		if(value != -1)
			return value;
		try{
			int val = Integer.parseInt(rank);
			value = val;
		}
		catch(NumberFormatException e){
			if(rank.equalsIgnoreCase("T"))
				value = 10;
			else if(rank.equalsIgnoreCase("J"))
				value = 11;
			else if(rank.equalsIgnoreCase("Q"))
				value = 12;
			else if(rank.equalsIgnoreCase("K"))
				value = 13;
			else if(rank.equalsIgnoreCase("A"))
				value = 14;
		}
		return this.value;
	}
	
	public String getRank() {
		return this.rank;
	}
	
	public String getSuit() {
		return this.suit;
	}
	
	public char getSuitChar() {
		return this.suit.charAt(0);
	}

	
	/**
	 * Compares Cards based on their suit first (lexicographically) and 
	 * then by value in case of a tie. This ordering must be used to
	 * be compatible with the hand evaluation database
	 */
	@Override
	public int compareTo(Card c) {
			int suitComp = this.suit.compareToIgnoreCase(c.suit);
			if( suitComp != 0)
				return suitComp;
			else {
				return Integer.compare(new Integer(this.value), new Integer(c.value));
			}
	}
	
	@Override
	public boolean equals(Object another) {
		if(another instanceof Card) {
			Card _another = (Card) another;
			return this.getSuit().equals(_another.getSuit()) && this.getValue() == _another.getValue();
		}
		return false;
	}
}
