package cosinesim;
//
//
// WeightVector

//
//


import java.util.*;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;


public class WeightVector implements Serializable {

	Hashtable features = new Hashtable();
	public String url;
	public String className;

	public WeightVector(int len, String url) {
		this.url = url;
		features = new Hashtable(len);
	}

	public WeightVector(Vector features, String url) {
		int len = features.size();
		this.url = url;
		//System.out.println("len: " + len);
		for(int i=0; i< len; i++) {
			feature f= (feature)features.elementAt(i);
			this.features.put(f.word, f);
		}

	}

	public WeightVector() {
		// TODO Auto-generated constructor stub
	}

	public Hashtable getFeatures() {
		return features;
	}

	public boolean setWeight(float weight, String term) {
		feature f = (feature)features.get(term);
		if(f == null )return false;
		f.setWeight(weight);
		return true;
	}

	public float getWieghtFor(String term) {
		feature f = (feature)features.get(term);
		if(f == null ) return 0;
		return f.weight ;
	}

	public void addVal(String term, float w) {
		feature f = (feature)features.get(term);
		if(f != null )return;
		f = new feature(term, w);
		features.put(term, f);
	}

	public void addVal(feature f) {
		features.put(f.word, f);
	}

	public String[] getKeys() {
		
		int len = features.size();
		if(len < 1) return null;
		String[] result = new String[len];
		Enumeration e = features.keys() ;
		for(int i= 0; i< len; i++)
			result[i] = (String)e.nextElement();
		return result;

	}

	public int getLength() {
		return features.size();
	}
	
	public void addVector(WeightVector v) {
		for (Enumeration e = v.features.elements() ; e.hasMoreElements() ;) {
			feature f = (feature)e.nextElement();
			if(features.containsKey(f.word)) {
				feature f2 = (feature)features.get(f.word);
				f2.weight = (f.weight + f2.weight)/2;
			}
			else addVal(f);

		}
	}
	public String encode(String input) {

		//	System.out.println("input: " + input);
		String enc = " ";
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


	public void printKeys() {
		Vector l = new Vector();
		for (Enumeration e = features.elements() ; e.hasMoreElements() ;)
		{
			l.addElement(e.nextElement());
		}
	//	(Vector)features.values();
		Collections.sort(l,new featureComparator());
		for (int i= 0; i< l.size(); i++ ) {
			feature f = (feature)l.elementAt(i);
			System.out.println("(" +encode(f.word)+ ", " + getWieghtFor(f.word)+ "), ");

		}
		//System.out.println(l);

		/*for (Enumeration e = features.keys() ; e.hasMoreElements() ;) {
			String k = (String)e.nextElement();
			System.out.print(k+ ", " + getWieghtFor(k));
		}*/
		System.out.println();
	}
}

