import com.termOne.Board;
import com.termOne.Hand;

public class DictionaryBuilder extends Thread implements Runnable {
	//	public static HashMap<String, Float> dictionary = new HashMap<String, Float>();

	public DictionaryBuilder() {

	}

	@Override
	public void run() {
		//		BufferedReader buff;
		int counter = 0;
		long time = 0;
		//			buff = new BufferedReader(new FileReader(new File("input.txt")));
		String line;
		time = System.currentTimeMillis();
		while((line = ThreadManager.getNextLine()) != null) {
//			if(counter%100 == 0) {
//				System.out.println(counter);
//				System.out.println((System.currentTimeMillis() - time) / 1000.);
//			}
			Hand _hand = new Hand(line.substring(0, line.indexOf(":")));
			Board b = new Board();
			float maxConfidence = 0;
			for(String s : _hand.permute2CardHands())
				maxConfidence = Math.max(maxConfidence, b.getConfidenceFromBoard( s.toLowerCase(), ""));
			System.out.println("Max Confidence: " + maxConfidence);
			ThreadManager.db.insertRow(_hand.toString(), maxConfidence);
			counter++;
		}

	}

}
