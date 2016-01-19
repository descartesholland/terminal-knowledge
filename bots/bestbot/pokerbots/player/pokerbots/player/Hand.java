package pokerbots.player;

import java.util.LinkedHashMap;
import java.util.Map.Entry;


public class Hand {
	private LinkedHashMap<Float, Card> order_by_val = new LinkedHashMap<Float, Card>();
	public Hand(Card[] cards){
		for(Card card : cards){
			float i = (float) (Integer.parseInt(card.suit)*.001);
			order_by_val.put(card.value+i, card);
		}
	}
	public String toString(){
		StringBuilder ans = new StringBuilder();
		for(Entry<Float, Card> pair: order_by_val.entrySet()){
			ans.append(pair.getValue().toString());
		}
		return ans.toString();
	}
}
