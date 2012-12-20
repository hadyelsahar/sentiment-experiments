import java.awt.List;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;



public class BasicUtils {

	public static <T extends Comparable> ArrayList<String> sortHashMap(HashMap<String,T> h)
	{
		final HashMap<String,T> h1 = h;
		ArrayList<String> words = new ArrayList<String>(h.keySet());

		Collections.sort(words,new Comparator<String>(){

			public int compare(String w1 , String w2)
			{
				return h1.get(w2).compareTo(h1.get(w1));
			}
		});
		return words;
	}
	
	public static ArrayList<String> getArabicStopWords()
	{
		String stopWordsString = readFromFile("./ArabicStopWords.txt");
		String [] foo  = stopWordsString.split("\n");
		ArrayList<String> stopWords =  new ArrayList (Arrays.asList(foo));
		ArrayList<String> cleanStopWords = new ArrayList<String>(); 
		//remove stopwords who are empty due to extra new lines in the file and trim each one
		for (String word : stopWords)
		{
			if(word.length() > 0)
			{
			   cleanStopWords.add(word);
			}
		}
		return cleanStopWords;
		
	}
	
	public static ArrayList<String> GetDoneWordsFromLexiconFile(){
		
		String WordsString = readFromFile("./finished_words.txt");
		String [] foo  = WordsString.split("\n");
		ArrayList<String> Words =  new ArrayList (Arrays.asList(foo));
		ArrayList<String> cleanWords = new ArrayList<String>(); 
		//remove stopwords who are empty due to extra new lines in the file and trim each one
		for (String word : Words)
		{
			if(word.length() > 0)
			{
			   cleanWords.add(word);
			}
		}
		return cleanWords;
	}
	
	public static void addWordToFinishedWordsInLexicon(String word)
	{
		writeToFile("./finished_words.txt", word);
		
	}
	
			
	public static String readFromFile (String aFileName)
	{
		BufferedReader reader = null ; 
		String fileContent = "" ;
		InputStream in = BasicUtils.class.getResourceAsStream(aFileName);

		try {

			String sCurrentLine;

			reader=new BufferedReader(new InputStreamReader(in));

			while ((sCurrentLine = reader.readLine()) != null) {
				fileContent += sCurrentLine + "\n";
			}
			fileContent= fileContent.substring(0, fileContent.length()-1);

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (reader != null)reader.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return fileContent;
	}
	
	public static void writeToFile (String aFileName,String text)
	{

		try {
			
			URL url = BasicUtils.class.getResource(aFileName);
			File file = new File(url.toURI());
			
			FileWriter fw = new FileWriter(file.getAbsoluteFile(),true);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(text);
			bw.close();


		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} 
		
	}
	
	
}