package com.termOne;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class PokerstoveLoader {

	public void loadPokerstove() throws IOException {
		ProcessBuilder pb = new ProcessBuilder("src/lexparser.sh", "myArg1", "myArg2");
		Process p = pb.start();
		BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
		String line = null;
		while ((line = reader.readLine()) != null)
		{
			System.out.println(line);
		}
	}

}
