import java.util.*;

import com.mysql.jdbc.StringUtils;

import cosinesim.*;


public class TopUtils {

	public static void generateWeightVectorsForLexiconAndAddToDatabase()
	{
		int count = 1740; 
		DatabaseWrapper d = new DatabaseWrapper();
		ArrayList<String> lexiconWords = d.GetRestOfWordsFromLexicon();

		for (String lexiconWord : lexiconWords)
		{
			System.out.println(">>>>> starting : " + lexiconWord + "\t id: "+ count);
			HashMap<String,Float> h = utils.calculateLexiconTFIDFVectorsForWord(lexiconWord);

			System.out.println("built vector for " + lexiconWord);
			//d.WriteWeightVector(h, lexiconWord);
			BasicUtils.addWordToFinishedWordsInLexicon("\n"+lexiconWord);
			System.out.println(lexiconWord + "added to database" );
			count ++ ; 
		}
	}

	public static void generateCosineSimVectors()
	{
		DatabaseWrapper d = new DatabaseWrapper();
		//get remained items didn't get relation between them all items in the lexicon
		ArrayList<String> remainedLexiconWords = d.getRemainedLexiconWordsForSimGraph();
		int countRemained = remainedLexiconWords.size();
		System.out.println("items remained : " + countRemained);

		//get all weight vector for all lexicon words
		HashMap<String, WeightVector>  allWeightVectorHash = d.getAllWeightVectors(80);
		System.out.println("weight vectors built");
		HashMap<Map.Entry<String, String>,Float> cosSim = new HashMap<Map.Entry<String,String>, Float>();

		for(String key :remainedLexiconWords)
		{
			HashMap<String, Float> graph = new HashMap<String, Float>();

			for(String innerkey :allWeightVectorHash.keySet())
			{
				if(!cosSim.containsKey(new AbstractMap.SimpleEntry<String,String>(innerkey,key))) {

					WeightVector w1 = allWeightVectorHash.get(innerkey);
					WeightVector w2 = allWeightVectorHash.get(key);

					float weight = TDFutil.Sim(w1, w2);
					cosSim.put(new AbstractMap.SimpleEntry<String,String>(key,innerkey), weight);

					//System.out.println(key +" \t "+ innerkey +" \t  "+  weight);

					graph.put(innerkey, weight);

				}
			}

			d.addToSimilarityGraph(key, graph);
			System.out.println(key);
			
			System.out.println("items Remained: "+ --countRemained);

		}

	}	

	public static void generateWeightVectorsForLexiconAndAddToDatabaseRemoveOdd()
	{ 
		DatabaseWrapper d = new DatabaseWrapper();
		ArrayList<String> lexiconWords = d.GetRestOfWordsFromLexicon();
		HashMap<String,HashMap<String,Float>> allWeightVectors = new HashMap<String, HashMap<String,Float>>();
		for (String lexiconWord : lexiconWords)
		{
			HashMap<String,Float> h = utils.calculateLexiconTFIDFVectorsForWord(lexiconWord);
			System.out.println("built vector for "+ lexiconWord);
			allWeightVectors.put(lexiconWord, h);	
		}

		System.out.println("starting cleaning vectors \n ---------------------------- ");
		allWeightVectors = utils.cleanWeightVectors(allWeightVectors);
		
		System.out.println("starting adding to database \n ---------------------------- ");
		d.WriteWeightVector(allWeightVectors);
		
	}

	//throw_away code convert an existing table containing weight vectors into new table 
	//with removal of weight vector words that occurs in vectors of both +ve and -ve lexicon words  
	public static void convertWeightVectorTableToCleanTable()
	{
		DatabaseWrapper d = new DatabaseWrapper();
		HashMap<String,HashMap<String,Float>> allWeightVectors = d.getAllWeightVectorsWithOddRemoval();
		
		System.out.println("finished building unique weight vectors \n ------------------------");
		
		d.WriteWeightVector(allWeightVectors);
	}
}
