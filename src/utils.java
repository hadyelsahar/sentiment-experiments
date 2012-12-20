import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;

import cosinesim.TDFutil;
import cosinesim.WeightVectorBuilder;


public class utils {
	private static final int leftWindowSize = 3;
	private static final int rightWindowSize = 3;


	/**
	 * for each word in the lexicon , search the tweets and grap the words in the window
	 * generate the vector of words 
	 * find weight of each word in the vector 
	 * add the vector of words and weight to the database 
	 */
	public static HashMap<String, Float>  calculateLexiconTFIDFVectorsForWord(String searchKeyWord)
	{
		SolrWrapper solrWrapper = new SolrWrapper();
		HashMap<String, Float> wordVectorCount = calculateLexiconCountVectorsForWord(searchKeyWord);

		//replacing the TF (count) with the TF-IDF  TF/DocFreq
		Set<Entry<String, Float>> set = wordVectorCount.entrySet();
		for(Map.Entry<String, Float> entry :set)
		{
			Float idf = solrWrapper.getidf(entry.getKey());
			Float tfidf = entry.getValue() * idf;
			wordVectorCount.put(entry.getKey(), tfidf);
		}

		//calculating the weight vector
		return wordVectorCount;
	}


	/**
	 * for each word in the lexicon , search the tweets and grap the words in the window
	 * generate the vector of words 
	 * find weight of each word in the vector 
	 * add the vector of words and weight to the database 
	 */
	public static HashMap<String, Float>  calculateLexiconCountVectorsForWord(String searchKeyWord)
	{
		SolrWrapper solrWrapper = new SolrWrapper();
		ArrayList<String> tweets =  solrWrapper.getTweetsByKeyword(searchKeyWord);

		HashMap<String, Float> wordVectorCount = new HashMap<String, Float>();

		tweets = cleanTweet(tweets);
		tweets = getWindow(tweets, searchKeyWord);
		tweets = removeDuplicateTweets(tweets);


		for (String cleanTweet : tweets)
		{
			String[] tweetWords = cleanTweet.split(" ");

			//counting repetitions of words in the vector
			for (String tweetWord : tweetWords)
			{

				if (wordVectorCount.containsKey(tweetWord))
				{
					Float val = wordVectorCount.get(tweetWord);
					wordVectorCount.put(tweetWord, val+1 );
				}
				else
				{
					wordVectorCount.put(tweetWord, new Float(1) );
				}
			}
		}

		//calculating the weight vector

		return wordVectorCount;	
	}

	/**
	 * 
	 * @param string contains tweet text
	 * 
	 * @return tweet text after extracting only arabic words and remove everything else
	 */
	public static String cleanTweet(String tweet)
	{
		//remove puncituations
		tweet = tweet.replaceAll("[ًٌٍَُِْ]", "");

		//arabic characters only	
		Pattern p = Pattern.compile("[ذدجحخهعغفقثصضطكمنتالبيسشظزوةىلارؤءئألألآآلإإ]+");
		Matcher m = p.matcher(tweet);
		String cleanTweet = "" ; 
		while (m.find())
		{
			//exclude those whose length <= 1 
			String token = tweet.substring(m.start(), m.end());
			if (token.length() > 1)
			{
				cleanTweet += (token +" ");
			}
		}
		if(cleanTweet.length() > 1)
		{
			cleanTweet = cleanTweet.substring(0, cleanTweet.length()-1);
		}
		//remove stop words
		ArrayList<String> stopWords = BasicUtils.getArabicStopWords();

		for(String stopWord :stopWords)
		{
			String Regx = "( "+stopWord+"$)|(^"+stopWord+" )"+"|( "+stopWord+" )";
			cleanTweet = cleanTweet.replaceAll(Regx, " ");
		}


		cleanTweet = cleanTweet.replaceAll(" +", " ");
		return cleanTweet;
	}

	public static ArrayList<String> cleanTweet(ArrayList<String> tweets)
	{	
		ArrayList<String> cleanTweets = new ArrayList<String>();
		for(String tweet : tweets)
		{
			String cleanTweet = cleanTweet(tweet);
			cleanTweets.add(cleanTweet);
		}
		return cleanTweets;
	}

	/**
	 * 
	 * @param tweet string contains the tweet text
	 * @param kw akeyword in the text
	 * @return string contains the window in the tweet around this keyword  
	 */
	public static String getWindow(String tweet,String kw)
	{
		List<String> tweetWords = (List<String>) Arrays.asList(tweet.split(" "));
		String[] keywordWords = kw.split(" ");

		int beforeKwIndexEnd = tweetWords.indexOf(keywordWords[0]) -1;
		int beforeKwIndexStart = beforeKwIndexEnd-leftWindowSize;


		int afterKwIndexStart = tweetWords.indexOf(keywordWords[keywordWords.length-1])+1;
		int afterKwIndexEnd = afterKwIndexStart + rightWindowSize;


		//calculating before words
		String before ="";
		if(beforeKwIndexEnd < 0 || beforeKwIndexStart == beforeKwIndexEnd)	
		{
			before = "";
		}
		else
		{
			if (beforeKwIndexStart < 0)
			{
				beforeKwIndexStart = 0;
			}
			List<String> beforeKwWords = tweetWords.subList(beforeKwIndexStart, beforeKwIndexEnd+1);
			before = StringUtils.join(beforeKwWords.toArray(), ' ');
		}

		//calculating After words
		int lastIndex = tweetWords.size()-1;
		String after ="";
		if(afterKwIndexStart > lastIndex || afterKwIndexStart == afterKwIndexEnd)	
		{
			after = "";
		}
		else
		{
			if (afterKwIndexEnd > lastIndex+1)
			{
				afterKwIndexEnd = lastIndex+1;
			}
			List<String> afterKwWords = tweetWords.subList(afterKwIndexStart, afterKwIndexEnd);
			after =  StringUtils.join(afterKwWords.toArray(), ' ');
		}


		return before +" "+ after;
	}

	public static ArrayList<String> getWindow(ArrayList<String> tweets,String kw)
	{
		ArrayList<String> cleanWindows = new ArrayList<String>();
		for(String tweet : tweets)
		{
			String window = getWindow(tweet,kw);
			cleanWindows.add(window);
		}
		return cleanWindows;
	}

	/**
	 * 
	 * @param tweets ArrayList of tweets may contain Duplicates 
	 * @return return ArrayList of unique tweets based on cosine similarity
	 */
	public static ArrayList<String> removeDuplicateTweets(ArrayList<String>tweets)
	{
		cosinesim.WeightVectorBuilder builder = new WeightVectorBuilder();
		ArrayList<String> uniqueTweets = new ArrayList<String>();
		for(int i=0 ; i < tweets.size() ; i++)
		{
			boolean hasSimilar = false ; 
			for(int j= i+1 ; j <tweets.size() ; j++)
			{
				cosinesim.WeightVector wv1 = builder.getTFIDFfor(tweets.get(i), "1");
				cosinesim.WeightVector wv2 = builder.getTFIDFfor(tweets.get(j), "2");		
				Float sim = TDFutil.Sim(wv1, wv2);
				if(sim > 0.70)
				{
					hasSimilar = true;
					break;
				}
			}

			if(!hasSimilar)
			{
				uniqueTweets.add(tweets.get(i));
			}
		}

		return uniqueTweets ;
	}

	
	/**
	 * returns the same weight vectors with removal of words that occur in both positive and negative words
	 * @param weightVectors
	 * @return cleanWeightVectors free from words that occur in both weight vectors of positive and negative lexicon words 
	 */
	public static HashMap<String,HashMap<String,Float>> cleanWeightVectors(HashMap<String, HashMap<String,Float>> weightVectors)
	{
		DatabaseWrapper d = new DatabaseWrapper();

		HashMap<String,HashMap<String,Float>>  cleanWeightVectors = new HashMap<String, HashMap<String,Float>>(weightVectors);
		HashMap<String,Boolean> lexiconWords = new HashMap<String, Boolean>();

		HashSet<String> positiveWeightWords = new HashSet<String>();
		HashSet<String> negativeWeightWords = new HashSet<String>();

		//getting all words that occur in positive and negtive vectors
		for(String lexiconWord : weightVectors.keySet())
		{
			if(lexiconWords.get(lexiconWord) == true)
			{
				positiveWeightWords.addAll(weightVectors.get(lexiconWord).keySet());
			}
			else
			{
				negativeWeightWords.addAll(weightVectors.get(lexiconWord).keySet());
			}
		}
		
		
		//check if a positive word occur in 
		for(String lexiconWord : weightVectors.keySet())
		{
			if(lexiconWords.get(lexiconWord) == true)  //positive lexicon word
			{
				for(String weightWord :weightVectors.get(lexiconWord).keySet())
				{
					if(negativeWeightWords.contains(weightWord))
					{
						cleanWeightVectors.get(lexiconWord).remove(weightWord);
					}
				}
			}
			else //negative lexicon word
			{
				for(String weightWord :weightVectors.get(lexiconWord).keySet())
				{	
					if(positiveWeightWords.contains(weightWord))
					{
						cleanWeightVectors.get(lexiconWord).remove(weightWord);
					}
				}
			}
			
			
			
		}
		





		return cleanWeightVectors;
	}




}

