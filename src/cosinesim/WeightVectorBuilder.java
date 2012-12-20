package cosinesim;
import java.io.*;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;


public class WeightVectorBuilder {

	Hashtable terms = new Hashtable();
	int docsNo =2;							//total number of vectors in collection
	int profileLen = 15;
	boolean debug = true;

	public WeightVectorBuilder() {
	   //	load("Arfil.pro");
	}

	public void load(String filename) {
		FileInputStream f;
		ObjectInputStream  is;

		long t1 = System.currentTimeMillis();
		try {
			f = new FileInputStream(filename);
			is  = new  ObjectInputStream(f);
			try {
				docsNo	= ((Integer)is.readObject()).intValue();
				System.err.println("Doc used: " + docsNo);
				System.out.println("loading terms");
				terms	= (Hashtable)is.readObject();
			} catch (ClassNotFoundException e) {
				System.err.println("ClassNotFoundException:  "+ e) ;
			}
			is.close();
			f.close();

		} catch (IOException e) {
			System.err.println("IOException:  "+ e) ;
		}
		long t2 = System.currentTimeMillis();
		long ttime =  (t2-t1)/60000;
		System.out.println("done loading in: " + Long.toString(ttime) + "mins");
	}

	public WeightVector getTFIDFfor(String Doc, String id) {
		Hashtable<String,Integer> keys = new Hashtable<String,Integer>();
		Vector fvect = new Vector();
		// write code that populates hashtable and counts here
		String[] words = Doc.split(" ");
		
		//counting repetitions of words in the vector
		for (String word : words)
		{
			if (keys.containsKey(word))
			{
				int val = keys.get(word);
				keys.put(word, val+1 );
			}
			else
			{
				keys.put(word, 1 );
			}
		}

		for(Map.Entry<String, Integer> ent :keys.entrySet()) {
			String key = ent.getKey();
			int count = ent.getValue();
			
			float termCount = 1;
			
			if(terms.containsKey(key))
				termCount = ((Integer)terms.get(key)).floatValue();
			
			float idf = (float)(Math.log(docsNo/termCount)/Math.log(10));
			float nfr = count/(float)(words.length);
			float w = idf*nfr;
			feature f = new feature(key, w);
			fvect.add(f);
		}

		
		WeightVector profile = new WeightVector(fvect, id);
		return profile;


	}


}