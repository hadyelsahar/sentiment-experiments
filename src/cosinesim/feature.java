package cosinesim;


import java.io.*;

public class feature implements Serializable {

	public String  word;
	public float  weight;


	public feature(String u, float w) {
		this.word = u;
		weight = w;
	}

	public void setWeight(float w) {
		weight = w;
	}

	public float getWeight() {
		return weight;
	}
	public boolean equals(Object o) {
		  	System.err.println("innnnn");
			if (!(o instanceof feature)) return false;
			feature k = (feature) o;
			if(k.word.equals(word)) {
				System.err.println("found it");
				return true;
			}
			return false;
	}



}

