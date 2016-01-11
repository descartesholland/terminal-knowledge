package pokerbots.player;

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
	private Card[] hand = new Card[4];
	private int myBank,otherBank;

	public Player(PrintWriter output, BufferedReader input) {
		this.outStream = output;
		this.inStream = input;
	}

	private Card getCardFromString(String s){
		return new Card(String.valueOf(s.charAt(0)),String.valueOf(s.charAt(1)));
	}

	public void run() {
		String input;
		try {
			// Block until engine sends us a packet; read it into input.
			while ((input = inStream.readLine()) != null) {

				// Here is where you should implement code to parse the packets
				// from the engine and act on it.
				System.out.println(input);

				String[] words = input.split(" ");


				if ("NEWHAND".compareToIgnoreCase(words[0])==0) {
					button = Boolean.getBoolean(words[2]);
					for(int i=0;i<4;i++){
						hand[i] = getCardFromString(words[3+i]);
					}
					myBank = Integer.parseInt(words[7]);
					otherBank = Integer.parseInt(words[8]);
				}
				if ("GETACTION".compareToIgnoreCase(words[0]) == 0) {
					// When appropriate, reply to the engine with a legal
					// action.
					// The engine will ignore all spurious packets you send.
					// The engine will also check/fold for you if you return an
					// illegal action.
					int pot_size = Integer.parseInt(words[1]);
					int num_board_cards = Integer.parseInt(words[2]);
					Card[] board_cards = new Card[num_board_cards];
					//Get the cards on the board, if there are any
					int index = 3;
					if(num_board_cards!=0){
						for(int i=0;i<num_board_cards;i++){
							board_cards[i] = getCardFromString(words[index+i]);
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
							if(action.contains("RAISE:*:")){
								//more than one raise option
								for(i=6;i<action.length();i+=2){
									actions.add("RAISE:"+action.charAt(i));
								}
							}
							else{
								actions.add(action);
							}
						}
					}
					Random r = new Random();
					int result = r.nextInt(actions.size());
					outStream.println(actions.get(result));
				} else if ("REQUESTKEYVALUES".compareToIgnoreCase(words[0]) == 0) {
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
