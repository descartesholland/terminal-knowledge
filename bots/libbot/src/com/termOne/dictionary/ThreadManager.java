package com.termOne.dictionary;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import com.termOne.DatabaseConnector;

public class ThreadManager {
	public static DatabaseConnector db = new DatabaseConnector(true);
	private static BufferedReader reader;
	private ArrayList<Thread> threads;
	private static int counter = 0;
	
	public ThreadManager(File f, int numThreads) {
		try {
			reader = new BufferedReader(new FileReader(f));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		threads = new ArrayList<Thread>();
		for(int i = 0; i < numThreads; i++) {
			this.addThread();
		}
		

	}
	
	public static synchronized String getNextLine() {
		try {
			counter++; 
			if(counter%200 == 0)
				getProgress();
			return reader.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		db.shutdown();
		return null;
	}
	
	public void addThread() {
		this.threads.add((Thread) new DictionaryBuilder());
		this.threads.get(threads.size() -1 ).start();
	}
	
	public static void getProgress() {
		System.out.println(counter / (16432.)*100.);
	}
}
