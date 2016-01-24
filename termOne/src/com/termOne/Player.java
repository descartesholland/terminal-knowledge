package com.termOne;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;

/**
 * Simple example pokerbot, written in Java.
 * 
 * This is an example of a bare bones, pokerbot. It only sets up the socket
 * necessary to connect with the engine and then always returns the same action.
 * It is meant as an example of how a pokerbot should communicate with the
 * engine.
 * 
 */
public class Player {

	private final PrintWriter outStream;
	private final BufferedReader inStream;

	private boolean button;

	private Hand hand;
	private int myBank,otherBank;
	
	private float confidence;
	private float maxConfidence;
	
	private DatabaseConnector dbc;
	
	public Player(PrintWriter output, BufferedReader input) {
		this.outStream = output;
		this.inStream = input;
		dbc = new DatabaseConnector(false);
		
	}

	/**
	 * 
	 * @param raise "Raise:_____"
	 * @return List of all the different raise moves possible
	 */
	private ArrayList<Integer> getRaisesFromString(String raise){
		ArrayList<Integer> signals = new ArrayList<Integer>();
		int index = raise.indexOf(':');
		while (index >= 0) {
			signals.add(index);
		    index = raise.indexOf(':', index + 1);
		}
		return signals;
	}
	
	public void run() {
		String input;
		try {
			// Block until engine sends us a packet; read it into input.
			while ((input = inStream.readLine()) != null) {

				// Here is where you should implement code to parse the packets
				// from the engine and act on it.
				System.out.println("INPUT:"+input);

				String[] words = input.split(" ");
				Board myBoard = new Board();

				if ("NEWHAND".compareToIgnoreCase(words[0])==0) {
					Card[] _hand = new Card[4];
					button = Boolean.getBoolean(words[2]);
					for(int i=0;i<4;i++){
						Card hand_card = new Card(words[3+i]);
						_hand[i] = hand_card;
					}
					hand = new Hand(_hand);
					myBank = Integer.parseInt(words[7]);
					otherBank = Integer.parseInt(words[8]);
				}
				else if ("GETACTION".compareToIgnoreCase(words[0]) == 0) {
					// When appropriate, reply to the engine with a legal action.
					// The engine will ignore all spurious packets you send.
					// The engine will also check/fold for you if you return an illegal action.
					int pot_size = Integer.parseInt(words[1]);
					int num_board_cards = Integer.parseInt(words[2]);
					
					//Get the cards on the board, if there are any
					int index = 3;
					if(num_board_cards!=0){
						for(int i=0;i<num_board_cards;i++){
							myBoard.addCard(new Card(words[index+i]));
						}
						index += num_board_cards;
					}
					
					//Throw away last actions (can change this later)
					//Skips over the interactions in this game
					index += Integer.parseInt(words[index])+1;
					
					
					ArrayList<String> actions = new ArrayList<String>();
					if(index<words.length){
						//Get possible actions
						
						int num_actions = Integer.parseInt(words[index]);
						index++;
						for(int i=0; i<num_actions; i++){
							String action = words[index+i];							
							if(action.contains("RAISE:")){
								String temp = "RAISE:";
								//Break up the different raise amounts
								for(int j=6; j<action.length(); j++){
									if(action.charAt(j)==':'){
										//end of number
										actions.add(temp);
										temp = "RAISE:";
									}
									else{
										temp+=action.charAt(j);
									}
								}
								actions.add(temp);
							}
							else if(action.contains("BET:")){
								String temp = "BET:";
								//Break up the different raise amounts
								for(int j=4; j<action.length(); j++){
									if(action.charAt(j)==':'){
										//end of number
										actions.add(temp);
										temp = "BET:";
									}
									else{
										temp+=action.charAt(j);
									}
								}
								actions.add(temp);
							}
							else{
								actions.add(action);
							}
						}
					}
					boolean check = false;
					boolean call = false;
					boolean fold = false;
					ArrayList<String> bet = new ArrayList<String>();
					ArrayList<String> raise = new ArrayList<String>();
					
					for(String a:actions){
						String x = ""+a.charAt(0);
						if(x.equalsIgnoreCase("F")){
							fold = true;
						}
						else if(x.equalsIgnoreCase("C")){
							String y = ""+a.charAt(1);
							if(y.equalsIgnoreCase("A")){
								call = true;
							}
							else{check = true;}
						}
						else if(x.equalsIgnoreCase("B")){
							bet.add(a);
						}
						else if(x.equalsIgnoreCase("R")){
							raise.add(a);
						}
					}
					
					String out = "";
					double random = Math.random();
					System.out.println(bet.toString()+" "+raise.toString()+" "+new Boolean(check).toString()+" "+new Boolean(fold).toString());
					//If very confident: biggest raise
					confidence = dbc.lookup(dbc.tableName, "HAND_ID", hand.toDatabaseString());
					if(num_board_cards > 0) {
						for(String s : hand.permute2CardHands()){
							confidence = Math.max(confidence, myBoard.getConfidenceFromBoard( s.toLowerCase(), myBoard.toString()));
						}
					}

					System.out.println("Confidence: "+confidence);
					if(confidence>.6){
						if(!raise.isEmpty()){
							out = raise.get(raise.size()-1);
						}
						else if(!bet.isEmpty()){
							out = bet.get(bet.size()-1);
						}
						else if(call){out = "CALL";}
						else if(check){out = "CHECK";}
						else{out = "FOLD";}
					}
					//If semi-confident: second biggest raise/call/check
					
					else if(confidence>.55){
						if(!raise.isEmpty()){
							out = raise.get(0);
						}
						else if(!bet.isEmpty()){
							out = bet.get(0);
						}
						else if(call){out = "CALL";}
						else if(check){out = "CHECK";}
						else if(fold){out = "FOLD";}
					}
					//If not confident : play 57% of hands OOP
					else if(!button && random>.56){
						if(check){out = "CHECK";}
						else if(call){out = "CALL";}
						else if(fold){out = "FOLD";}
					}
					//random from optional moves
					else{
						Random r = new Random();
						int result = r.nextInt(actions.size());
						out = actions.get(result);
					}
					outStream.println(out);
				}
					
				else if ("HANDOVER".compareToIgnoreCase(words[0])==0){
					//Create new board
					myBoard = new Board();
					confidence = 0;
				}
				else if ("REQUESTKEYVALUES".compareToIgnoreCase(words[0]) == 0) {
					// At the end, engine will allow bot to send key/value pairs to store.
					// FINISH indicates no more to store.
					outStream.println("FINISH");
				}
			}
		} catch (IOException e) {
			System.out.println("IOException: " + e.getMessage());
		}

		System.out.println("Gameover, engine disconnected");

		// Once the server disconnects from us, close our streams and sockets.
		try {
			outStream.close();
			inStream.close();
		} catch (IOException e) {
			System.out.println("Encounterd problem shutting down connections");
			e.printStackTrace();
		}
	}
}
