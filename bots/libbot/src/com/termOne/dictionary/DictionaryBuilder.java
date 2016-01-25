package com.termOne.dictionary;

import java.io.File;

import com.termOne.Board;
import com.termOne.Hand;

public class DictionaryBuilder extends Thread implements Runnable {

	public DictionaryBuilder() {}

	@Override
	public void run() {
		long time = 0;
		String line;
		time = System.currentTimeMillis();
		for(int counter = 0; (line = ThreadManager.getNextLine()) != null; counter++) {
			if(counter%200 == 0) {
				System.out.println(counter);
				System.out.println((System.currentTimeMillis() - time) / 1000.);
				time = System.currentTimeMillis();
			}
			Hand _hand = new Hand(line.substring(0, line.indexOf(":")));
			Board b = new Board();
			float maxConfidence = 0;
			for(String s : _hand.permute2CardHands())
				maxConfidence = Math.max(maxConfidence, b.getConfidenceFromBoard( s.toLowerCase(), ""));
			System.out.println("Max Confidence: " + maxConfidence);
			ThreadManager.db.insertRow(_hand.toString(), maxConfidence);
		}
	}
	
	@SuppressWarnings("unused")
	public static void main(String[] args) {
		ThreadManager t = new ThreadManager(new File("input.txt"), 12);
	}
}
