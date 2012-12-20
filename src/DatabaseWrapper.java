/**
 * @author hady
 * 
 * this class used as a wrapper for database for example getting lexicon words
 *
 */

import java.awt.List;
import java.util.*;
import java.sql.*;
import javax.sql.*;
import javax.swing.text.Position;

import cosinesim.*;

import org.apache.commons.lang3.StringUtils;

public class DatabaseWrapper {

	private final String dbURL = "jdbc:mysql://localhost/twitter_db?";
	private final String dbuser = "root";
	private final String dbpassword = "";

	private Connection con;
	private Statement statement;

	/**
	 * function constructor
	 */
	public DatabaseWrapper ()
	{ 
		try {

			Class.forName("com.mysql.jdbc.Driver");
			String unicode= "useUnicode=yes&characterEncoding=UTF-8";
			con = DriverManager.getConnection (dbURL+unicode + "&user="+dbuser+"&password="+dbpassword);
			statement = con.createStatement();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * function used to get just all words in the lexicon table 
	 * @return list of all words in the lexicon table
	 */
	public ArrayList<String> GetAllWordsFromLexicon()
	{
		ArrayList<String> result = new ArrayList<String>();

		try 
		{
			String query = "select text from lexicon";
			ResultSet resultset = statement.executeQuery(query);

			while(resultset.next())
			{
				String s = resultset.getString("text");
				result.add(s);
			}

			return result;
		}
		catch (SQLException e)
		{
			System.out.println("error in querying from database");
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * function used to get all words in the lexicon table with the polarity
	 * @return list of all words in the lexicon table
	 */
	public HashMap<String,Boolean> GetAllWordsFromLexiconWithPolarity()
	{
		HashMap<String,Boolean> result = new HashMap<String, Boolean>();

		try 
		{
			String query = "select text,polarity from lexicon";
			ResultSet resultset = statement.executeQuery(query);

			while(resultset.next())
			{
				String s = resultset.getString("text");
				boolean isPositive = resultset.getString("polarity").equals("pos");
				result.put(s,isPositive);
			}

			return result;
		}
		catch (SQLException e)
		{
			System.out.println("error in querying from database");
			e.printStackTrace();
			return null;
		}

	}

	/**
	 * for building weight vectors this function gets the rest of lexicon words in order resume
	 * after any sudden stop 
	 * @return array string containing list of words
	 */
	public ArrayList<String> GetRestOfWordsFromLexicon()
	{

		ArrayList<String> result = new ArrayList<String>();

		try 
		{
			ArrayList<String> doneWords = BasicUtils.GetDoneWordsFromLexiconFile();
			String query= "SELECT `text` FROM `lexicon` WHERE text not in (\""+ StringUtils.join(doneWords,"\",\"") +"\")";
			ResultSet resultset = statement.executeQuery(query);

			while(resultset.next())
			{
				String s = resultset.getString("text");
				result.add(s);
			}

			return result;
		}
		catch (SQLException e)
		{
			System.out.println("error in querying from database");
			e.printStackTrace();
			return null;
		}

	}


	public HashSet<String> GetPolarWeightWords(boolean ispositive , String weightVectorsTableName,String lexiconTableName)
	{
		HashSet<String> results = new HashSet<String>();

		String polarity = ispositive?"pos":"neg";

		try {

			//String Query = "SELECT Distinct("+weightVectorsTableName+".`word`) FROM `"+weightVectorsTableName+"` inner join "+lexiconTableName+" on "+weightVectorsTableName+".lexiconword_id = "+lexiconTableName+".id where "+lexiconTableName+".polarity = '"+polarity+"'"; 
			String Query = "SELECT Distinct("+weightVectorsTableName+".`word`) FROM `"+weightVectorsTableName+"` inner join "+lexiconTableName+" on "+weightVectorsTableName+".lexiconword_id = "+lexiconTableName+".id where "+lexiconTableName+".polarity = '"+polarity+"'";
			ResultSet resultSet = statement.executeQuery(Query);

			while(resultSet.next())
			{
				String word = resultSet.getString("word");
				results.add(word);
			}

			return results;
		}
		catch(SQLException e){

			System.out.println("error in querying from database");
			e.printStackTrace();
			return null;
		}
	}

	public HashMap<String,HashMap<String,Float>> getAllWeightVectorsWithOddRemoval()
	{
		HashMap<String,HashMap<String,Float>> allWeightVectors = new HashMap<String, HashMap<String,Float>>();
		HashSet<String> positiveWeightWords  = GetPolarWeightWords(true, "weightvector", "lexicon");
		System.out.println("retrieved +ve bag of words of size " + positiveWeightWords.size());
		HashSet<String> negativeWeightWords  = GetPolarWeightWords(false, "weightvector", "lexicon");
		System.out.println("retrieved -ve bag of words of size " + negativeWeightWords.size());
	
		try {

			String query= "SELECT lexicon.text , lexicon.polarity ,weightvector.word , weightvector.weight FROM `weightvector` join lexicon on weightvector.lexiconword_id = lexicon.id";
			ResultSet resultset = statement.executeQuery(query);

			int count = 1 ; //max = 3,400,000 monitoring to remove 
			while(resultset.next())
			{
				String lexiconWord = resultset.getString("text");
				boolean polarity = (resultset.getString("polarity") == "pos");
				String weightWord = resultset.getString("word");
				float weight = Float.parseFloat(resultset.getString("weight"));

				if(( negativeWeightWords.contains(weightWord) && !positiveWeightWords.contains(weightWord))){
					//System.out.println(lexiconWord +"\t" + weightWord +" positive");

					if(allWeightVectors.containsKey(lexiconWord))
					{
						HashMap<String, Float> h = allWeightVectors.get(lexiconWord);
						h.put(weightWord, weight);
						allWeightVectors.put(lexiconWord, h);
					}
					else
					{
						HashMap<String, Float> h = new HashMap<String, Float>();
						h.put(weightWord, weight);
						allWeightVectors.put(lexiconWord, h);
					}	
				}
				else if (positiveWeightWords.contains(weightWord) && !negativeWeightWords.contains(weightWord))
				{	
					if(allWeightVectors.containsKey(lexiconWord))
					{
						HashMap<String, Float> h = allWeightVectors.get(lexiconWord);
						h.put(weightWord, weight);
						allWeightVectors.put(lexiconWord, h);
					}
					else
					{
						HashMap<String, Float> h = new HashMap<String, Float>();
						h.put(weightWord, weight);
						allWeightVectors.put(lexiconWord, h);
					}	
				}
				
				System.out.println(count-- + " remaining" );
			}

			return allWeightVectors;
		}
		catch (SQLException e)
		{
			System.out.println("error in querying from database");
			e.printStackTrace();
			return null;
		}

	}


	/**
	 * function used to add weight vectors to the database
	 */
	//	public void WriteWeightVector(HashMap<String, Float> h , String lexiconWord)
	//	{
	//		ArrayList<String> result = new ArrayList<String>();
	//
	//		try 
	//		{
	//			String Query  = "INSERT INTO `twitter_db`.`weightvectors` (`word`, `weight`, `lexicon_word`) VALUES";
	//			for (String key : h.keySet())
	//			{
	//				if (key.length() > 0 &&  h.get(key)> 0 )
	//					Query += "('"+key+"','"+h.get(key)+"','"+lexiconWord+"'),";
	//			}
	//
	//			Query = Query.substring(0, Query.length()-1);	
	//			System.out.println(Query);
	//			statement.executeUpdate(Query);
	//		}
	//		catch (SQLException e)
	//		{
	//			System.out.println("error in adding weight vectors to database");
	//			System.out.println( "lexiconWord" + lexiconWord );
	//			e.printStackTrace();
	//		}
	//
	//	}

	public void WriteWeightVector(HashMap<String,HashMap<String, Float>> h)
	{
		int remainitems  = h.size();
		

		String Querybase  = "INSERT INTO `twitter_db`.`cleanweightvectors` (`word`, `weight`, `lexicon_word`) VALUES ";
		String Query = "";

		try {
			int count = 0 ; 
			for (String lexiconWord : h.keySet())
			{
				
				HashMap<String, Float> weightVector = h.get(lexiconWord);
				for (String key : weightVector.keySet())
				{
					Query += "('"+key+"','"+weightVector.get(key)+"','"+lexiconWord+"'),";
					
					count ++;
					if (count > 10000)
					{
						System.out.println("executing Query ...");
						Query = Query.substring(0, Query.length()-1);
						statement.executeUpdate(Querybase + Query);
						System.out.println("Done executing");
						System.out.println(remainitems--);
						Query = "";
						count = 0 ; 	
					}
				}
				
			}
			
			Query = Query.substring(0, Query.length()-1);
			statement.executeUpdate(Querybase + Query);
			
		}
		catch (SQLException e)
		{
			System.out.println("error in adding weight vectors to database");
			e.printStackTrace();
		}


	}

	public HashMap<String, Float>  getWeightVector(String lexiconWord)
	{
		HashMap<String, Float> result = new HashMap<String, Float>();

		try 
		{
			String query = "select weightvector.word,weightvector.weight from weightvector join lexicon on lexicon.id = weightvector.lexiconword_id where lexicon.text like \""+ lexiconWord +"\"";
			ResultSet resultset = statement.executeQuery(query);

			while(resultset.next())
			{
				String s = resultset.getString("word");
				String w = resultset.getString("weight");
				float weight = Float.parseFloat(w);
				result.put(s, weight);
			}

			return result;
		}
		catch (SQLException e)
		{
			System.out.println("error in querying from database");
			e.printStackTrace();
			return null;
		}

	}

	public HashMap<String, WeightVector> getAllWeightVectors(int limit)
	{
		//if limit == 0 means no limit  
		HashMap<String, WeightVector> result = new HashMap<String, WeightVector> ();

		try 
		{
			//String query = "select lexicon.text,weightvector.word,weightvector.weight from weightvector join lexicon on lexicon.id = weightvector.lexiconword_id order by weightvector.weight desc";
			String query = "select cleanweightvectors.word,cleanweightvectors.weight,cleanweightvectors.lexicon_word from cleanweightvectors order by cleanweightvectors.weight desc";
			ResultSet resultset = statement.executeQuery(query);

			while(resultset.next())
			{
				String lexiconWord = resultset.getString("lexicon_word");
				String w = resultset.getString("weight");				
				float weight = Float.parseFloat(w);
				String word = resultset.getString("word");

				if(result.containsKey(lexiconWord))
				{
					WeightVector h = result.get(lexiconWord);

					if(h.getLength()<limit && limit!=0)
					{
						feature f = new feature(word, weight);
						h.addVal(f);
					}
				}
				else 
				{
					Vector<feature> h = new Vector<feature>();
					feature f = new feature(word, weight);
					h.add(f);
					WeightVector weightVector = new WeightVector(h, lexiconWord);
					result.put(lexiconWord, weightVector);
				}
			}

			return result;
		}
		catch (SQLException e)
		{
			System.out.println("error in querying from database");
			e.printStackTrace();
			return null;
		}
	}

	public void addToSimilarityGraph(String s1 , String s2 , float Weight)
	{
		String Query = "INSERT INTO `twitter_db`.`simGraph` (`word1`, `word2`, `weight`) VALUES ('"+s1+"', '"+s2+"', '"+Weight+"');";

		try 
		{
			statement.executeUpdate(Query);
		}
		catch (SQLException e)
		{
			System.out.println("error in adding to database");
			System.out.println( s1 + "\t"+ s2  );
			e.printStackTrace();
		}

	}

	public void addToSimilarityGraph(String s1 , HashMap<String, Float> graph)
	{		
		String Query = "INSERT INTO `twitter_db`.`simGraph3` (`word1`, `word2`, `weight`) VALUES ";
		for(String innerKey :graph.keySet())
		{
			float weight = graph.get(innerKey); 
			Query += "('"+s1+"', '"+innerKey+"', '"+weight+"'),";
		}

		Query = Query.substring(0,Query.length()-1);

		try 
		{
			statement.executeUpdate(Query);
		}
		catch (SQLException e)
		{
			System.out.println("error in adding to database");
			System.out.println(s1);
			e.printStackTrace();
		}	

	}

	public ArrayList<String> getRemainedLexiconWordsForSimGraph ()
	{
		ArrayList<String> resultDone = new ArrayList<String>();
		ArrayList<String> result = new ArrayList<String>();

		try 
		{
			String QueryDone = "SELECT Distinct(word1) FROM `simGraph3`";
			ResultSet resultsetDone = statement.executeQuery(QueryDone);

			while(resultsetDone.next())
			{
				String s = resultsetDone.getString("word1");
				resultDone.add(s);
			}

			String query= "SELECT `text` FROM `lexicon` WHERE text not in (\""+ StringUtils.join(resultDone,"\",\"") +"\")";
			ResultSet resultset = statement.executeQuery(query);

			while(resultset.next())
			{
				String s = resultset.getString("text");
				result.add(s);
			}

			return result;
		}
		catch (SQLException e)
		{
			System.out.println("error in querying from database");
			e.printStackTrace();
			return null;
		}



	}





}
