package cosinesim;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.UnsupportedEncodingException;
import java.util.*;

public class TDFutil {

	public static String getWord(feature inF)
	{
		return inF.word;
	}

	public static String[] getTopNKeys(int len,WeightVector profile ) {
		//returns the highest n keys specified in the parameter

		Vector info = new Vector(profile.features.values());
		Collections.sort(info ,new featureComparator());

		int no = info.size();
		int idx = (no < len) ? no : len;
		String result[] = new String[idx];
		for (int i= 0; i< idx; i++ ) {
			feature f = (feature)info.elementAt(i);
			result[i] = f.word + ", " + f.weight;
		}
		return result;

	}

	public static WeightVector trimToLength(int len,WeightVector profile ) {
		//returns the highest n keys specified in the parameter

		Vector info = new Vector(profile.features.values());
		int no = info.size();
		if(no < len) return new WeightVector(info, profile.url);;
		Collections.sort(info ,new featureComparator());
		Vector result = new Vector(len);
		for (int i= 0; i< len; i++ ) {
			result.addElement(info.elementAt(i));
		}
		return new WeightVector(result, profile.url);

	}
	public static void Normalize(WeightVector v) {
		Hashtable p1 = v.getFeatures();
		float sum= 0;

		for (Enumeration e = p1.elements() ; e.hasMoreElements() ;) {
			feature f = (feature)e.nextElement();
			sum = sum + f.weight;
		}
		sum = (float)Math.sqrt(sum);
		for (Enumeration e = p1.elements() ; e.hasMoreElements() ;) {
			feature f = (feature)e.nextElement();
			f.setWeight(f.weight/sum);
			//System.out.println("Norm Key" + f.word +", w:" + f.weight);
		}

	}

	public static void Normalize(WeightVector v, float cutOff) {
		Hashtable p1 = v.getFeatures();
		float sum= 0;

		for (Enumeration e = p1.elements() ; e.hasMoreElements() ;) {
			feature f = (feature)e.nextElement();
			sum = sum + f.weight;
		}
		sum = (float)Math.sqrt(sum);
		for (Enumeration e = p1.elements() ; e.hasMoreElements() ;) {
			feature f = (feature)e.nextElement();
			f.setWeight(f.weight/sum);
			//System.out.println("Norm Key" + f.word +", w:" + f.weight);
			if(f.weight < cutOff) p1.remove(f.word);
		}

	}

	public static void Norm2(WeightVector v, float cutOff) {
		Hashtable p1 = v.getFeatures();
		float max= 0;

		for (Enumeration e = p1.elements() ; e.hasMoreElements() ;) {
			feature f = (feature)e.nextElement();
			if(f.weight>max) max = f.weight;
		}
		for (Enumeration e = p1.elements() ; e.hasMoreElements() ;) {
			feature f = (feature)e.nextElement();
			f.setWeight(f.weight/max);
			System.out.println("Norm Key" + f.word +", w:" + f.weight);
			if(f.weight < cutOff) p1.remove(f.word);
		}

	}
	public static WeightVector Norm2(WeightVector v) {
			Hashtable p1 = v.getFeatures();
			float max= 0;

			for (Enumeration e = p1.elements() ; e.hasMoreElements() ;) {
				feature f = (feature)e.nextElement();
				if(f.weight>max) max = f.weight;
			}
			for (Enumeration e = p1.elements() ; e.hasMoreElements() ;) {
				feature f = (feature)e.nextElement();
				f.setWeight(f.weight/max);
				//System.out.println("Norm Key" + f.word +", w:" + f.weight);
			}
		 return v;
		}


	public static WeightVector addProfiles(WeightVector pro1, WeightVector pro2, int len) {
		Hashtable profiles = new Hashtable();

		Hashtable p1 = pro1.getFeatures();

		//copy the contents of the 1st profile into
		for (Enumeration e = p1.elements() ; e.hasMoreElements() ;) {
			feature f = (feature)e.nextElement();
			System.out.println("word: " + f.word);
			profiles.put(f.word, new feature(f.word, f.weight));
		}

		p1 = pro2.getFeatures();
		for (Enumeration e = p1.keys() ; e.hasMoreElements() ;) {
			String key = (String)e.nextElement();
			feature f = (feature)p1.get(key);
			System.out.println("found: " + f.word + ", " + f.weight);
			if(profiles.containsKey(key)){
				feature term = (feature)profiles.get(key);
				if(term.weight < f.weight)
					term.setWeight((f.getWeight() ) * ((float)1.7) );
				else term.setWeight((term.getWeight()) * (float)1.7);
				//term.setWeight((f.getWeight() + term.getWeight()) * ((float)1.5) );
			}else
				profiles.put(f.word, new feature(f.word, f.weight));

		}

		WeightVector result = new WeightVector(profiles.size(), "");
		for (Enumeration e = profiles.elements() ; e.hasMoreElements() ;) {
			 feature f = (feature)e.nextElement();
			 result.addVal(f);
			 //System.out.println(f.word + ">>"+f.weight);
		}
	//	Normalize(result);
		if((len < 1) ||  (profiles.size() < len))
			return result;
		return(trimToLength(len,result));


	}
	public static Set getIntersection(WeightVector pro1, WeightVector pro2) {

		if(pro1==null || pro2 == null)
			return new HashSet();

		if(pro1.getKeys()== null || pro2.getKeys()== null)
			return new HashSet();

		Set h1  =  new HashSet(Arrays.asList(pro1.getKeys()));
		Set h2  =  new HashSet(Arrays.asList(pro2.getKeys()));
		h1.retainAll(h2);
		//System.out.println("h1:" +h1);
		return h1;
	}

	public static float Sim(WeightVector pro1, WeightVector pro2) {
		float dotProd = 0;
		float crossProd1 = 0;
		float crossProd2 = 0;


		long t1 = System.currentTimeMillis();

		Vector seen = new Vector();
		/*pro1.printKeys();
		System.out.println("--------------");

		pro2.printKeys();*/
		int inter = getIntersection(pro1,pro2).size();

		if( inter < 1)
			return 0;

		/*float boost = 1f;

		if(inter == 1) boost = 0.5f;
		else if(inter >1) boost = 2f;*/


		for (Enumeration e = pro1.features.keys() ; e.hasMoreElements() ;) {
			String term = (String)e.nextElement();
			seen.addElement(term);
			float w1 = pro1.getWieghtFor(term);
			float w2 = pro2.getWieghtFor(term);

			dotProd = dotProd + (w1*w2);
			crossProd1 = crossProd1 + (w1*w1);
			crossProd2 = crossProd2 + (w2*w2);
		}
		for (Enumeration e = pro2.features.keys() ; e.hasMoreElements() ;) {
			String term = (String)e.nextElement();
			if(!seen.contains(term) ){
				float w1 = pro1.getWieghtFor(term);
				float w2 = pro2.getWieghtFor(term);
				dotProd = dotProd + (w1*w2);
				crossProd1 = crossProd1 + (w1*w1);
				crossProd2 = crossProd2 + (w2*w2);
			}
		}

		double crossP = crossProd1*crossProd2;
		crossP = Math.sqrt(crossP);

		float result = (float)(dotProd/crossP) ;
		long t2 = System.currentTimeMillis();
		long ttime =  t2-t1;
		//System.out.println("done calc in : " + Long.toString(ttime) + "ms");
		//System.err.println("result: " + result);
		return result;

	}


	public static String Sim2(WeightVector pro1, WeightVector pro2) {
		float dotProd = 0;
		float crossProd1 = 0;
		float crossProd2 = 0;


		long t1 = System.currentTimeMillis();

		Vector seen = new Vector();
		/*pro1.printKeys();
		System.out.println("--------------");

		pro2.printKeys();*/

		Set set = getIntersection(pro1,pro2);
		int inter = set.size();

		if( inter < 1)
			return "0";

		float boost = 1f;

		if(inter == 1) boost = 0.5f;
		else if(inter >1) boost = 2f;
		//if(getIntersection(pro1,pro2).size() < 1)
		//	return "0";



		for (Enumeration e = pro1.features.keys() ; e.hasMoreElements() ;) {
			String term = (String)e.nextElement();
			seen.addElement(term);
			float w1 = pro1.getWieghtFor(term);
			float w2 = pro2.getWieghtFor(term);

			dotProd = dotProd + (w1*w2);
			crossProd1 = crossProd1 + (w1*w1);
			crossProd2 = crossProd2 + (w2*w2);
		}
		for (Enumeration e = pro2.features.keys() ; e.hasMoreElements() ;) {
			String term = (String)e.nextElement();
			if(!seen.contains(term) ){
				float w1 = pro1.getWieghtFor(term);
				float w2 = pro2.getWieghtFor(term);
				dotProd = dotProd + (w1*w2);
				crossProd1 = crossProd1 + (w1*w1);
				crossProd2 = crossProd2 + (w2*w2);
			}
		}

		double crossP = crossProd1*crossProd2;
		crossP = Math.sqrt(crossP);

		float result = (float)(dotProd/crossP)*boost;
		long t2 = System.currentTimeMillis();
		long ttime =  t2-t1;
		//System.out.println("done calc in : " + Long.toString(ttime) + "ms");
		//System.out.println("result: " + result);

		Iterator it = set.iterator();
		String elements = "";
		while (it.hasNext()) {
		    // Get element
		     elements = elements +", "+ encode(it.next().toString());
		}
		return String.valueOf(result)+"," + elements;

	}

private static String decode(String input) {

	 InputStreamReader temp = new InputStreamReader(System.in);
     String enc = "";
    	 enc=temp.getEncoding();
		try {
		if(enc.equalsIgnoreCase("cp1256")){
			return input;
	  }

			byte[] mod = input.getBytes("cp1252");
			return  new String(mod, "cp1256");
		}catch (UnsupportedEncodingException e) {
			  e.printStackTrace(); {
		}
		}
		return " ";

	}
public static  String encode(String input) {

	//	System.out.println("input: " + input);
	 InputStreamReader temp = new InputStreamReader(System.in);
     String enc = "";
    	 enc=temp.getEncoding();
		try {
			if(enc.equalsIgnoreCase("cp1256")) {
				return input;
				 }

			byte[] mod = input.getBytes("cp1256");
			return new String(mod, "cp1252");
		}catch (UnsupportedEncodingException e) {
			  e.printStackTrace(); {
		}
		}
		return " ";

	}

	public void printHashTable(Hashtable map)
	{
	Enumeration keys = map.keys();
    Enumeration elements=map.elements();

    while(keys. hasMoreElements() && elements.hasMoreElements() )
    {
    	System.out.println(keys.nextElement()+", "+elements.nextElement());
    }
}
	public Hashtable loadTerms(String filename) throws IOException, ClassNotFoundException {
  		FileInputStream f = null;
  		InputStream in  = null;
  		ObjectInputStream  is;

		f = new FileInputStream(filename);
  		is  = new  ObjectInputStream(f);
  		Hashtable idfs	=  new Hashtable();


  		try {
  			int adocsNo	= ((Integer)is.readObject()).intValue();
  			System.err.println("Arabic Docs used: " + adocsNo);
  			System.err.println("loading terms");
  			Hashtable terms	= (Hashtable)is.readObject();


  			is.close();
  			f.close();

  			Enumeration keys = terms.keys();
  		    Enumeration elements=terms.elements();

  		    while(keys. hasMoreElements() && elements.hasMoreElements() )
  		    {
  		    	String key = keys.nextElement().toString();
  		    	float idf=computeIDF(terms, adocsNo, key);
  		    	idfs.put(key,idf);
  		    }


  		} catch (IOException e) {
  			System.err.println("IOException:  "+ e) ;
  		}

  		return idfs;
  	}

	public float computeIDF(Hashtable terms, int docsNo, Object key)
	{
		float termCount = 1;
		if(terms.containsKey(key))
		{
			termCount= ((Integer)terms.get(key)).floatValue();
		}

		float idf =  (float)(Math.log(docsNo/termCount)/Math.log(10));
		//System.out.println("Term: "+ key.toString() + "IDF :" + idf);
		return idf;
	}

	public static void main (String argv[]) throws IOException, ClassNotFoundException
	{
	TDFutil test = new TDFutil();
	test.loadTerms("NU.pro");
	}
}

