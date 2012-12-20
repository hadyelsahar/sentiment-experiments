import java.awt.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import cosinesim.TDFutil;
import cosinesim.WeightVector;
import cosinesim.WeightVectorBuilder;

public class main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		
		//TopUtils.generateWeightVectorsForLexiconAndAddToDatabase();
		
		//TopUtils.generateWeightVectorsForLexiconAndAddToDatabaseRemoveOdd();
		
//		DatabaseWrapper d = new DatabaseWrapper();
//		d.getAllWeightVectorsWithOddRemoval();
//		
		TopUtils.generateCosineSimVectors();
		
		
		
		

		//TopUtils.convertWeightVectorTableToCleanTable();
		
		
		//Test new getAllWeightVectors(int limit) with limit
//		DatabaseWrapper d = new DatabaseWrapper();
//		HashMap<String, WeightVector> x = d.getAllWeightVectors(40);
//		
//		for(String i : x.keySet())
//		{
//			System.out.println(i + "\t" + x.get(i).getLength());
//			
//		}
		

//		SolrWrapper wr = new SolrWrapper();
//		ArrayList<String> tweets =  wr.getTweetsByKeyword("");
//     	///testing building lexicon weight vectors
////		//HashMap<String,Float> h = utils.calculateLexiconTFIDFVectorsForWord("");
//		HashMap<String,Float> h = utils.calculateLexiconCountVectorsForWord("");
//		ArrayList<String> words = BasicUtils.sortHashMap(h);
//		
//		for (int i =0 ; i< 500 ; i ++)
//		{
//			String w = words.get(i);
//			Float tfidf = h.get(w);
//			System.out.println( w +"\t"+ tfidf);
//		}
	
		
		
//testing getting IDf 
//		
//		SolrWrapper wr = new SolrWrapper();
//		float x = wr.getDocumentFrequency("");
//		System.out.println(x);
//
//		 
		
//Testing Method utils.getWindow
		
//		String s = "1b 2b 3b is 1a 2a 3a 4a 5a 6a";
//		String kw = "is";
//		
//		System.out.print(utils.getWindow(s, kw));
//		
		
		
//Testing Getting tweets from solr
//		SolrWrapper sw = new SolrWrapper();
//		ArrayList<String> list = sw.getTweetsByKeyword("جميل");
//		list = utils.cleanTweet(list);
//		list = utils.getWindow(list, "جميل" );
//		list = utils.removeDuplicateTweets(list);
//		String text = StringUtils.join(list,'\n');
//		BasicUtils.writeToFile("temp.txt",text );
		
		
//TEsting Getting cosine similarity 
//		cosinesim.WeightVectorBuilder builder = new WeightVectorBuilder();
//		cosinesim.WeightVector wv1 = builder.getTFIDFfor("الظن مرض يقتل شي", "1");
//		cosinesim.WeightVector wv2 = builder.getTFIDFfor("الظن مرض يقتل مآ", "2");
//		
//		System.out.println(TDFutil.Sim(wv1, wv2));
//		

	}

}
